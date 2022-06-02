package com.distivity.productivitylauncher.Adapters;


import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.distivity.productivitylauncher.Activities.HomeActivity;
import com.distivity.productivitylauncher.AppExecutors;
import com.distivity.productivitylauncher.CustomCheckbox;
import com.distivity.productivitylauncher.Database.DaoHelper;
import com.distivity.productivitylauncher.Pojos.CustomRunnable;
import com.distivity.productivitylauncher.Pojos.MenuActions;
import com.distivity.productivitylauncher.Pojos.References;
import com.distivity.productivitylauncher.Pojos.Todo;
import com.distivity.productivitylauncher.Pojos.TreeNode;
import com.distivity.productivitylauncher.R;
import com.distivity.productivitylauncher.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.CustomViewHolder> {


    private List<TreeNode> mData;
    private LayoutInflater mInflater;
    private HomeActivity context;

    private boolean isForToday ;
    private int focusItemId = -1;
    private LinearLayoutManager layoutManager;


    private boolean automaticallyDeleteOnChecked;
    private boolean deleteCheckedTodoOnCheckAgain;

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;



    public void setData(final boolean isForToday, final List<TreeNode> treeNodes){

        TodoRecyclerViewAdapter.this.isForToday=isForToday;

        AppExecutors.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mData==null){
                    mData=new ArrayList<>();
                }
                mData.clear();
                mData.addAll(treeNodes);
                onDataUpdated(treeNodes.size());
                notifyDataSetChanged(); }
        });



    }


    public TodoRecyclerViewAdapter(final HomeActivity context,
                                   LinearLayoutManager linearLayoutManager, boolean isForToday) {
        this.mInflater = LayoutInflater.from(context);
        this.context=context;
        this.layoutManager=linearLayoutManager;
        this.isForToday=isForToday;
        mData=new ArrayList<>();

        sharedPreferencesLogic();


        DaoHelper.getInstance(context).setOnTodoAdded(new CustomRunnable<Todo>() {
            @Override
            public void onCustomRunnableRun(Todo data) {
                for (int i = 0 ; i<mData.size();i++){

                    TreeNode currentItem = mData.get(i);

                    if (currentItem.getContent().getId()==data.getId()){

                        mData.set(i,currentItem.setContent(data));
                        return;
                    }

                    if (i==mData.size()-1){
                        mData.add(new TreeNode(data));
                        notifyItemRangeInserted(mData.size()-1,mData.size());
                    }
                }
            }
        });
    }

    private void sharedPreferencesLogic() {
        automaticallyDeleteOnChecked= References.getPrefs(context).
                getBoolean(Utils.CONSTANTS.SharedPreferences.AUTOMATICALLY_DELETE_TODO_ON_CHECK,false);

        deleteCheckedTodoOnCheckAgain= References.getPrefs(context).
                getBoolean(Utils.CONSTANTS.SharedPreferences.DELETE_CHECKED_TODOS_ON_CHECK_AGAIN,false);


        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                automaticallyDeleteOnChecked= References.getPrefs(context).
                        getBoolean(Utils.CONSTANTS.SharedPreferences.AUTOMATICALLY_DELETE_TODO_ON_CHECK,false);

                deleteCheckedTodoOnCheckAgain= References.getPrefs(context).
                        getBoolean(Utils.CONSTANTS.SharedPreferences.DELETE_CHECKED_TODOS_ON_CHECK_AGAIN,false);
            }
        };

        References.getPrefs(context).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public void releaseListener(){
        References.getPrefs(context).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public TodoRecyclerViewAdapter .CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout view = (ConstraintLayout) mInflater.inflate(R.layout.todo_list_item, parent, false);
        return new TodoRecyclerViewAdapter .CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder viewHolder, final int i) {
        viewHolder.customCheckBox.setChecked(false,null);
        final TreeNode animal = mData.get(i);

         viewHolder.itemView.setPaddingRelative(animal.getHeight() * 30, 3, 3, 3);



        //CARRET LOGIC

        //DETERMINE IF CAN SHOW CARRET LOGIC
        mData.get(i).setOnChildChangeListener(new TreeNode.OnChildChangeListener() {
            @Override
            public void onChildChangeListener(int childCount) {
                determineIfCanShowCarret(viewHolder,childCount);
            }
        });

        determineIfCanShowCarret(viewHolder,animal.getChildList().size());


         viewHolder.expandImpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.Views.preventSpamClicks(viewHolder.expandImpandImageView, new Runnable() {
                    @Override
                    public void run() {
                        int positionStart = mData.indexOf(animal) +1;

                        if (!animal.isExpand()) {
                            notifyItemRangeInserted(positionStart, addChildNodes(animal, positionStart));
                            viewHolder.expandImpandImageView.animate().rotationBy(180).start();
                        } else {
                            notifyItemRangeRemoved(positionStart, removeChildNodes
                                    (animal,viewHolder.getLayoutPosition(), true,false,false));
                            viewHolder.expandImpandImageView.animate().rotationBy(-180).start();
                        }
                    }


                },500);
            }
        });



         //EDIT TEXT LOGIC

            viewHolder.editText.setText(animal.getContent().getName());

            //SAVING WITH FOCUS CHANGE LOGIC
            viewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (hasFocus){
                        context.setToUnFocusEditText(viewHolder.editText);
                    }else {
                        DaoHelper.getInstance(context).addTodosToUpdateForLater(animal.getContent()
                                   .setName(viewHolder.editText.getText().toString()),false);

                    }
                }
            });

            //KEY LISTENERS LOGIC
            Utils.EditTexts.setOnDeleteAndEnterKeyListener(viewHolder.editText, new Runnable() {
                @Override
                public void run() {
                    if (viewHolder.editText.getText().toString().equals("")){

                        if (viewHolder.getLayoutPosition()!=0){
                            focusItemId=mData.get(viewHolder.getLayoutPosition()-1).getContent().getId();
                        }

                        Utils.EditTexts.clearFocusOnEditText(viewHolder.editText,context);
                        deleteTodo(viewHolder.getLayoutPosition());


                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    addTodo(mData.indexOf(animal.getParent()),new TreeNode(new Todo
                                    (DaoHelper.getInstance(context).getId(context),"",false,
                                            animal.getParent()!=null?animal.getParent().getContent().getId():-1,
                                            animal.getContent().isForToday())),
                            viewHolder.getLayoutPosition()+animal.getAllChildsSize()+1);

                }
            });

            //FOCUS ON EDIT TEXT IF NEW LOGIC
            if (animal.getContent().getId()==focusItemId){
                layoutManager.scrollToPosition(viewHolder.getLayoutPosition());
                Utils.EditTexts.requestFocusOnEditText(viewHolder.editText,context);
                focusItemId= -1;

            }



        //OPTIONS LOGIC
            final ArrayList<MenuActions> menuActions = new ArrayList<>();

            menuActions.add(new MenuActions(context.getResources().getDrawable(R.drawable.ic_edit), "Edit Todo", new Runnable() {
                @Override
                public void run() {
                    Utils.CustomPopups.showAddTodoPopup(context,animal.getContent(),animal.getContent().isForToday());
                }
            }));

            menuActions.add(new MenuActions(context.getResources().getDrawable(R.drawable.ic_add), "Add subtodo", new Runnable() {
                @Override
                public void run() {
                    if (!animal.isExpand()){
                        viewHolder.expandImpandImageView.performClick();}


                    addTodo(viewHolder.getLayoutPosition(),new TreeNode(new Todo(DaoHelper.getInstance(context).
                                    getId(context),"",false,
                                    animal.getContent().getId(),isForToday)),
                            viewHolder.getLayoutPosition()+animal.getAllChildsSize()+1);


                }
            }));

            if (!animal.getContent().isForToday()){
                menuActions.add(new MenuActions(context.getResources().getDrawable(R.drawable.ic_tasks_for_later)
                        , "Make todo for now (with childs)", new Runnable() {
                    @Override
                    public void run() {
                        makeItemForToday(viewHolder.getLayoutPosition());
                    }
                }));
            }


            menuActions.add(new MenuActions(context.getResources().getDrawable(R.drawable.ic_trash_delete),
                    "Delete todo", new Runnable() {
                @Override
                public void run() {

                    deleteTodo(viewHolder.getLayoutPosition());

                }
            }));

            viewHolder.moreImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.Popups.showOptionsPopupWindow(context,viewHolder.moreImageView,menuActions);
                }
            });



        //CHECKBOX LOGIC

        viewHolder.customCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.Views.preventSpamClicks(viewHolder.customCheckBox, new Runnable() {
                    @Override
                    public void run() {

                        if (automaticallyDeleteOnChecked){
                            deleteTodo(viewHolder.getLayoutPosition());
                            return;
                        }

                        if (animal.getContent().isChecked()){
                            if (deleteCheckedTodoOnCheckAgain){
                                deleteTodo(viewHolder.getLayoutPosition());
                            }else {
                                viewHolder.customCheckBox.setChecked(false, new Runnable() {
                                    @Override
                                    public void run() {
                                        animal.setChecked(false);
                                        DaoHelper.getInstance(context).addTodosToUpdateForLater(animal.getContent(),false);
                                    }
                                });
                            }
                        }else {
                            viewHolder.customCheckBox.setChecked(true, new Runnable() {
                                @Override
                                public void run() {
                                    animal.setChecked(true);
                                    DaoHelper.getInstance(context).addTodosToUpdateForLater(animal.getContent(),false);
                                }
                            });
                        }

                    }
                },2000);


            }
        });

        viewHolder.customCheckBox.setChecked(animal.getContent().isChecked(),null);

        //IF THIS SETTING IS ON THEN ON ONE LONG PRESS ON THE CHECKBOX THE ANIMATION IS PLAYED IN REVERSE AND THE
        //TODOz IS UNCHECKED
