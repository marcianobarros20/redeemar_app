package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.BrowseOffersActivity;
import com.tier5.redeemar.RedeemarConsumerApp.CategoryActivity;
import com.tier5.redeemar.RedeemarConsumerApp.LoginActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.SearchResultActivity;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.ImageDownloadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.database.DatabaseHelper;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.SimpleViewHolder> {

    private static final String LOGTAG = "SwipeRecyclerView";
    private Context mContext;
    private ArrayList<Category> categoryList;
    private SharedPreferences sharedpref;
    private String mViewType;
    private Resources res;
    private SharedPreferences.Editor editor;
    Typeface myFont;
    private int catId;
    private String catName = "";


    private DatabaseHelper db;


    public CategoryViewAdapter(Context context) {
        this.mContext = context;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        editor = sharedpref.edit();
        db = new DatabaseHelper(mContext);
    }


    public void setCategories(ArrayList<Category> listCategories) {
        this.categoryList = listCategories;
        Log.d(LOGTAG, "Sub Categories count: "+listCategories.size());
        //update the adapter to reflect the new set of Offers
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2 * 2;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(LOGTAG, "View Type: "+viewType);
        mViewType = "list";

        if(sharedpref.getString(res.getString(R.string.spf_view_type), null) != null) {
            mViewType = sharedpref.getString(res.getString(R.string.spf_view_type), "");
        }

        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);
        myFont = Typeface.createFromAsset(view.getResources().getAssets(),  view.getResources().getString(R.string.default_font));
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

        final Category item = categoryList.get(position);
        catName = item.getCatName();
        Log.d(LOGTAG, "Sub category is: "+catName);
        viewHolder.catName.setText(catName);

        viewHolder.catName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Resources res = view.getResources();
                sharedpref = view.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

                catId = item.getId();
                Log.d(LOGTAG, "Category Id: "+categoryList.size());
                categoryList = (ArrayList<Category>) db.getCategories(catId);
                Log.d(LOGTAG, "No. of categories: "+categoryList.size());
                if(categoryList.size() > 0) {
                    Log.d(LOGTAG, "Category Level is set as: "+2);
                    editor.putInt(res.getString(R.string.spf_category_level), 2); // Storing Category Level
                    editor.commit(); // commit changes
                    setCategories(categoryList);
                }
                else {

                    Log.d(LOGTAG, "Before SearchResultActivity");

                    Intent catIntent = new Intent((Activity)mContext, SearchResultActivity.class);
                    catIntent.putExtra(res.getString(R.string.ext_redir_to), "CategoryOffers");
                    catIntent.putExtra(res.getString(R.string.ext_category_name), catName);
                    catIntent.putExtra(res.getString(R.string.ext_category_id), catId);
                    mContext.startActivity(catIntent);
                    ((Activity)mContext).finish();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView catName;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            catName = (TextView) itemView.findViewById(R.id.cat_name);
        }
    }


}
