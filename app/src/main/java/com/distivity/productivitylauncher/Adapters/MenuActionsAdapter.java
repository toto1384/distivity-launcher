package com.distivity.productivitylauncher.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.distivity.productivitylauncher.Pojos.MenuActions;
import com.distivity.productivitylauncher.R;

import java.util.ArrayList;
import java.util.List;


public class MenuActionsAdapter extends ArrayAdapter {


    private Context mContext;
    private List<MenuActions> menuActions;
    private PopupWindow popupWindow;

    public MenuActionsAdapter(@NonNull Context context, ArrayList<MenuActions> menuActions, PopupWindow popupWindow)  {
        super(context, 0, menuActions);
        mContext = context;
        this.menuActions= menuActions;
        this.popupWindow = popupWindow;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_actions_list_item, null);

        final MenuActions currentMenuAction = menuActions.get(position);

        ImageView imageView = convertView.findViewById( R.id.image_view_menu_actions_list_item);

        TextView textView = convertView.findViewById(R.id.text_view_menu_actions);

        imageView.setImageDrawable(currentMenuAction.getIcon());

        textView.setText(currentMenuAction.getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMenuAction.getToRun()!=null){
                popupWindow.dismiss();
                currentMenuAction.getToRun().run();
                }
            }
        });


        return convertView;

    }
}