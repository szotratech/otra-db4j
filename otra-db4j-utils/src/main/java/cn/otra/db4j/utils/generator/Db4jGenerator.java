package cn.otra.db4j.utils.generator;


public class Db4jGenerator {

	public static void execute(String domain,String outputDir) {
		new MetaUtil().execute(domain,outputDir);
	}
}
