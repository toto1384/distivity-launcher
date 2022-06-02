package com.distivity.productivitylauncher.Pojos;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class TreeNode implements Cloneable {
    private Todo content;
    private TreeNode parent;
    private List<TreeNode> childList;
    private boolean isExpand;
    //the tree high
    private int height = UNDEFINE;
    private OnChildChangeListener onChildChangeListener;

    public void setOnChildChangeListener(OnChildChangeListener onChildChangeListener) {
        this.onChildChangeListener = onChildChangeListener;
    }

    private static final int UNDEFINE = -1;

    public TreeNode(@NonNull Todo content) {
        this.content = content;
        this.childList = new ArrayList<>();
    }

    public int getHeight() {
        if (isRoot())
            height = 0;
        else if (height == UNDEFINE)
            height = parent.getHeight() + 1;
        return height;
    }

    public TreeNode  removeChild(TreeNode child){
        childList.remove(child);
        if (onChildChangeListener!=null)onChildChangeListener.onChildChangeListener(childList.size());
        return this;
    }

    private int getAllChildCount(TreeNode todo){
        int allChildSize = todo.childList.size();

        if (todo.isExpand){
            for (TreeNode treeNode: todo.childList){
                allChildSize = allChildSize + getAllChildCount(treeNode);
            }
        }


        return allChildSize;
    }


    public int getAllChildsSize(){
        return getAllChildCount(this);
    }



    public interface OnChildChangeListener{
        public void onChildChangeListener(int childCount);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return childList == null || childList.isEmpty();
    }

    public TreeNode setContent(Todo content) {
        this.content = content;
        return this;
    }

    public Todo getContent() {
        return content;
    }

    public List<TreeNode> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeNode> childList) {
        this.childList.clear();
        for (TreeNode treeNode : childList) {
            addChild(treeNode);
        }
        if (onChildChangeListener!=null)onChildChangeListener.onChildChangeListener(childList.size());
    }

    public void setChecked(boolean checked){
        setContent(getContent().setChecked(checked));
    }

    public TreeNode addChild(TreeNode node) {
        if (childList == null)
            childList = new ArrayList<>();
        node.parent = this;
        childList.add(node);
        if (onChildChangeListener!=null)onChildChangeListener.onChildChangeListener(childList.size());
        return this;
    }

    public boolean toggle() {
        isExpand = !isExpand;
        return isExpand;
    }

    public void collapse() {
        if (isExpand) {
            isExpand = false;
        }
    }

    public void collapseAll() {
        if (childList == null || childList.isEmpty()) {
            return;
        }
        for (TreeNode child : this.childList) {
            child.collapseAll();
        }
    }

    public void expand() {
        if (!isExpand) {
            isExpand = true;
        }
    }

    public void expandAll() {
        expand();
        if (childList == null || childList.isEmpty()) {
            return;
        }
        for (TreeNode child : this.childList) {
            child.expandAll();
        }
    }

    public boolean isExpand() {
        return isExpand;
    }

    public TreeNode setParent(TreeNode parent) {
        this.parent = parent;
         return this;
    }

    public TreeNode getParent() {
        return parent;
    }


    @Override
    public String toString() {
        return "TreeNode{" +
                "content=" + this.content +
                ", parent=" + (parent == null ? "null" : parent.getContent().toString()) +
                ", childList=" + (childList == null ? "null" : childList.toString()) +
                ", isExpand=" + isExpand +
                '}';
    }

    @Override
    public TreeNode clone() throws CloneNotSupportedException {
        TreeNode clone = new TreeNode(this.content);
        clone.isExpand = this.isExpand;
        return clone;
    }
}
