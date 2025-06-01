package idv.jazz.dto;


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
	
	 @Size(max=500, min=1)
	 private String title;
	 
	 @Size(max=50, min=0)
	 private String author;
	 
	 @Size(max=20, min=0)
     private String baokan;
     
	 @Size(max=10, min=0)
     private String pubdate;
     
	 @Size(max=10, min=0)
     private String banci;
	 
	 @Size(max=50, min=0)
     private String banming;
	 
	 @Size(max=50, min=0)
     private String jhuanlan;
	 
	 @Size(max=50, min=0)
     private String baodaodi;
     
     @Size(max=50, min=0)
     private String ftimg;     
}
