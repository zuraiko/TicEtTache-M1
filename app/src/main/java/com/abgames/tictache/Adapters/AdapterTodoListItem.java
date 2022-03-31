package com.abgames.tictache.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abgames.tictache.Listeners.RecyclerListItemClick;
import com.abgames.tictache.Model.TodoListItem;
import com.abgames.tictache.R;
import com.abgames.tictache.Tools.Tools;
import com.abgames.tictache.Tools.ViewAnimation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Adaptater who manage every item of a todo
 */
public class AdapterTodoListItem extends RecyclerView.Adapter<AdapterTodoListItem.ViewHolder> {

    private RecyclerListItemClick clickListener;
    private List<TodoListItem> items;
    private Context context;

    public AdapterTodoListItem(List<TodoListItem> items, RecyclerListItemClick clickListener) {
        this.items = items;
        this.clickListener = clickListener;

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvItemStatus;
        TextView tvItemDeadlineDate;
        ProgressBar progressDeadline;
        TextView tvItemDesc;
        TextView tvItemCreateDate;
        TextView tvRemainingDay;
        View layoutEnded;
        View layoutEdit;
        View layoutDelete;
        View parentView;
        View expandView;
        View ivExpandLogo;
        View llActionLayout;

        ViewHolder(View v) {
            super(v);
            tvItemName = v.findViewById(R.id.tvItemName);
            tvItemStatus = v.findViewById(R.id.tvItemStatus);
            tvItemDeadlineDate = v.findViewById(R.id.tvItemDeadlineDate);
            progressDeadline = v.findViewById(R.id.progressDeadline);
            tvItemDesc = v.findViewById(R.id.tvItemDesc);
            tvItemCreateDate = v.findViewById(R.id.tvItemCreateDate);
            layoutEnded = v.findViewById(R.id.layoutEnded);
            layoutEdit = v.findViewById(R.id.layoutEdit);
            layoutDelete = v.findViewById(R.id.layoutDelete);
            parentView = v.findViewById(R.id.parentView);
            expandView = v.findViewById(R.id.expandView);
            ivExpandLogo = v.findViewById(R.id.ivExpandLogo);
            llActionLayout = v.findViewById(R.id.llActionLayout);
            tvRemainingDay = v.findViewById(R.id.tvRemainingDay);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todolistitem, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TodoListItem todoListItem = getItem(position);
        holder.tvItemName.setText(todoListItem.getListItemName());
        holder.tvItemDeadlineDate.setText(todoListItem.getListItemDeadline());
        holder.tvItemCreateDate.setText(context.getString(R.string.todoListItemCreateDate, todoListItem.getListItemCreateDate()));
        holder.tvItemDesc.setText(context.getString(R.string.todoListItemDesc, todoListItem.getListItemDesc()));

        holder.layoutEnded.setTag(position);
        holder.layoutEnded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.endTask(v, todoListItem, (int) v.getTag());
            }
        });


        LocalDate dateDead = LocalDate.of(Integer.parseInt(todoListItem.getListItemDeadline().split("/")[2]), Integer.parseInt(todoListItem.getListItemDeadline().split("/")[1]), Integer.parseInt(todoListItem.getListItemDeadline().split("/")[0]));
        LocalDate dateCreate = LocalDate.of(Integer.parseInt(todoListItem.getListItemCreateDate().split("/")[2]), Integer.parseInt(todoListItem.getListItemCreateDate().split("/")[1]), Integer.parseInt(todoListItem.getListItemCreateDate().split("/")[0]));
        LocalDate today = LocalDate.now();
        today.getDayOfYear();

        long taskTime = ChronoUnit.DAYS.between(dateCreate, dateDead);
        long remainingTime = ChronoUnit.DAYS.between(today, dateDead);
        int progress;
        if(remainingTime <= 0){
            progress = 100;
            if(remainingTime == 0)
                holder.progressDeadline.getProgressDrawable().setTint(Color.argb(255, 255, 127, 80));
            else
                holder.progressDeadline.getProgressDrawable().setTint(Color.RED);
        } else {
            progress = (int) ((1 - (remainingTime / taskTime))*100);
            if(progress == 0)
                holder.progressDeadline.getProgressDrawable().setTint(Color.WHITE);
            else
                holder.progressDeadline.getProgressDrawable().setTint(Color.GREEN);
        }




        holder.progressDeadline.setProgress(progress);

        holder.layoutEdit.setTag(position);
        holder.layoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.editTask(v, todoListItem, (int) v.getTag());
            }
        });


        holder.layoutDelete.setTag(position);
        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.deleteTask(v, todoListItem, (int) v.getTag());
            }
        });


        holder.parentView.setTag(position);
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show = toggleLayoutExpand(!todoListItem.isExpanded(), holder.ivExpandLogo, holder.expandView);
                todoListItem.setExpanded(show);
            }
        });

        if (!(todoListItem.getListItemStatusCode().equals("0"))) {
            holder.progressDeadline.setVisibility(View.GONE);
            holder.llActionLayout.setVisibility(View.GONE);
            holder.tvRemainingDay.setVisibility(View.GONE);
            holder.tvItemStatus.setText(context.getString(R.string.todoListItemStatusCompleted));
            holder.parentView.setBackgroundColor(context.getResources().getColor(R.color.overlay_green_10));
        } else {
            holder.progressDeadline.setVisibility(View.VISIBLE);
            holder.llActionLayout.setVisibility(View.VISIBLE);
            holder.tvRemainingDay.setVisibility(View.VISIBLE);

            if(remainingTime == 0) {
                holder.tvRemainingDay.setText(context.getString(R.string.todoListItemTodayDate));
                holder.tvItemStatus.setText(context.getString(R.string.todoListItemStatusContinue));
            }
            else if (remainingTime > 0) {
                if(remainingTime == 1)
                    holder.tvRemainingDay.setText(context.getString(R.string.todoListItemOneRemainingDate, String.valueOf(remainingTime)));
                else
                    holder.tvRemainingDay.setText(context.getString(R.string.todoListItemRemainingDate, String.valueOf(remainingTime)));
                holder.tvItemStatus.setText(context.getString(R.string.todoListItemStatusContinue));
                holder.tvRemainingDay.setTextColor(context.getResources().getColor(R.color.grey_40));
            } else {
                holder.tvItemStatus.setText(context.getString(R.string.todoListItemStatusExpired));
                if(Math.abs(remainingTime) == 1)
                    holder.tvRemainingDay.setText(context.getString(R.string.todoListItemOneDelayDate, String.valueOf(Math.abs(remainingTime))));
                else
                    holder.tvRemainingDay.setText(context.getString(R.string.todoListItemDelayDate, String.valueOf(Math.abs(remainingTime))));
                holder.tvRemainingDay.setTextColor(context.getResources().getColor(R.color.red_500));
            }
            holder.parentView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        if (todoListItem.isExpanded()) {
            holder.expandView.setVisibility(View.VISIBLE);
        } else {
            holder.expandView.setVisibility(View.GONE);
        }

        Tools.toggleArrow(todoListItem.isExpanded(), holder.ivExpandLogo, false);
    }

    private boolean toggleLayoutExpand(boolean show, View view, View lyt_expand) {
        Tools.toggleArrow(show, view);
        if (show) {
            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
        return show;
    }

    private TodoListItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

