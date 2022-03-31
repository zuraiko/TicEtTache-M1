package com.abgames.tictache.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abgames.tictache.Adapters.AdapterTodoList;
import com.abgames.tictache.Firebase.BddManager;
import com.abgames.tictache.Fragments.FragmentMenu;
import com.abgames.tictache.Listeners.RecyclerListClickListener;
import com.abgames.tictache.Model.TodoList;
import com.abgames.tictache.Model.TodoListItem;
import com.abgames.tictache.R;
import com.abgames.tictache.Tools.OnSwipeTouchListener;
import com.abgames.tictache.Tools.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 *  MainActivity to manage every ttodo
 *  it allow to create a new ttodo modify and also delete
 */
public class MainActivity extends AppCompatActivity {

    /**
     * List that contain every todo
     */
    private List<TodoList> todoLists, searchedLists;

    /**
     * Adapter use to notify and create every display of each todo
     */
    private AdapterTodoList adapterTodoList;

    /**
     * Time of the last click to detect multi selection
     */
    private long mLastClickTime = 0;

    /**
     * Action mode of the click
     */
    private ActionMode actionMode;
    //Multi Selection
    /**
     * Callback of multiselection
     */
    private ActionModeCallback actionModeCallback;
    /**
     * Last search
     */
    private String lastSearch = "";

