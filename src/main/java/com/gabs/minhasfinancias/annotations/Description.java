package com.gabs.minhasfinancias.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/*
 INDICATES SOME DESCRIPTION TO A PART OF THE PROJECT
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.LOCAL_VARIABLE,
ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.CONSTRUCTOR, ElementType.TYPE_USE})
@Retention(RetentionPolicy.SOURCE)
public @interface Description {
    String value() default "";
}
