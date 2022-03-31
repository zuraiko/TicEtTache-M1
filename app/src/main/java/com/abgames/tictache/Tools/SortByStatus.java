package com.abgames.tictache.Tools;

import com.abgames.tictache.Model.TodoListItem;

import java.util.Comparator;



/**
 * Allow to sort TodoListItem by his status
 */
public class SortByStatus implements Comparator<TodoListItem> {
    @Override
    public int compare(TodoListItem t1, TodoListItem t2) {
        return t1.getListItemStatusCode().compareTo(t2.getListItemStatusCode());
    }
}
