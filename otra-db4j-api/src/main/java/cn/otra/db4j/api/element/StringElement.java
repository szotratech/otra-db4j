package cn.otra.db4j.api.element;

import cn.otra.db4j.api.ctx.BuilderContext;

public class StringElement implements SqlElement {
	private static final long serialVersionUID = 8436229229441417799L;
	private String value;
	private String []arrays;
	public StringElement(String value) {
		this.value = value;
	}
	
	public StringElement(String... arrays) {
		this.arrays = arrays;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String[] getArrays() {
		return arrays;
	}

	public void setArrays(String[] arrays) {
		this.arrays = arrays;
	}
	
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {

		if(value != null) {
			builder.append(value);
		} else {
            for(int i=0;i<arrays.length;i++) {
            	builder.append(arrays[i]);
                if(i<arrays.length - 1) {
                	builder.append(",");
                }
            }
		}
	}
//	
//	@Override
//	public String toString() {
//        StringBuilder buffer = new StringBuilder();
//
//		if(value != null) {
//			return value;
//		} else {
//            for(int i=0;i<arrays.length;i++) {
//                buffer.append(arrays[i]);
//                if(i<arrays.length - 1) {
//                    buffer.append(",");
//                }
//            }
//		}
//		return buffer.toString();
//	}
}
