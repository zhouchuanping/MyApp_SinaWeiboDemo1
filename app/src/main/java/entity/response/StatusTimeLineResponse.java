package entity.response;

import java.util.ArrayList;

import entity.Status;

/**
 * Created by ZCP_ing on 2016/5/10.
 */
//微博页面信息类
public class StatusTimeLineResponse {

    private ArrayList<Status> statuses;
    private int total_number;

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }
}
