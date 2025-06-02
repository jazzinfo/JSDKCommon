package idv.jazz.dto;
import idv.jazz.utility.ChineseOrBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class News {

	 @NotNull
	 private String title;
	 
	 @Size(max=100)
	 private String author;
	 
	 @Size(max=20)
     private String baokan;
     
	 @Pattern(regexp = "\\d{8}", message = "日期格式必須為 yyyyMMdd")
     private String pubdate;
     
	 @Size(max=10)
     private String banci;
	 
	 @Size(max=100)
     private String banming;
	 
	 @Size(max=100)
     private String jhuanlan;
	 
	 @ChineseOrBlank
     private String baodaodi;
     
	 @NotNull
     @Size(max=50)
     private String ftimg;     
}

