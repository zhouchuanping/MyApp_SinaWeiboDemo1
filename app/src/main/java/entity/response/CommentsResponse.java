package entity.response;

import java.util.List;

import entity.Comment;

/**
 * Created by ZCP_ing on 2016/5/14.
 */
public class CommentsResponse {

    private List<Comment> comments;
    private int total_number;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }
}
