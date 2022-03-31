package com.abgames.tictache.Tools;

import com.abgames.tictache.Model.TodoListItem;

import java.util.Comparator;


/**
 * Allow to sort TodoListItem by his createdate
 */
public class SortByCreateDate implements Comparator<TodoListItem> {

    @Override
    public int compare(TodoListItem t1, TodoListItem t2) {
        if(t1.getListItemCreateDate().split("/")[2].compareTo(t2.getListItemCreateDate().split("/")[2]) == 0){
            if(t1.getListItemCreateDate().split("/")[1].compareTo(t2.getListItemCreateDate().split("/")[1]) == 0){
                if(t1.getListItemCreateDate().split("/")[0].compareTo(t2.getListItemCreateDate().split("/")[0]) == 0){
                    return 0;
                } else
                    return t1.getListItemCreateDate().split("/")[0].compareTo(t2.getListItemCreateDate().split("/")[0]);
            } else
                return t1.getListItemCreateDate().split("/")[1].compareTo(t2.getListItemCreateDate().split("/")[1]);
        } else
            return t1.getListItemCreateDate().split("/")[2].compareTo(t2.getListItemCreateDate().split("/")[2]);
    }
}
