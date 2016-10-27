package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;
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
    private RecyclerView mRecyclerView;
    private int lastPos = -1;
    private boolean toggle = false;
    public BankedOffersAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        /*Iterator it = parentItemList.iterator();
        if(it.hasNext()) {
            Brand brand = (Brand) it.next();
            Log.d(LOGTAG, "My Reedemar List Parent Items: "+brand.getCompanyName());
        }*/

        mInflator = LayoutInflater.from(context);
        mContext = context;

    }

    @Override
    public BrandViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {

        View brandView = mInflator.inflate(R.layout.item_header, parentViewGroup, false);

        mRecyclerView = (RecyclerView) parentViewGroup;

        return new BrandViewHolder(brandView);
    }

    @Override
    public BankedViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View offerView = mInflator.inflate(R.layout.my_swipe_row_item, childViewGroup, false);
        return new BankedViewHolder(offerView);
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

    /*@Override
    public void onParentListItemExpanded(int position) {
        List<? extends ParentListItem> parentItemList = this.getParentItemList();
        collapseAllParents();
        int finalPos = position;
        if (lastPos != -1 && lastPos < position) {
            finalPos = position - parentItemList.get(lastPos).getChildItemList().size();
        }
        expandParent(finalPos);

        mRecyclerView.smoothScrollToPosition(finalPos);
        lastPos = position;
    }
*/

    @Override
    public void onParentListItemExpanded(int position) {


        if(lastPos == position && !toggle) {
            collapseParent(position);
            toggle = true;
        }
        else {

            toggle = false;
            Object parent = mItemList.get(position);
            collapseAllParents();
            position = mItemList.indexOf(parent);
            LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            llm.scrollToPositionWithOffset(position, 0);
            // Alternatively keep track of the single item that is expanded and explicitly collapse that row (more efficient)
            super.onParentListItemExpanded(position);
        }


        lastPos = position;
    }


    /*@Override
    public void onParentListItemExpanded(int position) {
        Object parent = mItemList.get(position);
        collapseAllParents();    // Alternatively keep track of the single item that is expanded and explicitly collapse that row (more efficient)
        expandParent(((ParentWrapper) parent).getParentListItem());
        super.onParentListItemExpanded(position);
        mRecyclerView.smoothScrollToPosition(position+1);
    }*/

}
