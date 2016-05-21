package entity;

/**
 * Created by ZCP_ing on 2016/5/10.
 */
public class Visible extends BaseEntity  {

    private int type;
    private int list_id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    @Override
    public String toString() {
        return "Visible{" +
                "type=" + type +
                ", list_id=" + list_id +
                '}';
    }
}
