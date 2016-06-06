package cn.otra.db4j.api.po;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.otra.db4j.api.util.ReflectUtil;

public abstract class BasePo implements Serializable {

	private static final long serialVersionUID = 1411684130026149861L;

	@Override
	public String toString() {
		try {
			Class<?> clazz = this.getClass();
			StringBuilder sb = new StringBuilder();
			Field[] fs = ReflectUtil.getFields(this.getClass());
			sb.append(clazz.getSimpleName()).append(":[");
			for (Field f : fs) {
				f.setAccessible(true);
				if (!Modifier.isStatic(f.getModifiers())) {
					Object fv = f.get(this);
					if(fv instanceof Date) {
						Date dateVal = (Date)fv;
						sb.append(f.getName()).append(":").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateVal)).append(",");
					} else {
						sb.append(f.getName()).append(":").append(fv).append(",");
					}
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			return sb.toString();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public abstract Class<?> getTableClass();
	
}
