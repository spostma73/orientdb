package com.orientechnologies.orient.object.annotations;

import java.lang.annotation.*;

/**
 * @author Sander Postma (s.postma@magsoft.nl)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OAttributes
{
    String min() default "";

    String max() default "";

    boolean mandatory() default false;

    boolean notNull() default false;

    String regExp() default "";

    boolean readOnly() default false;

    boolean collateCaseInsensitive() default false;
}
