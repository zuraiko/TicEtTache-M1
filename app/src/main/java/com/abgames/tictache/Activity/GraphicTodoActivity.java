package com.abgames.tictache.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.abgames.tictache.Firebase.BddManager;
import com.abgames.tictache.Fragments.FragmentMenu;
import com.abgames.tictache.Model.TodoList;
import com.abgames.tictache.Model.TodoListItem;
import com.abgames.tictache.R;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Radar;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.MarkerType;
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
 * Class allow to display a graphic for todo level
 * it can show the progress of each todo
 */
public class GraphicTodoActivity extends AppCompatActivity {


    /**
     * Function execute at the start of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic_todo);

        setupMenu();

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        displayGraphic();
    }

    /**
     * Use to setup the left menu
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
     * Calculate each value to display the progression of each todo
     */
    private void displayGraphic() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        Query listIdQuery = ref.child("todoList").orderByChild("userId").equalTo(BddManager.getInstance().getUid());

        listIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TodoList> todoLists = new ArrayList<>();




                for(DataSnapshot listIdSnapshot : snapshot.getChildren()) {
                    todoLists.add(listIdSnapshot.getValue(TodoList.class));
                }

                int[][] data = new int[todoLists.size()][3];

                for(int i=0; i<todoLists.size(); i++){

                    DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                    Query listIdQuery = ref.child("todoListItem").orderByChild("listId").equalTo(todoLists.get(i).getListId());
                    int finalI = i;
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




                            data[finalI][0] = cpt_c;
                            data[finalI][1] = cpt_com;
                            data[finalI][2] = cpt_expi;

                            if(finalI+1 == todoLists.size()){
                                displayGraphic(todoLists, data);
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("TAG", "onCancelled", error.toException());
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled", error.toException());
            }
        });
    }

    /**
     * Use to get the max value of each varaible, to limit the legend
     * @param numbers array contrain each value
     * @return the maximun value
     */
    public static int getMaxValue(int[][] numbers) {
        int maxValue = numbers[0][0];
        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] > maxValue) {
                    maxValue = numbers[j][i];
                }
            }
        }
        return maxValue;
    }

    /**
     * Use to display the progression of each todo
     * @param t Todo's lists
     * @param d the value
     */
    private void displayGraphic(List<TodoList> t, int[][] d){
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);

        Radar radar = AnyChart.radar();

        radar.title("Visualisation Todos progression");

        radar.yScale().minimum(0d);
        radar.yScale().minimumGap(0d);
        radar.yScale().ticks().interval(getMaxValue(d)+2);

        radar.xAxis().labels().padding(5d, 5d, 5d, 5d);

        radar.legend().align(Align.CENTER).enabled(true);

        List<DataEntry> data = new ArrayList<>();





        Number tmp[][];
        String name[] = {"In Progress", "Completed", "Expired"};

        tmp = new Number[3][t.size()];
        for(int i=0; i<3; i++) {
            for (int j = 0; j < t.size(); j++)
                tmp[i][j] = d[i][j];
            data.add(new CustomDataEntry(name[i],tmp[i]));
        }

        Set set = Set.instantiate();
        set.data(data);

        Mapping[] mapping = new Mapping[t.size()];

        for(int i=0; i<t.size(); i++){
            if(i == 0)
                mapping[i] = set.mapAs("{ x: 'x', value: 'value' }");
            else
                mapping[i] = set.mapAs("{ x: 'x', value: 'value"+String.valueOf(i)+"' }");

            radar.line(mapping[0]).name(t.get(i).getListName()).markers().enabled(true).type(MarkerType.CIRCLE).size(3d);
        }


        radar.tooltip().format("Value: {%Value}");

        anyChartView.setChart(radar);
    }

    /**
     * Put each value of each todo in legend
     */
    private class CustomDataEntry extends ValueDataEntry {
        public CustomDataEntry(String x, Number[] value) {
            super(x, value[0]);
            String name[] = {"In Progress", "Completed", "Expired"};
            for(int i=1; i<value.length; i++)
                setValue(name[i], value[i]);
        }
    }

}