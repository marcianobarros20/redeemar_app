package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.ViewHolder.BankedViewHolder;
import com.tier5.redeemar.RedeemarConsumerApp.ViewHolder.BrandViewHolder;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Banked;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Brand;
import java.util.Iterator;
import java.util.List;

public class BankedOffersAdapter extends ExpandableRecyclerAdapter<BrandViewHolder, BankedViewHolder> {

    private static final String LOGTAG = "BankedOfferAdapter";
    private LayoutInflater mInflator;
    private Context mContext;

    public BankedOffersAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        Iterator it = parentItemList.iterator();
        if(it.hasNext()) {
            Brand brand = (Brand) it.next();
            Log.d(LOGTAG, "My Reedemar List Parent Items: "+brand.getCompanyName());
        }

        mInflator = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public BrandViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {

        View recipeView = mInflator.inflate(R.layout.item_header, parentViewGroup, false);
        return new BrandViewHolder(recipeView);
    }

    @Override
    public BankedViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View ingredientView = mInflator.inflate(R.layout.my_swipe_row_item, childViewGroup, false);
        return new BankedViewHolder(ingredientView);
    }

    @Override
    public void onBindParentViewHolder(BrandViewHolder brandViewHolder, int position, ParentListItem parentListItem) {
        Brand brand = (Brand) parentListItem;
        brandViewHolder.bind(brand, mContext);
    }

    @Override
    public void onBindChildViewHolder(BankedViewHolder bankedViewHolder, int position, Object childListItem) {

        Log.d(LOGTAG, "My Reedemar List Child Items: "+childListItem.toString());
        Banked banked = (Banked) childListItem;
        bankedViewHolder.bind(banked, mContext);
    }

}
