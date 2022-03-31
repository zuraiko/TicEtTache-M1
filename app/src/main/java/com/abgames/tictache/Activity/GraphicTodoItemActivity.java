package com.abgames.tictache.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.abgames.tictache.Fragments.FragmentMenu;
import com.abgames.tictache.Model.TodoList;
import com.abgames.tictache.Model.TodoListItem;
import com.abgames.tictache.R;
import com.abgames.tictache.Tools.OnSwipeTouchListener;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Class allow to display a graphic for todoitem level
 * it can show the progress of each todo
 */
public class GraphicTodoItemActivity extends AppCompatActivity {

    /**
     * key of the todo
     */
    private String key;
    /**
     * Current todo
     */
    private TodoList tl;

    /**
     * Function execute at the start of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);

        this.key = getIntent().getExtras().getString("key");

        setupMenu();
        getActualTodo();

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupSwipe();
    }

    /**
     * Setup the left menu
     */
    private void setupMenu(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentMenu menuFragment = (FragmentMenu) fm.findFragmentById(R.id.id_container_menu);
        if (menuFragment == null) {
            menuFragment = new FragmentMenu();
            fm.beginTransaction().add(R.id.id_container_menu, menuFragment).commit();
        }
    }

    /**
     * Use to get the current todo
     */
    private void getActualTodo(){
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        Query listIdQuery = ref.child("todoList").orderByChild("listId").equalTo(this.key);

        listIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot listIdSnapshot : snapshot.getChildren()) {
                    tl = listIdSnapshot.getValue(TodoList.class);

                }
                displayGraphic();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled", error.toException());
            }
        });
    }

    /**
     * Use to calculate and display the graphic who reprensent the progression of the todo
     */
    private void displayGraphic(){
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        Query listIdQuery = ref.child("todoListItem").orderByChild("listId").equalTo(key);
        listIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cpt_c = 0;
                int cpt_com = 0;
                int cpt_expi = 0;
                for(DataSnapshot listIdSnapshot : snapshot.getChildren()) {
                    TodoListItem tmp = listIdSnapshot.getValue(TodoListItem.class);
                    LocalDate dateDead = LocalDate.of(Integer.parseInt(tmp.getListItemDeadline().split("/")[2]), Integer.parseInt(tmp.getListItemDeadline().split("/")[1]), Integer.parseInt(tmp.getListItemDeadline().split("/")[0]));
                    LocalDate today = LocalDate.now();
                    long remainingTime = ChronoUnit.DAYS.between(today, dateDead);

                    if (tmp.getListItemStatusCode().equals("1")) {
                        cpt_com += 1;
                    } else if (remainingTime >= 0 && tmp.getListItemStatusCode().equals("0")) {
                        cpt_c += 1;
                    } else {
                        cpt_expi += 1;
                    }
                }

                    AnyChartView anyChartView = findViewById(R.id.any_chart_view);

                    Pie pie = AnyChart.pie();


                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Completed", cpt_com));
                    data.add(new ValueDataEntry("In Progress", cpt_c));
                    data.add(new ValueDataEntry("Expired", cpt_expi));



                    pie.data(data);

                    Toolbar tb = findViewById(R.id.toolbar);
                    setSupportActionBar(tb);
                    tb.setTitle(tb.getTitle()+" : "+tl.getListName());

                    pie.title("Distribution of an item by status");

                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Legend")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    anyChartView.setChart(pie);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled", error.toException());
            }
        });

    }

    /**
     * Setup the swipe that allow to turn back
     */
    private void setupSwipe(){
        /*
            Manage swipe action
            Swipe left to go on sign up page
         */
        LinearLayout rl = findViewById(R.id.chart);
        rl.setOnTouchListener(new OnSwipeTouchListener(GraphicTodoItemActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                Intent i = new Intent(GraphicTodoItemActivity.this, TodoListItemActivity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
            public void onSwipeLeft() {


            }
            public void onSwipeBottom() {

            }

        });
    }
}
