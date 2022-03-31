package com.abgames.tictache.Model;

import java.util.HashMap;


/**
 * Class who represent an item of a todo
 */
public class TodoListItem {

    /**
     * Id of the item
     */
    private String listItemId;
    /**
     * Id of the todo
     */
    private String listId;
    /**
     * Name of the item
     */
    private String listItemName;
    /**
     * description of the item
     */
    private String listItemDesc;
    /**
     * Deadline of the item
     */
    private String listItemDeadline;
    /**
     * Create date of the item
     */
    private String listItemCreateDate;
    /**
     * Status of the item
     */
    private String listItemStatusCode;
    /**
     * Status name of the item
     */
    private String listItemStatusName;
    /**
     * Accomplish date of the item
     */
    private String listItemEndDate;
    /**
     * user id
     */
    private String userId;

    private boolean isExpanded;//Definition For Expandable RecyclerView

    public TodoListItem() {
        isExpanded = false;
        listItemStatusCode = "0";
    }

    public HashMap<String, String> toFirebaseObject() {
        HashMap<String, String> todoItem = new HashMap<String, String>();
        todoItem.put("listItemId", listItemId);
        todoItem.put("listId", listId);
        todoItem.put("listItemName", listItemName);
        todoItem.put("listItemDesc", listItemDesc);
        todoItem.put("listItemDeadline", listItemDeadline.toString());
        todoItem.put("listItemCreateDate", listItemCreateDate.toString());
        todoItem.put("listItemStatusCode", String.valueOf(listItemStatusCode));
        todoItem.put("listItemStatusName", listItemStatusName);
        todoItem.put("listItemEndDate",listItemEndDate);
        todoItem.put("userId", userId);
        return todoItem;
    }

    public String getListItemEndDate() {
        return listItemEndDate;
    }

    public void setListItemEndDate(String listItemEndDate) {
        this.listItemEndDate = listItemEndDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getListItemId() {
        return listItemId;
    }

    public void setListItemId(String listItemId) {
        this.listItemId = listItemId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListItemName() {
        return listItemName;
    }

    public void setListItemName(String listItemName) {
        this.listItemName = listItemName;
    }

    public String getListItemDesc() {
        return listItemDesc;
    }

    public void setListItemDesc(String listItemDesc) {
        this.listItemDesc = listItemDesc;
    }

    public String getListItemDeadline() {
        return listItemDeadline;
    }

    public void setListItemDeadline(String listItemDeadline) {
        this.listItemDeadline = listItemDeadline;
    }

    public String getListItemCreateDate() {
        return listItemCreateDate;
    }

    public void setListItemCreateDate(String listItemCreateDate) {
        this.listItemCreateDate = listItemCreateDate;
    }

    public String getListItemStatusCode() {
        return listItemStatusCode;
    }

    public void setListItemStatusCode(String listItemStatusCode) {
        this.listItemStatusCode = listItemStatusCode;
    }

    public String getListItemStatusName() {
        return listItemStatusName;
    }

    public void setListItemStatusName(String listItemStatusName) {
        this.listItemStatusName = listItemStatusName;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }



}
