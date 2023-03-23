package com.atTao.java1;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/2/22 17:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, MODULE})
public @interface MyAnnotations {

    MyAnnotation[] value();

}
