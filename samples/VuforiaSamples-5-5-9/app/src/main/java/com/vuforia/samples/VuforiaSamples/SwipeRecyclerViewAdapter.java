package com.vuforia.samples.VuforiaSamples;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.vuforia.samples.VuforiaSamples.models.Offer;

import java.util.ArrayList;

public class SwipeRecyclerViewAdapter extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapter.SimpleViewHolder> {


    private Context mContext;
    private ArrayList<Offer> offerList;

    public SwipeRecyclerViewAdapter(Context context, ArrayList<Offer> objects) {
        this.mContext = context;
        this.offerList = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final Offer item = offerList.get(position);



        //viewHolder.tvName.setText((item.getName()) + "  -  Row Position " + position);
        //viewHolder.tvEmailId.setText(item.getEmailId());


        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        // Drag From Left
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        // Drag From Right
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));


        // Handling different events when swiping
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        /*viewHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((((SwipeLayout) v).getOpenStatus() == SwipeLayout.Status.Close)) {
                    //Start your activity

                    Toast.makeText(mContext, " onClick : " + item.getName() + " \n" + item.getEmailId(), Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, " onClick : " + item.getOfferDescription() + " \n" + item.getRetailvalue(), Toast.LENGTH_SHORT).show();
            }
        });



        viewHolder.tvBankOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                offerList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, offerList.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Banked " + viewHolder.tvBankOffer.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        viewHolder.tvPassOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                offerList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, offerList.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Passed " + viewHolder.tvPassOffer.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(viewHolder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    //  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tvBankOffer, tvPassOffer, tvOfferDescription, tvPriceRangeId, tvDiscount, tvExpires;
        ImageView thumbnail;

        LinearLayout rating;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvBankOffer = (TextView) itemView.findViewById(R.id.bank_offer);
            tvPassOffer = (TextView) itemView.findViewById(R.id.pass_offer);
            tvOfferDescription = (TextView) itemView.findViewById(R.id.offer_description);
            tvPriceRangeId = (TextView) itemView.findViewById(R.id.price_range_id);
            tvDiscount = (TextView) itemView.findViewById(R.id.discount);
            tvExpires = (TextView) itemView.findViewById(R.id.expires);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);;


        }
    }
}
