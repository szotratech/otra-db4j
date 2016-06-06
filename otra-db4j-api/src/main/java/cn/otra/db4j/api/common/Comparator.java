package cn.otra.db4j.api.common;

public enum Comparator {
	GE(">="),
	GT(">"),
	EQ("="),
	NE("!="),
	LT("<"),
	LE("<="),
	NOT_EQ("!="),
	LIKE("LIKE"),
	IS_NULL("IS NULL"),
	IS_NOT_NULL("IS NOT NULL");
	
	private final String value;
	private Comparator(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
