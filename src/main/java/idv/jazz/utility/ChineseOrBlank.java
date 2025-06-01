package idv.jazz.utility;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ChineseOrBlankValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ChineseOrBlank {
    String message() default "必須為中文或空值";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

