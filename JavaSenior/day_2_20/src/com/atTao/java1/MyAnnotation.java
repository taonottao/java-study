package com.atTao.java1;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/2/21 22:41
 */
@Inherited
@Repeatable(MyAnnotations.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, MODULE,TYPE_PARAMETER,TYPE_USE})
public @interface MyAnnotation {

    String value()default "hello";
}
