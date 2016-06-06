package cn.otra.db4j.api.table;

import cn.otra.db4j.api.condition.AddCondition;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;

/**
 * 可加减操作的字段
 * @author ecxiaodx
 *
 * @param <T>
 */
public interface AddAbleTableField<T extends Number> extends TableField<T> {
	/**
	 * 加或减(value为负时是减)
	 * @param value
	 * @return
	 */
	AddCondition<T> add(T value);
	/**
	 * 乘value
	 * @param value 被乘数
	 * @return
	 */
	AddCondition<T> mul(T value);
	/**
	 * 相除
	 * @param value 被除数
	 * @return
	 */
	AddCondition<T> div(T value);
	void appendTo(StringBuilder builder, BuilderContext builderContext,T value,Keyword operation,boolean enableAlias);
}
