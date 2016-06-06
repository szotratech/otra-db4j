package cn.otra.db4j.utils.generator;


public class TableMetaDataField {
	private String field;
	private String type;
	private String key;
	private String comment;
	private String extra;
	//addtion
	private String javaType;
	private String javaField;
	private Object defaultVal;
	private boolean isPK;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
		
		this.javaField = MetaUtil.getJavaStandField(field);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.javaType = MetaUtil.getJavaType(type);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getJavaField() {
		return javaField;
	}

	public String getJavaType() {
		return javaType;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public Object getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(Object defaultVal) {
		this.defaultVal = defaultVal;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public void setJavaField(String javaField) {
		this.javaField = javaField;
	}

	public boolean isPK() {
		return isPK;
	}

	public void setPK(boolean isPK) {
		this.isPK = isPK;
	}

}
