package com.s1coder.cat.mock.sample;

import com.s1coder.cat.spring.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名: SampleTopService <br/>
 * 用途: 测试用顶层服务<br/>
 *
 * @author wangbo <br/>
 * Dec 26, 2017 5:04:05 PM
 */
@Service
public class SampleTopService {
    @Autowired
    private SampleSecondService service2;

    @Autowired
    private SpringContextUtils springContextUtils;

    /**
     * 动态初始化服务内部也可以mock
     */
    private Map<String, MapInterService> map;
    /**
     * 动态初始化服务内部也可以mock
     */
    private List<ListInterService> list;

    @PostConstruct
    public void init() {
        map = springContextUtils.getApplicationContext().getBeansOfType(MapInterService.class);
        list = new ArrayList<>();
        Map<String, ListInterService> res = springContextUtils.getApplicationContext().getBeansOfType(ListInterService.class);
        list.addAll(res.values());
    }

    public String callService2(String param) {
        return service2.callOtherSysFacade(param);
    }

    public String callService3(String param) {
        return service2.callOtherSysFacade3(param);
    }

    public String callMapInterService(String param) {
        return map.values().iterator().next().callOtherSysFacade(param);
    }

    public String callListInterService(String param) {
        return list.iterator().next().callOtherSysFacade(param);
    }
}
