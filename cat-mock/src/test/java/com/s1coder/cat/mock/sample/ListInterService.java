package com.s1coder.cat.mock.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 类名: MapInterService <br/>
 * 用途: 隐藏在map中的服务 <br/>
 *
 * @author wangbo <br/>
 * Dec 29, 2017 2:21:14 PM
 */
@Service
public class ListInterService {
    @Resource
    private SampleOtherSysFacade sampleOtherSysFacade;

    //模拟循环引用
    @Autowired
    private SampleTopService sampleTopService;

    public String callOtherSysFacade(String param) {
        return sampleOtherSysFacade.callOtherSys(param);
    }
}
