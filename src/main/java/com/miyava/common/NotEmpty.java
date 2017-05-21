package com.miyava.common;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Documented
@NotNull
@Size( min = 1 )
@ReportAsSingleViolation
@Constraint( validatedBy = NotEmpty.NotEmptyValidator.class )
@Target( { METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER } )
@Retention( RUNTIME )
public @interface NotEmpty {

    String message() default "common.messages.error.notempty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target( { METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER } )
    @Retention( RUNTIME )
    @Documented
    @interface List {

        NotEmpty[] value();
    }

    class NotEmptyValidator
        implements ConstraintValidator<NotEmpty, String> {

        public void initialize( NotEmpty constraintAnnotation ) {}

        public boolean isValid( String value, ConstraintValidatorContext context ) {
            return value.length() > 0;
        }
    }
}
