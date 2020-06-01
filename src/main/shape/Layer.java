package main.shape;

public class Layer {
    private static int tid = 0;
    private int id;
    private String name;
    private boolean visible = true;

    public Layer(String name) {
        id = tid;
        tid++;
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return "Layer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVisible(boolean b) {
        visible = b;
    }
}
