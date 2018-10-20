package com.s1coder.cat.mock.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类名: TagTestService <br/>
 * 用途:  <br/>
 *
 * @author wangbo <br/>
 * Jan 8, 2018 5:58:47 PM
 */
@Service
public class TagTestService {
    @Autowired
    private TestServiceFacade testServiceFacade;
    @Autowired
    private TestInterfaceFacade testInterfaceFacade;

    public String callService(String param) {
        return testServiceFacade.call(param);
    }

    public String callInterface(String param) {
        return testInterfaceFacade.call(param);
    }
}
