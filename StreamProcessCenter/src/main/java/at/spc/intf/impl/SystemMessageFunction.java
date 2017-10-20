package at.spc.intf.impl;

import at.spc.intf.LeafFunction;
import at.spc.intf.SubFunction;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by eyonlig on 9/22/2017.
 */
public class SystemMessageFunction implements LeafFunction {
    private static final String FUNCTION_ID = "SystemMessageFunction";
    private List<SubFunction> subFunctions = new LinkedList<>();

    @Override
    public String getFunctionId() {
        return FUNCTION_ID;
    }

    @Override
    public List<SubFunction> getSubFunctions() {
        return subFunctions;
    }

    @Override
    public String getTopic() {
        return "system-message";
    }

    @Override
    public String getSinkName() {
        return "SystemMessageSink";
    }
}