    /**
     * Listener that heard type search
     */
    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            searchedLists.clear();
            adapterTodoList.notifyDataSetChanged();
            for (TodoList pp : todoLists) {
                if (pp.getListName().toUpperCase().contains(newText.toUpperCase(new Locale("tr")))) {
                    searchedLists.add(pp);
                }
            }
            adapterTodoList.notifyDataSetChanged();
            lastSearch = newText;
            return false;
        }
    };


    /**
     * Function that create the header menu
     * @param menu Menu at the top
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.todoListSearch));
        searchView.setOnQueryTextListener(searchListener);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchedLists.clear();
                searchedLists.addAll(todoLists);
                adapterTodoList.notifyDataSetChanged();
                return false;
            }
        });
        if (lastSearch != null && !lastSearch.isEmpty()) {
            searchView.setIconified(false);
            searchView.setQuery(lastSearch, false);
        }

        return true;
    }

    /**
     * Function execute at the start of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Tools.setSystemBarColor(this, R.color.green_500);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        todoLists = new ArrayList<>();
        searchedLists = new ArrayList<>();

        adapterTodoList = new AdapterTodoList(this, searchedLists, new RecyclerListClickListener() {
            @Override
            public void itemClick(View view, Object item, int position) {
                if (mLastClickTime - System.currentTimeMillis() > 2000) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                if (adapterTodoList.getSelectedItemCount() > 0) {
                    enableActionMode(position);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", ((TodoList) item).getListId());
                    Intent i = new Intent(MainActivity.this, TodoListItemActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }

            @Override
            public void longItemClick(View view, Object item, int position) {
                enableActionMode(position);
            }

            @Override
            public void moreItemClick(View view, Object item, int position, MenuItem menuItem) {
                TodoList todoList = (TodoList) item;
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        BddManager.getInstance().deleteTodoList(todoList);
                        BddManager.getInstance().deleteTodoListItemByListId(todoList);
                        Toast.makeText(MainActivity.this,getString(R.string.todoListDeleteMessage),Toast.LENGTH_SHORT);
                        //SnackToa.snackBarError(LoginActivity.this, getString(R.string.loginErrorMessage));

                        getAllTodoList();

                        break;
                    case R.id.action_update:
                        showAddListDialog(todoList);
                        break;
                }
            }

        });

        actionModeCallback = new ActionModeCallback(this);

        RecyclerView recyclerViewTodoList = findViewById(R.id.recyclerViewTodoList);
        recyclerViewTodoList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTodoList.setHasFixedSize(true);
        recyclerViewTodoList.setAdapter(adapterTodoList);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override

            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;

            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                TodoList todoList = (TodoList) todoLists.get(viewHolder.getAdapterPosition());
                switch (direction){

                    case ItemTouchHelper.LEFT:
                        BddManager.getInstance().deleteTodoList(todoList);
                        BddManager.getInstance().deleteTodoListItemByListId(todoList);
                        Toast.makeText(MainActivity.this,getString(R.string.todoListDeleteMessage),Toast.LENGTH_SHORT);
                        getAllTodoList();
                        break;
                    case ItemTouchHelper.RIGHT:
                        showAddListDialog(todoList);
                        break;

                }
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTodoList);

        setupSwipe();
        setupMenu();
        setupActionAddTodo();
        getAllTodoList();
    }

    /**
     * Toggle action for multi selection
     * @param position
     */
    private void toggleSelection(int position) {
        adapterTodoList.toggleSelection(position);
        int count = adapterTodoList.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    /**
     * Allow to activate multi selection
     * @param position
     */
    //Multi Selection
    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) this).startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
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

    /**
     * Function to count every todoitem
     */
    private void getCountTask(){
        TextView continues = findViewById(R.id.tvContinuesCount);
        TextView completed = findViewById(R.id.tvCompletedTask);
        TextView expired = findViewById(R.id.tvExpiredCount);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        Query listIdQuery = ref.child("todoListItem").orderByChild("userId").equalTo(BddManager.getInstance().getUid());

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
                    today.getDayOfYear();
                    long remainingTime = ChronoUnit.DAYS.between(today, dateDead);

                    if(tmp.getListItemStatusCode().equals("1")) {
                        cpt_com+=1;
                    }
                    else if(remainingTime >= 0 && tmp.getListItemStatusCode().equals("0")) {
                        cpt_c +=1;
                    } else {
                        cpt_expi +=1;
                    }
                }
                continues.setText(String.valueOf(cpt_c));
                completed.setText(String.valueOf(cpt_com));
                expired.setText(String.valueOf(cpt_expi));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled", error.toException());
            }
        });
    }

    // Add a todolist

    /**
     * Listener when a click is detect on a todo
     */
    private void setupActionAddTodo(){
        FloatingActionButton floatingActionButton = findViewById(R.id.fabNewList);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddListDialog(null);
            }
        });
    }

    /**
     * Get value priority index
     * @param array
     * @param selected
     * @return
     */
    private int getPriorityIndex(final String[] array, String selected) {
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(selected)) index = i;
        }
        return index;
    }

    /**
     * Display dialog to create a new todo
     * @param todoList
     */
    private void showAddListDialog(final TodoList todoList) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_new_list);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText etListName = dialog.findViewById(R.id.etListName);
        final EditText etPriority = dialog.findViewById(R.id.etPriority);
        final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
        final Button buttonSave = dialog.findViewById(R.id.buttonSave);

        final String[] priority = getResources().getStringArray(R.array.listPriority);
        if (todoList != null) {
            etListName.setText(todoList.getListName());
            etPriority.setText(priority[Integer.valueOf(todoList.getListPriority())]);
            tvHeader.setText(getString(R.string.todoListDialogHeaderUpdate));
            buttonSave.setText(getString(R.string.todoListItemDialogSubmitUpdate));
        } else {
            etListName.getText().clear();
            etPriority.setText(priority[0]);
            tvHeader.setText(getString(R.string.todoListDialogHeaderCreate));
            buttonSave.setText(getString(R.string.todoListItemDialogSubmitNew));
        }

        etPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriorityDialog(priority, v);
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
                if (TextUtils.isEmpty(etListName.getText().toString().trim())) {
                    etListName.setError(getString(R.string.todolistAddListNameError));
                    return;
                }
                String message = "";
                TodoList todoList1;
                boolean update;
                if (todoList != null) {
                    todoList1 = todoList;
                    message = getString(R.string.todoListUpdateMessage);
                    update = true;
                } else {
                    todoList1 = new TodoList();
                    message = getString(R.string.todoListCreateMessage);
                    update = false;
                }

                todoList1.setListName(etListName.getText().toString());
                todoList1.setListPriority(String.valueOf(getPriorityIndex(priority, etPriority.getText().toString())));
                if(update) {
                    BddManager.getInstance().addTodoList(todoList1,todoList1.getListId());
                }else {
                    todoList1.setListAddDate(String.valueOf(new Date().getDate()) + "/" + String.valueOf(new Date().getMonth() + 1) + "/" + String.valueOf(new Date().getYear() + 1900));
                    BddManager.getInstance().addTodoList(todoList1);
                }
                dialog.dismiss();
                getAllTodoList();
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * Diaply dialog to choose a priority
     * @param array
     * @param v
     */
    private void showPriorityDialog(final String[] array, final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.todoListPriority));
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    // Search All TodoList

    /**
     * Get every todo
     */
    private void getAllTodoList(){
        todoLists.clear();
        searchedLists.clear();

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        Query listIdQuery = ref.child("todoList").orderByChild("userId").equalTo(BddManager.getInstance().getUid());

        listIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cpt = 0;
                View llEmptyBox = findViewById(R.id.llEmptyBox);
                for(DataSnapshot listIdSnapshot : snapshot.getChildren()) {
                    todoLists.add(listIdSnapshot.getValue(TodoList.class));
                    cpt+=1;
                }
                if(cpt == 0)
                    llEmptyBox.setVisibility(View.VISIBLE);
                else{
                    llEmptyBox.setVisibility(View.GONE);
                    searchedLists.addAll(todoLists);
                    adapterTodoList.notifyDataSetChanged();
                    getCountTask();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled", error.toException());
            }
        });
    }

    /**
     * Unselect of multi selection
     */
    private void deleteSelectedListItems() {
        List<Integer> selectedItemPositions = adapterTodoList.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            TodoList removedList = searchedLists.get(selectedItemPositions.get(i));
            BddManager.getInstance().deleteTodoList(removedList);
            BddManager.getInstance().deleteTodoListItemByListId(removedList);
            adapterTodoList.removeData(selectedItemPositions.get(i));
        }
        adapterTodoList.notifyDataSetChanged();
        Toast.makeText(MainActivity.this,getString(R.string.todoListDeleteMessage),Toast.LENGTH_SHORT);
        getAllTodoList();
    }

    /**
     * Callback of multi selection
     */
    private class ActionModeCallback implements ActionMode.Callback {

        private Activity activity;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(this.activity, R.color.grey_40);
            mode.getMenuInflater().inflate(R.menu.menu_todolist, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteSelectedListItems();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapterTodoList.clearSelections();
            actionMode = null;
            Tools.setSystemBarColor(this.activity, R.color.green_500);
            getAllTodoList();
        }

        public ActionModeCallback(Activity activity){
            this.activity = activity;
        }
    }

    /**
     * Function that activate swipe on the acvity
     */
    private void setupSwipe(){
        /*
            Manage swipe action
            Swipe left to go on sign up page
         */
        CoordinatorLayout rl = findViewById(R.id.todoList);
        rl.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {

            }
            public void onSwipeLeft() {
                startActivity(new Intent(MainActivity.this, GraphicTodoActivity.class));
            }
            public void onSwipeBottom() {

            }
        });
    }
}
