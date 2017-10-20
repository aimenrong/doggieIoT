package at.spc.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eyonlig on 9/22/2017.
 */
public class FunctionMeta {
    private String functionChainId;
    private String functionJarRootPath;
    private String functionConfigurationClassName;
    private String[] functionBeanNames;
    private Map<String, String[]> subFunctionBeanNames = new HashMap<>();
    private boolean externalFunction;

    public String getFunctionChainId() {
        return functionChainId;
    }

    public void setFunctionChainId(String functionChainId) {
        this.functionChainId = functionChainId;
    }

    public String[] getFunctionBeanNames() {
        return functionBeanNames;
    }

    public void setFunctionBeanNames(String[] functionBeanNames) {
        this.functionBeanNames = functionBeanNames;
    }

    public Map<String, String[]> getSubFunctionBeanNames() {
        return subFunctionBeanNames;
    }

    public String getFunctionConfigurationClassName() {
        return functionConfigurationClassName;
    }

    public void setFunctionConfigurationClassName(String functionConfigurationClassName) {
        this.functionConfigurationClassName = functionConfigurationClassName;
    }

    public String getFunctionJarRootPath() {
        return functionJarRootPath;
    }

    public void setFunctionJarRootPath(String functionJarRootPath) {
        this.functionJarRootPath = functionJarRootPath;
    }

    public boolean isExternalFunction() {
        return externalFunction;
    }

    public void setExternalFunction(boolean externalFunction) {
        this.externalFunction = externalFunction;
    }
}
