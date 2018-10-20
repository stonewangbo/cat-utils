package com.s1coder.cat.spring;

import com.s1coder.cat.error.UtilsParamException;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;


/**
 * 类名: SpringPropertiesUtils <br/>
 * 用途: 动态读取spring环境中的配置信息 <br/>
 *
 * @author wangbo <br/>
 * Jun 27, 2016 11:56:45 AM
 */
@Component
public class SpringPropertiesUtils implements EmbeddedValueResolverAware {

    StringValueResolver resolver ;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    public String getPropertiesValue(String name) throws UtilsParamException {
        StringBuilder str = new StringBuilder();
        str.append("${").append(name).append("}");
        String res = resolver.resolveStringValue(str.toString());
        if (StringUtils.isEmpty(res) || res.equals(str.toString())) {
            throw new UtilsParamException("配置参数:" + name + " 无法找到!!!");
        }
        return res;
    }

}
