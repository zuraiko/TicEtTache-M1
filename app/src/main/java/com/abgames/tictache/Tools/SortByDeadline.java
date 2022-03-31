package com.abgames.tictache.Tools;

import com.abgames.tictache.Model.TodoListItem;

import java.util.Comparator;


/**
 * Allow to sort TodoListItem by his deadline
 */
public class SortByDeadline implements Comparator<TodoListItem> {
    @Override
    public int compare(TodoListItem t1, TodoListItem t2) {
        if(t1.getListItemDeadline().split("/")[2].compareTo(t2.getListItemDeadline().split("/")[2]) == 0){
            if(t1.getListItemDeadline().split("/")[1].compareTo(t2.getListItemDeadline().split("/")[1]) == 0){
                if(t1.getListItemDeadline().split("/")[0].compareTo(t2.getListItemDeadline().split("/")[0]) == 0){
                    return 0;
                } else
                    return t1.getListItemDeadline().split("/")[0].compareTo(t2.getListItemDeadline().split("/")[0]);
            } else
                return t1.getListItemDeadline().split("/")[1].compareTo(t2.getListItemDeadline().split("/")[1]);
        } else
            return t1.getListItemDeadline().split("/")[2].compareTo(t2.getListItemDeadline().split("/")[2]);
    }
}
