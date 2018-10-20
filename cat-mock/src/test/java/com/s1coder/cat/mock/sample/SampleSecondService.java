package com.s1coder.cat.mock.sample;

import org.springframework.stereotype.Service;

/**
 * 类名: SampleSecondService <br/>
 * 用途: 测试用第二级服务 <br/>
 *
 * @author wangbo <br/>
 * Dec 26, 2017 5:04:35 PM
 */
@Service
public class SampleSecondService {

    private SampleOtherSysFacade sampleOtherSysFacade = new SampleOtherSysFacade();

    private SampleOtherSysFacade sampleOtherSysFacade3 = new SampleOtherSysFacade();

    public String callOtherSysFacade(String param) {
        return sampleOtherSysFacade.callOtherSys(param);
    }

    public String callOtherSysFacade3(String param) {
        return sampleOtherSysFacade3.callOtherSys(param);
    }
}
