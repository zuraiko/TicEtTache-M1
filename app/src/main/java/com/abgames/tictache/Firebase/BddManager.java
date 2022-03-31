package com.abgames.tictache.Firebase;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abgames.tictache.Model.TodoList;
import com.abgames.tictache.Model.TodoListItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Class who manage every function who is related with firebasedatabse
 */
public class BddManager {

    /**
     * Pattern singleton
     */
    private static BddManager instance = null;
    /**
     * User authentification
     */
    private FirebaseAuth mAuth;
    /**
     * The current user who is login
     */
    private FirebaseUser currentUser;


    /**
     * Get authentification when the class is created
     */
    private BddManager(){
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Pattern singleton
     * @return
     */
    public static BddManager getInstance(){
        if(instance == null){
            synchronized (BddManager.class){
                if(instance == null){
                    instance = new BddManager();
                }
            }
        }
        return instance;
    }

    /**
     * Allow to sign-up to the firebase
     * @param view
     * @param currentActivity
     * @param newActivity
     * @param email
     * @param password
     * @param username
     */
    public void signUp(View view, Activity currentActivity, Class newActivity, String email, String password, String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(currentActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(currentActivity,  "Successful authentication.",
                                    Toast.LENGTH_SHORT).show();
                            currentUser = mAuth.getCurrentUser();
                            updateProfile(username);
                            changeActivity(view, currentActivity, newActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(currentActivity,  task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Allow to signin to the firebase
     * @param view
     * @param currentActivity
     * @param newActivity
     * @param email
     * @param password
     */
    public void signIn(View view, Activity currentActivity, Class newActivity, String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(currentActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(currentActivity,  "Successful login.",
                                    Toast.LENGTH_SHORT).show();
                            currentUser = mAuth.getCurrentUser();
                            changeActivity(view, currentActivity, newActivity);
;
                        } else {
                            // If sign in fails, display a message to the user.;
                            Toast.makeText(currentActivity,  task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Allow to change activity, after login et after sign-up
     * @param v
     * @param currentActivity
     * @param newActivity
     */
    private void changeActivity(View v, Activity currentActivity, Class newActivity){
        Intent intent = new Intent(v.getContext(), newActivity);
        currentActivity.startActivity(intent);
    }

    /**
     * Get the username of the current user
     * @return
     */
    public String getUsername(){
        return currentUser.getDisplayName();
    }

    /**
     * Get the uid of the current user
     * @return
     */
    public String getUid(){return currentUser.getUid();}

    /**
     * Update profil of the user
     * @param username
     */
    public void updateProfile(String username){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        currentUser.updateProfile(profileUpdates);
    }

    // INSERT

    /**
     * Add a todo in the firebase
     * @param t
     */
    public void addTodoList(TodoList t){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app");
        String key = database.getReference("todoList").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        t.setUserId(currentUser.getUid());
        t.setListId(key);
        childUpdates.put(key,t.toFirebaseObject());
        database.getReference("todoList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error == null)
                    Log.d("Tag","OKOKOK");
            }
        });
    }

    // UPDATE

    /**
     * Update a todo in the firebase
     * @param t
     * @param key
     */
    public void addTodoList(TodoList t, String key){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key,t.toFirebaseObject());
        database.getReference("todoList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error == null)
                    Log.d("Tag","OKOKOK");
            }
        });
    }

    /**
     * Add a item of a todo to the firebase
     * @param t
     */
    public void addTodoListItem(TodoListItem t){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app");
        String key = database.getReference("todoListItem").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        t.setUserId(currentUser.getUid());
        t.setListItemId(key);
        childUpdates.put(key,t.toFirebaseObject());
        database.getReference("todoListItem").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error == null)
                    Log.d("Tag","OKOKOK");
            }
        });
    }

    // Update

    /**
     * Update a item of a todo to the firebase
     * @param t
     * @param key
     */
    public void addTodoListItem(TodoListItem t, String key){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key,t.toFirebaseObject());
        database.getReference("todoListItem").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error == null)
                    Log.d("Tag","OKOKOK");
            }
        });
    }

    // DELETE

    /**
     * Delete a todo from the firebase
     * @param t
     */
    public void deleteTodoList(TodoList t){
        FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("todoList").child(t.getListId()).removeValue();
    }

    /**
     * Delete a todo item from the firebase
     * @param t
     */
    public void deleteTodoListItem(TodoListItem t){
        FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("todoListItem").child(t.getListItemId()).removeValue();
    }

    /**
     * Delete every item of a todo
     * @param t
     */
    public void deleteTodoListItemByListId(TodoList t){
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tictache-b7f0b-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        Query listIdQuery = ref.child("todoListItem").orderByChild("listId").equalTo(t.getListId());

        listIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot listIdSnapshot : snapshot.getChildren())
                    listIdSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled", error.toException());
            }
        });
    }

}
