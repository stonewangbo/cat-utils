package com.s1coder.cat.mock.sample;

import org.springframework.stereotype.Service;

/**
 * 类名: SampleOtherSysFacade <br/>
 * 用途: 第三方接口示范 <br/>
 *
 * @author wangbo <br/>
 * Dec 26, 2017 5:05:53 PM
 */
@Service
public class SampleOtherSysFacade {
    public String callOtherSys(String param) {
        throw new RuntimeException("调用第三方接口失败");
    }
}
