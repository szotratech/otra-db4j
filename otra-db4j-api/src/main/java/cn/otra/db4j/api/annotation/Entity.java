package cn.otra.db4j.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.otra.db4j.api.table.Table;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

	Class<? extends Table> table();

	String catalog() default "";

	String schema() default "";

}