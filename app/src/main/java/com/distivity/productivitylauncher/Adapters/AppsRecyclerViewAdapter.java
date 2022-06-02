package com.distivity.productivitylauncher.Adapters;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.distivity.productivitylauncher.Activities.HomeActivity;
import com.distivity.productivitylauncher.AppExecutors;
import com.distivity.productivitylauncher.Pojos.MenuActions;
import com.distivity.productivitylauncher.Pojos.References;
import com.distivity.productivitylauncher.R;
import com.distivity.productivitylauncher.Utils;

import java.util.ArrayList;
import java.util.List;


public class AppsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<MenuActions> whiteListedApps = new ArrayList<>();
    private List<MenuActions> nonWhiteListedApps = new ArrayList<>();


    private LayoutInflater mInflater;

    private HomeActivity mContext;

    private OnItemClickListener onItemClickListener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_WHITELISTED_APP = 11;
    private static final int TYPE_NON_WHITELISTED_APP = 1;

    private boolean choseMode ;

    private boolean workMode = false;

    private int PositionToChange;

    private GridLayoutManager appsLayoutManager;

    private boolean showMathProblems ;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(MenuActions item);
    }

    public GridLayoutManager getAppsLayoutManager() {
        return appsLayoutManager;
    }

    public void setWorkMode(boolean workMode) {
        this.workMode = workMode;


        if (!workMode){
            appsLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
            {
                @Override
                public int getSpanSize(int position)
                {

                    return position==0||position==whiteListedApps.size()+1 ? 5:1;
                }
            });
        }else {
            appsLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
        }

        notifyDataSetChanged();
    }
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        setOnItemClickListener(null);
    }

    public void setData( List<MenuActions> whiteListedApps,
                         List<MenuActions> nonWhiteListedApps){


        this.whiteListedApps=whiteListedApps;
        this.nonWhiteListedApps=nonWhiteListedApps;

        notifyDataSetChanged();
    }

    public AppsRecyclerViewAdapter(HomeActivity context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext=context;
        this.workMode=false;

        appsLayoutManager= new GridLayoutManager(mContext,5);

        showMathProblems = References.getPrefs(context).getBoolean(Utils.CONSTANTS.SharedPreferences.MATH_PROBLEMS_ON,false);

        choseMode = false;


    }

    public AppsRecyclerViewAdapter(List<MenuActions> whiteListedApps, HomeActivity mContext,int PositionToChange) {
        this.whiteListedApps = whiteListedApps;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        choseMode = true;
        this.PositionToChange=PositionToChange;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType== TYPE_NON_WHITELISTED_APP){
            return new CustomViewHolder(mInflater.inflate(R.layout.app_list_item,parent,false),TYPE_NON_WHITELISTED_APP);

        }else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            return new CustomViewHolderHeader(new TextView(mContext));
        }else if (viewType == TYPE_WHITELISTED_APP) {
                //inflate your layout and pass it to view holder
                return new CustomViewHolder(mInflater.inflate(R.layout.app_list_item, parent, false),TYPE_WHITELISTED_APP);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int viewHolderPosition) {

        if (viewHolder instanceof CustomViewHolder) {
             final MenuActions currentItem  ;


             if (workMode||choseMode){
                 currentItem=whiteListedApps.get(viewHolderPosition);
             }else {
                 if (viewHolderPosition<=whiteListedApps.size()) {

                     currentItem = whiteListedApps.get(viewHolderPosition - 1);
                 }else {
                     currentItem = nonWhiteListedApps.get(viewHolderPosition-(whiteListedApps.size()+2));
                 }
             }




            ((CustomViewHolder) viewHolder).convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (choseMode){
                        AppExecutors.runOnBackgroundThread(new Runnable() {
                            @SuppressLint("ApplySharedPref")
                            @Override
                            public void run() {
                                References.getPrefs(mContext).edit().putString(Utils.CONSTANTS.
                                                SharedPreferences.X_WHITELISTED_APP+PositionToChange,
                                        Utils.PackageManagerr.getPackNameByAppName(currentItem.getTitle(),mContext)).commit();
                            }
                        });
                    }else {
                        if (showMathProblems&&((CustomViewHolder) viewHolder).Type==TYPE_NON_WHITELISTED_APP){
                            Utils.CustomPopups.openMathProblemDialog(mContext,currentItem);
                        }else {
                            if (currentItem.getToRun()!=null){
                                currentItem.getToRun().run();
                            }
                        }

                    }
                    if (onItemClickListener!=null){
                        onItemClickListener.onItemClick(currentItem);
                    }
                }
            });


            ((CustomViewHolder) viewHolder).imageView.setImageDrawable(currentItem.getIcon());
            ((CustomViewHolder) viewHolder).textView.setText(currentItem.getTitle());

            if (!currentItem.getTitle().equals("Add app")||!choseMode){

                ((CustomViewHolder) viewHolder).convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {

                        if (((CustomViewHolder) viewHolder).Type==TYPE_WHITELISTED_APP){
                            if (onItemClickListener!=null){
                                onItemClickListener.onItemClick(currentItem);
                            }
                            Utils.CustomPopups.openChoseAppPopup(mContext,viewHolder.getAdapterPosition());
                        }else {
                            Utils.PackageManagerr.showInstalledAppDetails(mContext,Utils.PackageManagerr.
                                    getPackNameByAppName(currentItem.getTitle(),mContext));
                        }
                        return true;
                    }
                });
            }


        } else if (viewHolder instanceof CustomViewHolderHeader) {
            //cast holder to VHHeader and set data for header.
            if (whiteListedApps.size()==0){
                ((CustomViewHolderHeader) viewHolder).textView.setVisibility(View.GONE);
            }else {
                if (viewHolder.getLayoutPosition()==0){
                    ((CustomViewHolderHeader) viewHolder).textView.setText("Whitelisted Apps");
                }else {
                    ((CustomViewHolderHeader) viewHolder).textView.setText("Non-whitelisted Apps");
                }

                ((CustomViewHolderHeader) viewHolder).textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                ((CustomViewHolderHeader) viewHolder).textView.setTextColor(Utils.Views.getTextColorPrimary(mContext));
                ((CustomViewHolderHeader) viewHolder).textView.setGravity(Gravity.CENTER_HORIZONTAL);
                ((CustomViewHolderHeader) viewHolder).textView.setPadding(Utils.Views.getPixelsFromDp(5,mContext),
                        Utils.Views.getPixelsFromDp(5,mContext),Utils.Views.getPixelsFromDp(5,mContext),
                        Utils.Views.getPixelsFromDp(5,mContext));
            }


        }



    }

    @Override
    public int getItemViewType(int position) {
        if (choseMode){
            return TYPE_WHITELISTED_APP;
            //IF CHOSE MODE RETURN ONLY THE APPS;
        }else if (workMode){
            return TYPE_WHITELISTED_APP;
        }else {
            if (position==0||position==whiteListedApps.size()+1){
                //RETURN HEADER IF ON POSITION 0 OR AT THE END OF WHITELISTED_APPS
                return TYPE_HEADER;
            }else if (position>=whiteListedApps.size()){
                return TYPE_NON_WHITELISTED_APP;
            }else {
                return TYPE_WHITELISTED_APP;
            }
        }


    }





    @Override
    public int getItemCount() {

        if (choseMode){
            return whiteListedApps.size();
        }else {
            if (workMode){
                return whiteListedApps.size();
            }else {
                return nonWhiteListedApps.size()+whiteListedApps.size()+2;
            }
        }


        }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        TextView textView;

        View convertView;

        int Type;



        CustomViewHolder(View convertView,int type) {
            super(convertView);
            imageView = convertView.findViewById(R.id.image_view_app_list_item);
            textView = convertView.findViewById(R.id.text_view_app_list_item);
            this.convertView=convertView;
            Type = type;
        }

    }

    class CustomViewHolderHeader extends RecyclerView.ViewHolder {
        TextView textView;

        public CustomViewHolderHeader(View itemView) {
            super(itemView);
            textView= (TextView) itemView;
        }
    }
}
