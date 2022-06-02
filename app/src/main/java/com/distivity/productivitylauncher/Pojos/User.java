package com.distivity.productivitylauncher.Pojos;

import java.util.ArrayList;
import java.util.List;

public class User {


    private List<Todo> allTodos ;
    private int idCount;
    private boolean isPremium;

    private String UserId;

    public User(List<Todo> allTodos, int idCount, boolean isPremium, String userId) {
        this.allTodos = allTodos;
        this.idCount = idCount;
        this.isPremium = isPremium;
        UserId = userId;
    }

    public List<Todo> getAllTodos() {
        return allTodos;
    }

    public void deleteTodo(Todo todo){
        allTodos.remove(todo);
    }

    public void addTodo(Todo todo){

        allTodos.add(todo);
    }

    public void updateTodo(Todo todo){

        for (int i = 0; i<allTodos.size();i++){
            Todo todoToUpdate = allTodos.get(i);
            if (todoToUpdate.getId()==todo.getId()){
                allTodos.set(i,todo);
                return;
            }
        }
    }

    public void addTodos(List<Todo> todosToAdd){
        allTodos.addAll(todosToAdd);
    }

    public void deleteTodos(List<Todo> deleteTodos){
        allTodos.removeAll(deleteTodos);
    }


    public void setAllTodos(List<Todo> allTodos) {
        this.allTodos = allTodos;
    }

    public int getIdCount() {
        return idCount;
    }

    public void setIdCount(int idCount) {
        this.idCount = idCount;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }


    public List<TreeNode> getSortedTodos(boolean forToday){

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

    private List<Todo> getTodosWithParrentId(int id, boolean isForToday) {

        List<Todo> todosToReturn = new ArrayList<>();


        for (Todo todo: allTodos) {
            if (todo.isForToday() == isForToday) {
                if (todo.getId() == id) {
                    todosToReturn.add(todo);
                }
            }
        }
        return todosToReturn;
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
}
