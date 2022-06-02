package com.distivity.productivitylauncher.Database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.distivity.productivitylauncher.Pojos.Todo;
import com.distivity.productivitylauncher.Pojos.TreeNode;

import java.util.ArrayList;
import java.util.List;


@androidx.room.Dao
    public abstract class Dao {


        @Transaction
        List<TreeNode> loadSortedTodos(boolean forToday){

            List<TreeNode> unSortedTodos = new ArrayList<>();

            for (Todo todoWithoutParent: getTodosWithParrentId(-1,forToday)){

                unSortedTodos.add(getTreenodeFromTodo(todoWithoutParent,forToday));
            }

            return findDisplayNodes(unSortedTodos);
        }

    private TreeNode getTreenodeFromTodo(Todo todoWithoutParent,boolean isForToday) {

        TreeNode treeNode =new TreeNode(todoWithoutParent);


            for (Todo todo:getTodosWithParrentId(todoWithoutParent.getId(),isForToday)){

                if (todo!=null){
                    treeNode.addChild(getTreenodeFromTodo(todo,isForToday));
                }


            }


        return treeNode;
    }

    private List<TreeNode> findDisplayNodes(List<TreeNode> nodes) {


            List<TreeNode> sortedTodos = new ArrayList<>();



        for (TreeNode node : nodes) {
            sortedTodos.add(node);
            if (!node.isLeaf())
                findDisplayNodes(node.getChildList());
        }

        return sortedTodos;
    }


    @Query("SELECT * FROM todos WHERE parrentId=:parrentId AND isForToday=:forToday")
    abstract List<Todo> getTodosWithParrentId(int parrentId,boolean forToday);


        @Transaction
        void updateDatabase(List<Todo> todoToAddForLater, List<Todo> todosToUpdate, List<Todo> todosToDelete
                , Runnable toRunOnFinish, Runnable toRunOnFinish1){
            for (Todo toUpdate:todosToUpdate){
                if (toUpdate!=null){
                    insertTodo(toUpdate);
                }

            }

            for (Todo todo:todoToAddForLater){
                if (todo!=null){
                    insertTodo(todo);
                }
            }

            for (Todo toDelete:todosToDelete){
                if (toDelete!=null){
                    deleteTodo(toDelete);
                }

            }
            if (toRunOnFinish!=null){
                toRunOnFinish.run();
            }
            if (toRunOnFinish1!=null){
                toRunOnFinish1.run();
            }
        }



        @Insert(onConflict = OnConflictStrategy.REPLACE)
        abstract void insertTodo(Todo todo);

        @Delete
        abstract void deleteTodo(Todo todo);



}
