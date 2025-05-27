package idv.jazz;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import idv.jazz.dto.NewsDto;

public class ReadArticleWithEncodingDetect {

    private static final String START_MARK = "標    題:";
    private static final String END_MARK = "-----------------------------------------";
    private static final Charset BIG5 = Charset.forName("Big5");

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\TEMP\\199601.txt");

        Charset detectedCharset;
        long startOffset = 0;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            // BOM 判斷
            byte[] first3 = new byte[3];
            raf.read(first3);
            if ((first3[0] & 0xFF) == 0xEF && (first3[1] & 0xFF) == 0xBB && (first3[2] & 0xFF) == 0xBF) {
                detectedCharset = StandardCharsets.UTF_8;
                startOffset = 3;
            } else if ((first3[0] & 0xFF) == 0xFF && (first3[1] & 0xFF) == 0xFE) {
                detectedCharset = StandardCharsets.UTF_16LE;
                startOffset = 2;
            } else {
                // 嘗試用 UTF-8 讀取部分內容，偵測是否為 BIG5
                detectedCharset = detectUtf8OrBig5(file);
                startOffset = 0;
            }
        }

        System.out.println("偵測到編碼: " + detectedCharset.displayName());

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(startOffset); //依上面編碼將讀檔位置往後調整

            String line;
            StringBuilder article = null;
            NewsDto newsDto = null;
            while ((line = customReadLine(raf, detectedCharset)) != null) {
            	
            	// recordBoxed(line, article) ;
                if (line.startsWith(START_MARK)) {
                    article = new StringBuilder();
                    article.append(line).append("\n");
                } else if (line.equals(END_MARK)) {
                    if (article != null) {
                        article.append(line).append("\n");
                      //  System.out.println("【讀取一篇文章】\n" + article);
                      //  System.out.println("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
                        recordShow(article);
                        article = null;
                    }
                } else if (article != null) {
                    article.append(line).append("\n");
                }
                
            }
        }
    }

    private static void recordShow( StringBuilder article) throws IOException {

          System.out.println( article);
          System.out.println("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");

    }
    
    
    private static Charset detectUtf8OrBig5(File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            bis.mark(1000);
            byte[] sample = new byte[512];
            int len = bis.read(sample);
            bis.reset();
            String s = new String(sample, 0, len, StandardCharsets.UTF_8);
            if (s.contains("�") || !s.contains("標")) {
                // 出現 � 字符或抓不到「標」 → 當作 BIG5
                return BIG5;
            } else {
                return StandardCharsets.UTF_8;
            }
        }
    }

    private static String customReadLine(RandomAccessFile raf, Charset charset) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        if (charset.equals(StandardCharsets.UTF_16LE)) {
            int b1, b2;
            // UTF-16LE：每個字元兩個 byte -> 即每次讀取一個字
            while ((b1 = raf.read()) != -1 && (b2 = raf.read()) != -1) {
                if (b1 == 0x0D && b2 == 0x00) { // '\r'
                    long pos = raf.getFilePointer();
                    int next1 = raf.read();
                    int next2 = raf.read();
                    if (!(next1 == 0x0A && next2 == 0x00)) { // 如果不是 '\n'
                        raf.seek(pos);
                    }
                    break; //讀到斷行 /r 中斷
                } else if (b1 == 0x0A && b2 == 0x00) { // '\n'
                    break; //讀到斷行 /n 中斷
                }
                buffer.write(b1);
                buffer.write(b2); // 一次寫入2 Bytes [即一個字]
            }
        } else {
        	// BIG 5
            int b;
            while ((b = raf.read()) != -1) {
                if (b == '\n') break;
                if (b == '\r') {
                    long pos = raf.getFilePointer();
                    int next = raf.read();
                    if (next != '\n' && next != -1) {
                        raf.seek(pos);
                    }
                    break;
                }
                buffer.write(b);
            }
        }

        if (buffer.size() == 0) return null;
        return new String(buffer.toByteArray(), charset); //以編碼 utf-16le 轉為 Byte Array 
    }
}

