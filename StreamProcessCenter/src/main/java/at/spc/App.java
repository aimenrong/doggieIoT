package at.spc;

import at.spc.bean.FunctionMeta;
import at.spc.common.FunctionManager;
import at.spc.common.SpringOnLoadConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Terry LIANG on 2017/9/4.
 */
public class App {
    private static AnnotationConfigApplicationContext context;

    public void init() {
        context = new AnnotationConfigApplicationContext();
        context.scan("at.spc");
        context.register(SpringOnLoadConfiguration.class);
        context.refresh();
    }

    public void startup() {
        FunctionManager functionManager = (FunctionManager) context.getBean("FunctionManager");
        FunctionMeta deviceNotificationMeta = (FunctionMeta) context.getBean("DeviceEventFunctionMeta");
        functionManager.activateFunction(functionManager.parseFunctionMeta(deviceNotificationMeta));

        FunctionMeta systemMessageMeta = (FunctionMeta) context.getBean("SystemMessageFunctionMeta");
        functionManager.activateFunction(functionManager.parseFunctionMeta(systemMessageMeta));
    }

    public static void main(String[] args) {
        App app = new App();
        app.init();
//        app.test();
        app.startup();
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {

            }
        }
    }

    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }
}
