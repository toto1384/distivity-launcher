package com.distivity.productivitylauncher.Database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.distivity.productivitylauncher.Activities.HomeActivity;
import com.distivity.productivitylauncher.Pojos.References;
import com.distivity.productivitylauncher.Pojos.Todo;
import com.distivity.productivitylauncher.Pojos.User;
import com.distivity.productivitylauncher.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class DaoHelper {



    private static DaoHelper daoHelper;

    private Dao dao;

    private User user;

    private FirebaseUser firebaseUser;



    public void updateChanges(List<Todo> todosToAdd, List<Todo> todosToUpdate , List<Todo> todosToDelete){

        if (isOnCloud){
            user.addTodos(todosToAdd);
            user.deleteTodos(todosToDelete);
            for (Todo todoToUpdate:todosToUpdate){
                user.updateTodo(todoToUpdate);
            }
            Utils.getUserrReference().child(firebaseUser.getUid()).setValue(user);
        }



    }

    private boolean isOnCloud = false;

    private DaoHelper(final AppCompatActivity applicationContext) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dao = Database.getInstance(applicationContext).dao();

        if (firebaseUser ==null){
            isOnCloud=false;
        }else {
            Utils.getUserrReference().child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user=dataSnapshot.getValue(User.class);

                    if (user==null){
                        isOnCloud=false;
                    }else {
                        isOnCloud = user.isPremium();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    isOnCloud = false;
                }
            });
        }



        //init id
        if (isOnCloud){
            id = user.getIdCount();
        }else {
            id = References.getPrefs(applicationContext).getInt(Utils.CONSTANTS.SharedPreferences.ID,0);
        }


    }


    public static DaoHelper getInstance(AppCompatActivity compatActivity){
        if (daoHelper==null){
            daoHelper= new DaoHelper(compatActivity);
        }

        return  daoHelper;
    }

    public static void releaseDaoHelper(){
        daoHelper = null;
    }


    private  int id ;

    public int getId(HomeActivity context){

        if (isOnCloud){
            user.setIdCount(id+1);
        }else {
            References.getPrefs(context).edit().putInt(Utils.CONSTANTS.SharedPreferences.ID,id+1).apply();
        }

        id++;

        return id-1;

    }

}
