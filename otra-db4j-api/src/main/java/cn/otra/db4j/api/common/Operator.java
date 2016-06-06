package cn.otra.db4j.api.common;


public enum Operator {
	AND(" and "),
	OR(" or ");
	
	private final String value;
	private Operator(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
