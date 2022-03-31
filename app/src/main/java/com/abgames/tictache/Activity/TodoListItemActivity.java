package com.abgames.tictache.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abgames.tictache.Adapters.AdapterTodoListItem;
import com.abgames.tictache.Firebase.BddManager;
import com.abgames.tictache.Fragments.FragmentMenu;
import com.abgames.tictache.Listeners.RecyclerListItemClick;
import com.abgames.tictache.Model.Filter;
import com.abgames.tictache.Model.TodoList;
import com.abgames.tictache.Model.TodoListItem;
import com.abgames.tictache.R;
import com.abgames.tictache.Tools.OnSwipeTouchListener;
import com.abgames.tictache.Tools.SortByCreateDate;
import com.abgames.tictache.Tools.SortByDeadline;
import com.abgames.tictache.Tools.SortByName;
import com.abgames.tictache.Tools.SortByStatus;
import com.abgames.tictache.Tools.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity to manage item from a todo
 */
public class TodoListItemActivity extends AppCompatActivity {

    /**
     * Key of the doo
     */
    private String key;
    /**
     * List to get each item of the todo
     */
    private List<TodoListItem> todoListItem, searchedList;
    /**
     * Adapter to group the function for each item
     */
    private AdapterTodoListItem adapterTodoListItem;
    /**
     * Stock the last click
     */
    private long mLastClickTime = 0;
    /**
     * Search of the text zone
     */
    private String lastSearch = "";

    /**
     * Filter that we can only show we we want
     */
    private Filter filter;

    /**
     * Boom button to dispay the filter choice
     */
    private BoomMenuButton bmb;
    /**
     * Stock each filter choice
     */
    private ArrayList<Pair> piecesAndButtons = new ArrayList<>();

