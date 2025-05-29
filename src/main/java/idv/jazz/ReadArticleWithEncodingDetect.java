package idv.jazz;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import idv.jazz.dto.News;
import idv.jazz.service.NewsService;
import idv.jazz.utility.NewsParser;

public class ReadArticleWithEncodingDetect {

    private static final String START_MARK = "標    題:";
    private static final String END_MARK = "-----------------------------------------";
    private static final Charset BIG5 = Charset.forName("Big5");

    public static void main(String[] args) {
       	NewsService service = new NewsService();
        File file = new File("C:\\TEMP\\199601.txt");

        try {
            Charset charset = detectCharset(file);
            System.out.println("偵測到編碼: " + charset.displayName());

            List<News> newsList = parseNewsFromFile(file, charset);
           // printNews(newsList);
            service.insertBatch(newsList);
            System.out.println("批量插入成功！");

        } catch (IOException e) {
            System.err.println("讀取檔案時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Charset detectCharset(File file) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            byte[] bom = new byte[3];
            raf.read(bom);

            if (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF) {
                return StandardCharsets.UTF_8;
            } else if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE) {
                return StandardCharsets.UTF_16LE;
            }
        }

        return detectUtf8OrBig5(file);
    }

    private static Charset detectUtf8OrBig5(File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            bis.mark(1000);
            byte[] sample = new byte[512];
            int len = bis.read(sample);
            bis.reset();

            String content = new String(sample, 0, len, StandardCharsets.UTF_8);
            return (content.contains("�") || !content.contains("標")) ? BIG5 : StandardCharsets.UTF_8;
        }
    }

    private static List<News> parseNewsFromFile(File file, Charset charset) throws IOException {
        List<News> newsList = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long offset = (charset.equals(StandardCharsets.UTF_8)) ? 3 :
                          (charset.equals(StandardCharsets.UTF_16LE)) ? 2 : 0;
            raf.seek(offset);

            String line;
            List<String> block = null;

            while ((line = readLineWithCharset(raf, charset)) != null) {
                if (line.startsWith(START_MARK)) {
                    block = new ArrayList<>();
                    block.add(line);
                } else if (line.equals(END_MARK) && block != null) {
                    block.add(line);
                    newsList.add(NewsParser.parseLinesToDto(block));
                    block = null;
                } else if (block != null) {
                    block.add(line);
                }
            }
        }

        return newsList;
    }

    private static String readLineWithCharset(RandomAccessFile raf, Charset charset) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        if (charset.equals(StandardCharsets.UTF_16LE)) {
            int b1, b2;
            while ((b1 = raf.read()) != -1 && (b2 = raf.read()) != -1) {
                if (isLineBreakUtf16(b1, b2, raf)) break;
                buffer.write(b1);
                buffer.write(b2);
            }
        } else {
            int b;
            while ((b = raf.read()) != -1) {
                if (b == '\n') break;
                if (b == '\r') {
                    long pos = raf.getFilePointer();
                    int next = raf.read();
                    if (next != '\n' && next != -1) raf.seek(pos);
                    break;
                }
                buffer.write(b);
            }
        }

        return (buffer.size() == 0) ? null : new String(buffer.toByteArray(), charset);
    }

    private static boolean isLineBreakUtf16(int b1, int b2, RandomAccessFile raf) throws IOException {
        if ((b1 == 0x0D && b2 == 0x00) || (b1 == 0x0A && b2 == 0x00)) {
            if (b1 == 0x0D) {
                long pos = raf.getFilePointer();
                int next1 = raf.read();
                int next2 = raf.read();
                if (!(next1 == 0x0A && next2 == 0x00)) {
                    raf.seek(pos);
                }
            }
            return true;
        }
        return false;
    }

    private static void printNews(List<News> newsList) {
        for (News item : newsList) {
            System.out.println(item.getTitle());
            System.out.println(item.getAuthor());
            System.out.println(item.getBaokan());
            System.out.println(item.getPubdate());
            System.out.println(item.getBanci() );
            System.out.println(item.getBanming());
            System.out.println(item.getJhuanlan());
            System.out.println(item.getBaodaodi());
            System.out.println(item.getFtimg());
            System.out.println("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
        }
    }

}


