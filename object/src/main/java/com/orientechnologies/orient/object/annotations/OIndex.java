package com.orientechnologies.orient.object.annotations;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;

import java.lang.annotation.*;

/**
 * @author Sander Postma (s.postma@magsoft.nl)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OIndex
{
    String name() default "";
    OClass.INDEX_TYPE indexType();
//    OType keyType() default OType.INHERITED;   Can't implement keyType yet
}