    /**
     * Listener of the search texte
     */
    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            searchedList.clear();
            adapterTodoListItem.notifyDataSetChanged();
            for (TodoListItem pp : todoListItem) {
                if (pp.getListItemName().toUpperCase().contains(newText.toUpperCase(new Locale("tr")))) {
                    searchedList.add(pp);
                }
            }
            adapterTodoListItem.notifyDataSetChanged();
            lastSearch = newText;
            return false;
        }
    };

    /**
     * Create the menu to search zone
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todolistitem, menu);

        MenuItem searchItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.todoListSearch));
        searchView.setOnQueryTextListener(searchListener);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchedList.clear();
                searchedList.addAll(todoListItem);
                adapterTodoListItem.notifyDataSetChanged();
                return false;
            }
        });
        if (lastSearch != null && !lastSearch.isEmpty()) {
            searchView.setIconified(false);
            searchView.setQuery(lastSearch, false);
        }

        ImageButton orderButton = (ImageButton) menu.findItem(R.id.action_order).getActionView();
        orderButton.setImageResource(R.drawable.ic_order);
        orderButton.setBackgroundColor(getResources().getColor(R.color.green_500));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 20;
        orderButton.setLayoutParams(params);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TodoListItemActivity.this, v);
                popupMenu.setOnMenuItemClickListener(item -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                    Query listIdQuery = ref.child("todoListItem").orderByChild("listId").equalTo(key);
                    listIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            todoListItem.clear();
                            searchedList.clear();
                            int cpt = 0;
                            View llEmptyBox = findViewById(R.id.llEmptyBox);
                            for(DataSnapshot listIdSnapshot : snapshot.getChildren()) {
                                todoListItem.add(listIdSnapshot.getValue(TodoListItem.class));
                                cpt+=1;
                            }
                            if(cpt == 0)
                                llEmptyBox.setVisibility(View.VISIBLE);
                            else{
                                switch (item.getItemId()) {
                                    case R.id.action_createdate:
                                        Collections.sort(todoListItem,new SortByCreateDate());
                                        Log.d("TAG", "CREATE_DATE");
                                        break;
                                    case R.id.action_deadline:
                                        Collections.sort(todoListItem,new SortByDeadline());
                                        Log.d("TAG", "DEADLINE");
                                        break;
                                    case R.id.action_name:
                                        Collections.sort(todoListItem,new SortByName());
                                        Log.d("TAG", "NAME");
                                        break;
                                    case R.id.action_status:
                                        Collections.sort(todoListItem,new SortByStatus());
                                        Log.d("TAG", "STATUS");
                                        break;
                                }

                                if (todoListItem.isEmpty())
                                    llEmptyBox.setVisibility(View.VISIBLE);
                                else {
                                    llEmptyBox.setVisibility(View.GONE);
                                    searchedList.addAll(todoListItem);
                                }
                                adapterTodoListItem.notifyDataSetChanged();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("TAG", "onCancelled", error.toException());
                        }
                    });
                    return true;
                });
                popupMenu.inflate(R.menu.menu_todolistitem_order);
                popupMenu.show();
            }
        });

        return true;
    }

    /**
     * Execute at the start of the activitty
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_item);
        this.key = getIntent().getExtras().getString("key");

        Tools.setSystemBarColor(this, R.color.green_500);

        filter = new Filter();

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        todoListItem = new ArrayList<>();
        searchedList = new ArrayList<>();

        getActualTodo();
        setupMenu();
        setupActionAddTodoListItem();

        adapterTodoListItem = new AdapterTodoListItem(searchedList, new RecyclerListItemClick() {

            @Override
            public void endTask(View view, Object item, int position) {
                if (mLastClickTime - System.currentTimeMillis() > 2000)
                    return;
                mLastClickTime = System.currentTimeMillis();
                final TodoListItem todoListItem = (TodoListItem) item;
                AlertDialog.Builder builder = new AlertDialog.Builder(TodoListItemActivity.this);
                builder.setTitle(getString(R.string.todoListItemEndTask));
                builder.setMessage(getString(R.string.todoListItemEndTaskMessage));
                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        todoListItem.setListItemStatusCode("1");
                        todoListItem.setExpanded(false);
                        BddManager.getInstance().addTodoListItem(todoListItem,todoListItem.getListItemId());
                        getAllTodoListItem();
                        Toast.makeText(TodoListItemActivity.this,getString(R.string.todoListItemStatusMessage),Toast.LENGTH_SHORT);
                    }
                });
                builder.setNegativeButton(R.string.CANCEL, null);
                builder.show();
            }

            @Override
            public void editTask(View view, Object item, int position) {
                if (mLastClickTime - System.currentTimeMillis() > 2000)
                    return;
                mLastClickTime = System.currentTimeMillis();
                TodoListItem todoListItem = (TodoListItem) item;
                showAddListItemDialog(todoListItem);
            }

            @Override
            public void deleteTask(View view, final Object item, int position) {
                if (mLastClickTime - System.currentTimeMillis() > 2000)
                    return;
                mLastClickTime = System.currentTimeMillis();
                final TodoListItem todoListItem = (TodoListItem) item;
                AlertDialog.Builder builder = new AlertDialog.Builder(TodoListItemActivity.this);
                builder.setTitle(getString(R.string.todoListItemDeleteTask));
                builder.setMessage(getString(R.string.todoListItemDeleteTaskMessage));
                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        BddManager.getInstance().deleteTodoListItem(todoListItem);
                        getAllTodoListItem();
                        Toast.makeText(TodoListItemActivity.this,getString(R.string.todoListItemDeleteMessage),Toast.LENGTH_SHORT);
                    }
                });
                builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);


        HamButton.Builder continued = new HamButton.Builder()
                .normalTextRes(R.string.todoListFilterContinued)
                .normalColorRes(R.color.grey_40);

        HamButton.Builder expired = new HamButton.Builder()
                .normalTextRes(R.string.todoListFilterExpired)
                .normalColorRes(R.color.grey_40);

        HamButton.Builder completed = new HamButton.Builder()
                .normalTextRes(R.string.todoListFilterCompleted)
                .normalColorRes(R.color.grey_40);

        TextInsideCircleButton.Builder reset = new TextInsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_refresh).normalColorRes(R.color.orange_500);

        bmb.addBuilder(continued);
        bmb.addBuilder(expired);
        bmb.addBuilder(completed);
        bmb.addBuilder(reset);



        continued.listener(index -> {
            filter.resetFilter();
            filter.setContinued(true);
            getAllTodoListItem();


            continued.pieceColorRes(R.color.green_500);
            completed.pieceColorRes(R.color.grey_40);
            expired.pieceColorRes(R.color.grey_40);

            continued.normalColorRes(R.color.green_500);
            completed.normalColorRes(R.color.grey_40);
            expired.normalColorRes(R.color.grey_40);
        });

        expired.listener(index -> {
            filter.resetFilter();
            filter.setExpired(true);
            getAllTodoListItem();

            expired.normalColorRes(R.color.green_500);
            completed.normalColorRes(R.color.grey_40);
            continued.normalColorRes(R.color.grey_40);

            expired.pieceColorRes(R.color.green_500);
            completed.pieceColorRes(R.color.grey_40);
            continued.pieceColorRes(R.color.grey_40);


        });

        completed.listener(index -> {
            filter.resetFilter();
            filter.setCompleted(true);
            getAllTodoListItem();

            completed.normalColorRes(R.color.green_500);
            expired.normalColorRes(R.color.grey_40);
            continued.normalColorRes(R.color.grey_40);

            completed.pieceColorRes(R.color.green_500);
            expired.pieceColorRes(R.color.grey_40);
            continued.pieceColorRes(R.color.grey_40);


        });

        reset.listener(index -> {
            filter.resetFilter();
            getAllTodoListItem();

            completed.pieceColorRes(R.color.grey_40);
            expired.pieceColorRes(R.color.grey_40);
            continued.pieceColorRes(R.color.grey_40);

            completed.normalColorRes(R.color.grey_40);
            expired.normalColorRes(R.color.grey_40);
            continued.normalColorRes(R.color.grey_40);
        });


        RecyclerView recyclerViewTodoList = findViewById(R.id.recyclerViewTodoListItems);
        recyclerViewTodoList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTodoList.setHasFixedSize(true);
        recyclerViewTodoList.setAdapter(adapterTodoListItem);

        getAllTodoListItem();
        setupSwipe();
    }

    /**
     * Setup the left bar menu
     */
    private void setupMenu(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentMenu menuFragment = (FragmentMenu) fm.findFragmentById(R.id.id_container_menu);
        if (menuFragment == null) {
            menuFragment = new FragmentMenu();
            fm.beginTransaction().add(R.id.id_container_menu, menuFragment).commit();
        }
    }

    // GET TodoList

    /**
     * Get the current todo
     */
    private void getActualTodo(){
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        Query listIdQuery = ref.child("todoList").orderByChild("listId").equalTo(this.key);

        listIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot listIdSnapshot : snapshot.getChildren()) {
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled", error.toException());
            }
        });
    }

    // ADD ITEM

    /**
     * Setup the function to add a item to a todo
     */
    private void setupActionAddTodoListItem(){
        FloatingActionButton floatingActionButton = findViewById(R.id.fabNewListItem);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddListItemDialog(null);
            }
        });
    }

    /**
     * Show the dialog to create a todo
     * @param todoListItem
     */
    private void showAddListItemDialog(final TodoListItem todoListItem) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_new_list_item);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText etListItemName = dialog.findViewById(R.id.etListItemName);
        final EditText etListItemDesc = dialog.findViewById(R.id.etListItemDesc);
        final TextView tvListItemDeadline = dialog.findViewById(R.id.tvListItemDeadline);
        final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
        final Button buttonSave = dialog.findViewById(R.id.buttonSave);

        if (todoListItem != null) {
            etListItemName.setText(todoListItem.getListItemName());
            etListItemDesc.setText(todoListItem.getListItemDesc());
            tvListItemDeadline.setText(todoListItem.getListItemDeadline());
            tvHeader.setText(getString(R.string.todoListItemDialogHeaderUpdateItem));
            buttonSave.setText(getString(R.string.todoListItemDialogSubmitUpdate));
        } else {
            etListItemName.getText().clear();
            etListItemDesc.getText().clear();
            tvListItemDeadline.setText(String.valueOf(new Date().getDate()) + "/" + String.valueOf(new Date().getMonth() + 1) + "/" + String.valueOf(new Date().getYear() + 1900));
            tvHeader.setText(getString(R.string.todoListItemDialogHeaderNewItem));
            buttonSave.setText(getString(R.string.todoListItemDialogSubmitNew));
        }

        tvListItemDeadline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int selectedYear = new Date().getYear() + 1900;
                int selectedMonth = new Date().getMonth();
                int selectedDayOfMonth = new Date().getDate();

                // Date Select Listener.
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        tvListItemDeadline.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                };

                // Create DatePickerDialog (Spinner Mode):
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        dateSetListener, selectedYear, selectedMonth, selectedDayOfMonth);

                // Show
                datePickerDialog.show();
            }
        });

        dialog.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etListItemName.getText().toString().trim())) {
                    etListItemName.setError(getString(R.string.todolistAddListItemNameError));
                    return;
                }
                if (TextUtils.isEmpty(etListItemDesc.getText().toString().trim())) {
                    etListItemDesc.setError(getString(R.string.todolistAddListItemDescError));
                    return;
                }
                TodoListItem todoListItemNew;
                String message = "";
                boolean update;
                if (todoListItem != null) {
                    todoListItemNew = todoListItem;
                    message = getString(R.string.todoListItemUpdateMessage);
                    update = true;
                } else {
                    todoListItemNew = new TodoListItem();
                    message = getString(R.string.todoListItemCreateMessage);
                    update = false;
                }
                todoListItemNew.setListId(key);
                todoListItemNew.setListItemName(etListItemName.getText().toString());
                todoListItemNew.setListItemDesc(etListItemDesc.getText().toString());
                todoListItemNew.setListItemDeadline(tvListItemDeadline.getText().toString());

                if(update){
                    BddManager.getInstance().addTodoListItem(todoListItemNew,todoListItemNew.getListItemId());
                } else {
                    todoListItemNew.setListItemStatusCode("0");
                    todoListItemNew.setExpanded(false);
                    todoListItemNew.setListItemCreateDate(String.valueOf(new Date().getDate()) + "/" + String.valueOf(new Date().getMonth() + 1) + "/" + String.valueOf(new Date().getYear() + 1900));
                    BddManager.getInstance().addTodoListItem(todoListItemNew);
                }
                dialog.dismiss();
                getAllTodoListItem();
                Toast.makeText(TodoListItemActivity.this,message,Toast.LENGTH_SHORT);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    // GET TodoListItem

    /**
     * Get every item in a todo
     */
    private void getAllTodoListItem(){
        todoListItem.clear();
        searchedList.clear();

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        Query listIdQuery = ref.child("todoListItem").orderByChild("listId").equalTo(key);
        listIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cpt = 0;
                View llEmptyBox = findViewById(R.id.llEmptyBox);



                for(DataSnapshot listIdSnapshot : snapshot.getChildren()) {
                    TodoListItem tmp = listIdSnapshot.getValue(TodoListItem.class);
                    LocalDate dateDead = LocalDate.of(Integer.parseInt(tmp.getListItemDeadline().split("/")[2]), Integer.parseInt(tmp.getListItemDeadline().split("/")[1]), Integer.parseInt(tmp.getListItemDeadline().split("/")[0]));
                    LocalDate today = LocalDate.now();
                    today.getDayOfYear();
                    long remainingTime = ChronoUnit.DAYS.between(today, dateDead);

                    if(filter.isCompleted()){
                        if(tmp.getListItemStatusCode().equals("1"))
                            todoListItem.add(listIdSnapshot.getValue(TodoListItem.class));
                    }
                    else if(filter.isContinued()){
                        if(remainingTime > 0 && tmp.getListItemStatusCode().equals("0"))
                            todoListItem.add(listIdSnapshot.getValue(TodoListItem.class));
                    }
                    else if(filter.isExpired()){
                        if(remainingTime < 0)
                            todoListItem.add(listIdSnapshot.getValue(TodoListItem.class));
                    } else
                        todoListItem.add(listIdSnapshot.getValue(TodoListItem.class));

                    cpt+=1;
                }
                if(cpt == 0)
                    llEmptyBox.setVisibility(View.VISIBLE);
                else{
                    llEmptyBox.setVisibility(View.GONE);
                    searchedList.addAll(todoListItem);
                    adapterTodoListItem.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled", error.toException());
            }
        });

    }

    /**
     * Swipe to get graphic
     */
    private void setupSwipe(){
        /*
            Manage swipe action
            Swipe left to go on sign up page
         */
        RecyclerView rl = findViewById(R.id.recyclerViewTodoListItems);
        rl.setOnTouchListener(new OnSwipeTouchListener(TodoListItemActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {

            }
            public void onSwipeLeft() {
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                Intent i = new Intent(TodoListItemActivity.this, GraphicTodoItemActivity.class);
                i.putExtras(bundle);
                startActivity(i);

            }
            public void onSwipeBottom() {

            }

        });
    }


}