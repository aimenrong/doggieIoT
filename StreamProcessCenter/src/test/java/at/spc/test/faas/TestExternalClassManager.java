package at.spc.test.faas;

import at.spc.faas.ExternalClassManager;
import at.spc.faas.NodeBean1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.net.URL;

/**
 * Created by eyonlig on 9/22/2017.
 */
@RunWith(PowerMockRunner.class)
public class TestExternalClassManager {
    private ExternalClassManager externalClassManager;

    @Before
    public void init() {
        externalClassManager = new ExternalClassManager();
    }

    @Test
    public void testLoadInstance() throws Exception {
        URL beanUrl1 = this.getClass().getClassLoader().getResource("testBean1.jar");
        String rootPath = beanUrl1.toString();
        System.out.println(rootPath);
        rootPath = rootPath.substring(rootPath.indexOf("file:/") + "file:/".length(), rootPath.lastIndexOf("/"));
        System.out.println(rootPath);
        externalClassManager.loadExternalJar("testFunctionChain", rootPath);
        Object bean1 =  externalClassManager.loadInstance("testFunctionChain", "at.spc.faas.NodeBean1");
        String retValue = retrieveValueByReflection(bean1, "getName");
        Assert.assertSame(retValue, "bean1");
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadInstanceWithException() throws Throwable {
        String pathNotExist = "K:/";
        externalClassManager.loadExternalJar("testFunctionChain", pathNotExist);
        try {
            Object bean1 =  externalClassManager.loadInstance("testFunctionChain", "at.spc.faas.NodeBean1");
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    private String retrieveValueByReflection(Object object, String methodName) throws Exception {
        Method method = object.getClass().getMethod(methodName);
        return (String) method.invoke(object);
    }

}
