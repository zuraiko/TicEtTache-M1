package com.abgames.tictache.Model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Class who represent a todo
 */
public class TodoList implements Serializable {
    /**
     * Id of the todo
     */
    private String listId;
    /**
     * User id
     */
    private String userId;
    /**
     * Name of the todo
     */
    private String listName;
    /**
     * Priority of the todo
     */
    private String listPriority;
    /**
     * Create date of the todo
     */
    private String listAddDate;

    private boolean swiped = false;

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListPriority() {
        return listPriority;
    }

    public void setListPriority(String listPriority) {
        this.listPriority = listPriority;
    }

    public String getListAddDate() {
        return listAddDate;
    }

    public void setListAddDate(String listAddDate) {
        this.listAddDate = listAddDate;
    }

    public boolean isSwiped() {
        return swiped;
    }

    public void setSwiped(boolean swiped) {
        this.swiped = swiped;
    }

    public HashMap<String, String> toFirebaseObject(){
        HashMap<String, String> todo = new HashMap<String, String>();
        todo.put("listId",listId);
        todo.put("userId",userId);
        todo.put("listName", listName);
        todo.put("listPriority",String.valueOf(listPriority));
        todo.put("listAddDate",listAddDate);
        return todo;
    }

}
