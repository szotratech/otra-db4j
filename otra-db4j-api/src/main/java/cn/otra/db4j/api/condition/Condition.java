package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.common.Operator;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.SqlElement;


public abstract class Condition implements SqlElement {
	
	private int startTag;
	private int endTag;
	private Condition next;
	private Condition prev;
	private Condition tail;//条件的最后一个，用于条件拼接
	private Operator operator;
	
	protected String subSql;
	
	private BuilderContext builderContext;
	
//	public void reset() {
//		startTag = 0;
//		endTag = 0;
//		next = null;
//		prev = null;
//		tail = null;
//		operator = null;
//		subSql = null;
//		builderContext = null;
//	}
	
//	public static final Condition ALL = new AllCondition();
	
	public static final Condition getInstance() {
		return new AllCondition();
	}
	
	/**
	 * 如果condition==null,就直接返回原值，方便数据拼接
	 * @param condition
	 * @return
	 */
	public Condition and(Condition condition) {
		if(condition == null) {
			return this;
		}
		return this.chain(condition,Operator.AND);
	}
	
	public Condition or(Condition condition) {
		if(condition == null) {
			return this;
		}
		return this.chain(condition,Operator.OR);
	}
	
//	public Condition exists(EndBuilder endBuilder) {
//		
//	}
//	
//	public Condition notExists(EndBuilder endBuilder) {
//		
//	}
	
	public BuilderContext getBuilderContext() {
		return builderContext;
	}
	
	public void setBuilderContext(BuilderContext builderContext) {
		this.builderContext = builderContext;
	}
	
	public int getStartTag() {
		return startTag;
	}

	public void setStartTag(int startTag) {
		this.startTag = startTag;
	}

	public int getEndTag() {
		return endTag;
	}

	public void setEndTag(int endTag) {
		this.endTag = endTag;
	}

	protected Condition chain(Condition nextCondition,Operator operator) {
		if(nextCondition instanceof AllCondition) {
			throw new RuntimeException("AllCondtion not support.");
		}
		Condition head = nextCondition.head();
		if(this.tail != null) {
			this.tail.next = head;
			head.prev = this.tail;
		} else {
			this.next = head;
			head.prev = this;
		}
		
		head.operator = operator;
		Condition nextConditionTail = nextCondition.tail();
		
		if(head.next() != null) {//more than one condition
			head.startTag ++;
			nextConditionTail.endTag ++;
		}
		Condition thisHead = this.head();
		thisHead.tail = nextConditionTail;
		this.tail = nextConditionTail;
//		System.err.println("tail="+nextCondition);
		return thisHead.tail;
	}
	
	
	public Operator getOperator() {
		return operator;
	}
	
	public Condition next() {
		return next;
	}
	
	public Condition prev() {
		return prev;
	}
	
	/**
	 * return the head of the link list
	 * @return
	 */
	public Condition head() {
		if(this.prev == null) {
			return this;
		}
		Condition head = this;
		Condition tmp = null;
		while((tmp = head.prev()) != null) {
			head = tmp;
		}
		return head;
	}
	
	public Condition tail() {
		if(this.next == null) {
			return this;
		}
		Condition tail = this;
		Condition tmp = null;
		while((tmp = tail.next()) != null) {
			tail = tmp;
		}
		return tail;
	}
	
//	public void allToBuilder(StringBuilder builder) {
//		Condition head = this.head();
//		head.appendTo(builder, null);
//		Condition cond = head;
//		while((cond = cond.next) != null) {
//			cond.appendTo(builder, null);
//		}
//	}
	
	/**
	 * 获取对象的真实sql值，还sql格式
	 * 如字符串要加上引号
	 * @param value
	 * @return
	 */
//	protected Object getRealValue(Object value) {
//		
//		if(value instanceof String) {
//			return "'"+value+"'";
//		}
//		if(value instanceof Boolean) {
//			return ((Boolean)value)?1:0;
//		}
//		if(value instanceof Date) {
//			return "'"+DateUtils.dateToString((Date)value, TimeFormatter.FORMATTER1)+"'";
//		}
//		
//		return value;
//	}
	
	/**
	 * 获取参数值
	 * @return
	 */
	public abstract Object[] getParamValue();
}
