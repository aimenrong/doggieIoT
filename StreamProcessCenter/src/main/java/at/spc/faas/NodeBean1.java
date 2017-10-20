package at.spc.faas;

/**
 * Created by eyonlig on 9/20/2017.
 */
public class NodeBean1 {
    private String name;
    private NodeBean2 bean2 = new NodeBean2();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeBean2 getBean2() {
        return bean2;
    }

    public void setBean2(NodeBean2 bean2) {
        this.bean2 = bean2;
    }
}
