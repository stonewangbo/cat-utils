package com.s1coder.cat.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 类名: ExceptionFormatUtils <br/>
 * 用途: 将exception完整转换为string <br/>
 *
 * @author wangbo <br/>
 * Dec 27, 2016 5:15:25 PM
 */
public class ExceptionFormatUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionFormatUtils.class);

    /**
     * 将错误完整转化为string
     *
     * @param Throwable e
     * @return
     */
    public static String exceptionToString(Throwable e) {
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e0) {
            logger.warn("can not parse Exception to string, will return simple message:", e0);
            return e.getMessage();
        }
    }
}
