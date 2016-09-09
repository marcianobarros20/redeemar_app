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
    private int catId, pos=0;
    private String catName = "";


    private DatabaseHelper db;


    public CategoryViewAdapter(Context context) {
        this.mContext = context;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        editor = sharedpref.edit();
        db = new DatabaseHelper(mContext);
    }


    public void setCategories(ArrayList<Category> listCategories, int mCatId) {

        //ArrayList categories = (ArrayList<Category>) db.getCategories(catId);

        Category anyCat = new Category();
        anyCat.setId(mCatId);
        anyCat.setCatName("All");

        listCategories.add(0, anyCat);


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

        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_row, parent, false);
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

                pos = position;

                Resources res = view.getResources();
                sharedpref = view.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

                catId = item.getId();
                catName = item.getCatName();


                Log.d(LOGTAG, "Category Id: "+categoryList.size());
                categoryList = (ArrayList<Category>) db.getCategories(catId);
                Log.d(LOGTAG, "No. of categories: "+categoryList.size());
                Log.d(LOGTAG, "Position: "+view.getId());


                if(categoryList.size() > 0 && pos > 0) {
                    editor.putInt(res.getString(R.string.spf_category_level), 2); // Storing Category Level
                    editor.commit(); // commit changes
                    setCategories(categoryList, catId);
                }
                else {

                    Log.d(LOGTAG, "Before Search Result Activity Cat Id: "+catId);
                    Log.d(LOGTAG, "Before Search Result Activity Cat Name: "+item.getCatName());

                    editor.putInt(res.getString(R.string.spf_category_id), catId);          // Storing Category Id
                    editor.putString(res.getString(R.string.spf_category_name), item.getCatName());   // Storing Category Name
                    editor.commit(); // commit changes

                    Intent catIntent = new Intent((Activity)mContext, SearchResultActivity.class);
                    catIntent.putExtra(res.getString(R.string.ext_redir_to), "CategoryOffers");
                    //catIntent.putExtra(res.getString(R.string.ext_category_name), catName);
                    //catIntent.putExtra(res.getString(R.string.ext_category_id), catId);
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