//        if (deleteCheckedTodoOnCheckAgain){
//            viewHolder.viewOffCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    Toast.makeText(context, "long press", Toast.LENGTH_SHORT).show();
//                    if (animal.getContent().isChecked()){
//                        viewHolder.checkBox.setSpeed(-4);
//                        viewHolder.checkBox.setVisibility(View.INVISIBLE);
//                        viewHolder.viewOffCheckBox.setVisibility(View.VISIBLE);
//                        viewHolder.checkBox.playAnimation();
//                        mData.set(viewHolder.getLayoutPosition(),animal.setContent(animal.getContent().setChecked(false)));
//                        DaoHelper.getInstance(context).addTodoToUpdateForLater(animal.getContent());
//                    }
//                    return false;
//                }
//            });
//        }

    }

    private void makeItemForToday(int position) {
        TreeNode animal = mData.get(position);

        mData.remove(animal);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());

        int newPosition = 0;

        if (position!=0){
            position--;
            newPosition=position;
        }

        if (animal.getChildList().size()!=0){

            notifyItemRangeRemoved(newPosition+1, removeChildNodes
                    (animal,newPosition, true,false,true));

        }

        if (animal.getParent()!=null){
            mData.set(mData.indexOf(animal.getParent()),animal.getParent().removeChild(animal));
        }

        DaoHelper.getInstance(context).addTodosToUpdateForLater(animal.getContent().setForToday(true),false);

        onDataUpdated(getItemCount());
    }

    private void deleteTodo(int position) {

        TreeNode animal = mData.get(position);

        mData.remove(animal);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());

        int newPosition = 0;

        if (position!=0){
            position--;
            newPosition=position;
        }

        if (animal.getChildList().size()!=0){

            notifyItemRangeRemoved(newPosition+1, removeChildNodes
                    (animal,newPosition, true,true,false));

        }


        if (animal.getParent()!=null){
            mData.set(mData.indexOf(animal.getParent()),animal.getParent().removeChild(animal));
        }

        DaoHelper.getInstance(context).addTodoToDeleteForLater(animal.getContent());

        onDataUpdated(getItemCount());

    }


    private void determineIfCanShowCarret(CustomViewHolder viewHolder, int childListSize) {
        if (childListSize==0){
            viewHolder.expandImpandImageView.setVisibility(View.GONE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(viewHolder.convertView);
            constraintSet.connect(R.id.more_todo_list_item,ConstraintSet.BOTTOM,
                    R.id.parrent_todo_list_item,ConstraintSet.BOTTOM,3);
            constraintSet.applyTo(viewHolder.convertView);
        }else {
            viewHolder.expandImpandImageView.setVisibility(View.VISIBLE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(viewHolder.convertView);
            constraintSet.connect(R.id.more_todo_list_item,ConstraintSet.BOTTOM,
                    R.id.expand_impand_image_view_todo_list_item,ConstraintSet.TOP,3);
            constraintSet.applyTo(viewHolder.convertView);
        }
    }

    @Override
    public int getItemCount() {
        Toast.makeText(context, "size"+mData.size(), Toast.LENGTH_SHORT).show();
        return mData.size();}

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        CustomCheckbox customCheckBox;
        ImageView moreImageView;
        ImageView expandImpandImageView;
        EditText editText;
        ConstraintLayout convertView;

        CustomViewHolder(ConstraintLayout convertView) {
            super(convertView);

            this.convertView=convertView;
            customCheckBox= convertView.findViewById( R.id.checkbox_todo_list_item);
            moreImageView = convertView.findViewById(R.id.more_todo_list_item);
            editText = convertView.findViewById(R.id.edit_text_todo_list_item);
            expandImpandImageView = convertView.findViewById(R.id.expand_impand_image_view_todo_list_item);
        }

    }






    public abstract  void onDataUpdated(int dataSize);




    //BOILERPLATE


//    private void deleteTodo(final TreeNode<Todo> animal, final int adapterPosition, boolean checkedNotDeleted) {
//
//        removeTodoFromAdapter(animal,adapterPosition);
//        DaoHelper.getInstance(context).addTodoToDeleteForLater(animal.getContent());
//
//        showChildNodes(animal,adapterPosition,false);
//        for (TreeNode<Todo> todoTreeNode:getAllChilds(animal,false)){
//            DaoHelper.getInstance(context).addTodoToDeleteForLater(todoTreeNode.getContent());
//        }
//
//        Snackbar chocoBar = ChocoBar.builder().setBackgroundColor(context.getResources().getColor(R.color.blackLighter))
//                .setTextSize(15)
//                .setTextColor(Color.parseColor("#FFFFFF"))
//                .setText(checkedNotDeleted?"Todo checked(ᵔᴥᵔ)":"Todo Deleted!")
//                .setMaxLines(4)
//                .setActionTextColor(context.getResources().getColor(R.color.colorAccent))
//                .setActionTextSize(18)
//                .setActionTextTypefaceStyle(Typeface.BOLD_ITALIC)
//                .setDuration(ChocoBar.LENGTH_LONG)
//                .setActionText("UNDO")
//                .setActionClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        insertTodoToAdapter(animal.getContent(),adapterPosition);
//                        DaoHelper.getInstance(context).removerTodoToDeleteForLater(animal.getContent());
//                        for (TreeNode<Todo> todoTreeNode:getAllChilds(animal,false)){
//                            DaoHelper.getInstance(context).removerTodoToDeleteForLater(todoTreeNode.getContent());
//                        }
//                        showChildNodes(animal,adapterPosition,true);
//                    }
//                })
//                .setActivity(context)
//                .build();
//
//        chocoBar.show();
//
//    }

    private int addChildNodes(TreeNode pNode, int startIndex) {
        List<TreeNode> childList = pNode.getChildList();
        int addChildCount = 0;
        for (TreeNode treeNode : childList) {
            mData.add(startIndex + addChildCount++, treeNode);
            if (treeNode.isExpand()) {
                addChildCount += addChildNodes(treeNode, startIndex + addChildCount);
            }
        }
        if (!pNode.isExpand())
            pNode.toggle();
        return addChildCount;
    }

    private int removeChildNodes(TreeNode pNode, int parentNodePosition, boolean shouldToggle, boolean delete,boolean makeForToday) {


        if (pNode.isLeaf()){

            return 0;
        }

        List<TreeNode> childList = pNode.getChildList();


        int removeChildCount =  childList.size();

        for (int i = 0;i<childList.size();i++) {
            TreeNode child =childList.get(i);

            if (child.isExpand()||delete) {
                child.toggle();
                removeChildCount += removeChildNodes(child,
                        parentNodePosition+i , false, delete,makeForToday);

            }
            mData.remove(child);
            if (delete){
                DaoHelper.getInstance(context).addTodoToDeleteForLater(child.getContent());
            }
            if (makeForToday){
                DaoHelper.getInstance(context).addTodosToUpdateForLater(child.getContent().setForToday(true),false);
            }
        }

        if (shouldToggle)
            pNode.toggle();



        return removeChildCount;
    }


    private void addTodo(int parrentPos, TreeNode child,int addToPosition){

        focusItemId=child.getContent().getId();

        if (parrentPos!=-1){
            addNodeToParrent(parrentPos,child);
        }


        DaoHelper.getInstance(context).addTodoToAddForLater(child.getContent(),false);

            mData.add(addToPosition,child);
            notifyItemInserted(addToPosition);
           notifyItemRangeChanged(addToPosition,getItemCount());






    }




    private void addNodeToParrent(int parrentPos, TreeNode child) {
        mData.set(parrentPos,mData.get(parrentPos).addChild(child));
    }


    private void unregisterNodeFromParrent(int parrentPos, TreeNode parrent, TreeNode child) {
        mData.set(parrentPos,parrent.removeChild(child));
    }


}