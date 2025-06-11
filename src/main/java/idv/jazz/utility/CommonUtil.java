package idv.jazz.utility;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import idv.jazz.dto.News;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class CommonUtil {

    private static final Charset BIG5 = Charset.forName("Big5");
    
	public CommonUtil() {
		super();
	}
	
    public static List<Path> getSrcFileList(String srcPath){
        Path directoryPath = Paths.get(srcPath);

        try {
            List<Path> filePaths = Files.walk(directoryPath)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            for (Path filePath : filePaths) {
                System.out.println(filePath.toAbsolutePath());
            }
            return filePaths;
        } catch (IOException e) {
            System.err.println("Error accessing directory: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean doValidData(List<News> newsList) {
        ValidatorFactory factory = Validation
        	    .byDefaultProvider()
        	    .configure()
        	    .messageInterpolator(new ParameterMessageInterpolator())
        	    .buildValidatorFactory();
        	Validator validator = factory.getValidator();
        	
        // ✅ 加入驗證
        for (News news : newsList) {
            Set<ConstraintViolation<News>> violations = validator.validate(news);
            if (!violations.isEmpty()) {
                System.out.println("資料驗證失敗：");
                for (ConstraintViolation<News> violation : violations) {
                    System.out.println("欄位：" + violation.getPropertyPath() +
                                       "，錯誤：" + violation.getMessage() +
                                       "，值：" + violation.getInvalidValue());
                    printOneNews( violation.getRootBean() );
                }
                // 可以選擇：拋出例外、中止流程、或略過這筆
               // throw new RuntimeException("資料驗證錯誤");
                return false;
            }
        }  
        return true;
    }

    public static Charset detectCharset(File file) throws IOException {
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

    public static Charset detectUtf8OrBig5(File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            bis.mark(1000);
            byte[] sample = new byte[512];
            int len = bis.read(sample);
            bis.reset();

            String content = new String(sample, 0, len, StandardCharsets.UTF_8);
            return (content.contains("�") || !content.contains("標")) ? BIG5 : StandardCharsets.UTF_8;
        }
    }

    public static List<News> parseNewsFromFile(File file, String startMark, String endMark, Charset charset, String baokan) throws IOException {
        List<News> newsList = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long offset = (charset.equals(StandardCharsets.UTF_8)) ? 3 :
                          (charset.equals(StandardCharsets.UTF_16LE)) ? 2 : 0;
            raf.seek(offset);

            String line;
            List<String> block = null;
            //int counter=0;
            while ((line = readLineWithCharset(raf, charset)) != null) {
                if (line.startsWith(startMark)) {
                    block = new ArrayList<>();
                    block.add(line);
                    /*
                    ++counter;
                    System.out.println( counter  + "\t" + line.length() + "\t" + line);
                    if( line.length()>100 || line.length()==0) {
                    	  break;
                    }
                    */
                } else if (line.equals(endMark) && block != null) {
                    block.add(line);
                    parseLinesToDto( newsList, block, baokan);
                    block = null;
                } else if (block != null) {
                    block.add(line);
                }
            }
        }

        return newsList;
    }
    
    private static List<News> parseLinesToDto(List<News> newsList, List<String> block, String baokan) {
        if( baokan.equals("CDNS")) {
            newsList.add(NewsParser.parseLinesToDto(block));
        }else  if( baokan.equals("CNNS")) {
            newsList.add(CNNEWSParser.parseLinesToDto(block));
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
    
    private static void printOneNews(News item) {
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
    
    @SuppressWarnings("unused")
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
