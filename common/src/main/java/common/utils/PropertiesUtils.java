package common.utils;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * properties文件处理工具类，默认只支持编码为UTF-8的文件
 * User: lidujun
 * Date: 2015-04-29
 */
public class PropertiesUtils {

	/**
	 * 文件默认编码格式
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	/**
	 * 载入多个properties文件, 相同的属性在最后载入的文件中的值将会覆盖之前的载入. 文件路径使用Spring Resource格式,
	 * 文件编码使用UTF-8.
	 * 
	 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
	 */
	public static Properties loadProperties(String... resourcesPaths)
			throws IOException {
		Properties props = new Properties();
		for (String location : resourcesPaths) {
			System.out.println("Loading properties file from:" + location);
			InputStream is = null;
			try {
				Resource resource = resourceLoader.getResource(location);
				is = resource.getInputStream();
				propertiesPersister.load(props, new InputStreamReader(is,
						DEFAULT_ENCODING));
			} catch (IOException ex) {
				throw new RuntimeException("Could not load properties from classpath:"
						+ location + ": " + ex.getMessage(),ex);
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		return props;
	}
}