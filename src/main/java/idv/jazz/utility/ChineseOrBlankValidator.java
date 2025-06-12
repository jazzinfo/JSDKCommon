package idv.jazz.utility;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChineseOrBlankValidator implements ConstraintValidator<ChineseOrBlank, String> {
 
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 允許為 null 或空字串
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // 判斷是否為中文（含全形中文字）只要包含任意一個指定字元
        return value.matches(".*[\\u4e00-\\u9fa5Ａ-Ｚａ-ｚ０-９A-Za-z0-9；;，：:].*");
    }
}


