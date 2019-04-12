package org.springframework.cloud.config.server.ssh;

import org.springframework.validation.annotation.Validated;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Beans annotated with {@link PrivateKeyValidator} and {@link Validated} will have the constraints applied.
*
* @author Ollie Hughes
*/
@Constraint(validatedBy = PrivateKeyValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivateKeyIsValid {
	String message() default "{PrivateKeyIsValid.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
