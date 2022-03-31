package com.abgames.tictache.Tools;

import com.abgames.tictache.Model.TodoListItem;

import java.util.Comparator;

/**
 * Allow to sort TodoListItem by his name
 */
public class SortByName implements Comparator<TodoListItem> {
    @Override
    public int compare(TodoListItem t1, TodoListItem t2) {
        return t1.getListItemName().compareTo(t2.getListItemName());
    }
}
