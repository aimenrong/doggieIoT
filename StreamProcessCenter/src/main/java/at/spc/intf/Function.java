package at.spc.intf;


import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/21.
 */
public interface Function {
    String getFunctionId();

    List<SubFunction> getSubFunctions();

    String getTopic();

    String getSinkName();
}
