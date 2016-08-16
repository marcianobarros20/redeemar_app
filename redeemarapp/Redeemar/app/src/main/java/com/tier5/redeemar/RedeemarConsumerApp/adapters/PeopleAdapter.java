package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.daimajia.swipe.SwipeLayout;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.CustomVolleyRequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends ExpandableRecyclerAdapter<PeopleAdapter.PeopleListItem> {

    private static final String LOGTAG = "PeopleAdapter";
    public static final int TYPE_PERSON = 1001;
    private ArrayList<Offer> offerList;
    Typeface myFont;

    public PeopleAdapter(Context context) {
        super(context);

        setItems(getSampleItems());
    }

    public static class PeopleListItem extends ExpandableRecyclerAdapter.ListItem {
        public String Text;

        public PeopleListItem(String group) {
            super(TYPE_HEADER);

            Text = group;
        }

        public PeopleListItem(String first, String last) {
            super(TYPE_PERSON);

            Text = first + " " + last;
        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.thumbnail));
            //name = (TextView) view.findViewById(R.id.eventName);
        }

        public void bind(int position) {
            super.bind(position);

            //name.setText(visibleItems.get(position).Text);
        }
    }

    public class PersonViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView tvValidateOffer, tvOfferDescription, tvRetailValue, tvDiscount, tvPayValue, tvDistance, tVOnDemand;
        private NetworkImageView thumbnail;
        private ImageView mapIcon;
        private ImageLoader mImageLoader;
        private LinearLayout distanceLayout, discountLayout;

        public PersonViewHolder(View viewHolder) {
            super(viewHolder);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvValidateOffer = (TextView) itemView.findViewById(R.id.validate_offer);
            tvOfferDescription = (TextView) itemView.findViewById(R.id.offer_description);
            tvRetailValue = (TextView) itemView.findViewById(R.id.retail_value);
            tvDiscount = (TextView) itemView.findViewById(R.id.discount);
            tvPayValue = (TextView) itemView.findViewById(R.id.pay_value);
            tvDistance = (TextView) itemView.findViewById(R.id.distance);
            mapIcon = (ImageView) itemView.findViewById(R.id.map_icon);
            tVOnDemand = (TextView) itemView.findViewById(R.id.on_demand);
            thumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);


            distanceLayout = (LinearLayout) itemView.findViewById(R.id.distance_layout);
            discountLayout = (LinearLayout) itemView.findViewById(R.id.discount_layout);
        }

        public void bind(int position) {

            ///name.setText(visibleItems.get(position).Text);

            String discDesc = "";
            StringBuilder sb = new StringBuilder(14);
            StringBuilder esb = new StringBuilder(14);
            String address_distance = "";

            String perc_sym = mContext.getResources().getString(R.string.percentage_symbol);
            String off = mContext.getResources().getString(R.string.off);
            String disc = mContext.getResources().getString(R.string.discount);
            String cur_sym = mContext.getResources().getString(R.string.currency_symbol);
            String save = mContext.getResources().getString(R.string.save);
            String expires_in = mContext.getResources().getString(R.string.expires_in);
            String days = mContext.getResources().getString(R.string.days);

            String distance_unit = mContext.getResources().getString(R.string.distance_unit);


            final Offer item = offerList.get(position);

            String offer_desc = item.getOfferDescription();

            if(item.getLocation() != null && !item.getLocation().equalsIgnoreCase("null")) {
                address_distance = item.getLocation() + " ";
                Log.d(LOGTAG, "Browse Location: "+address_distance);
            }
            else {
                address_distance = item.getAddress() + " ";
            }

            if(!item.getDistance().equalsIgnoreCase("")) {
                Log.d(LOGTAG, "Browse Distance: "+item.getDistance());
                address_distance = address_distance + item.getDistance() + " miles";
            }



            if(offer_desc.length() > 50)
                tvOfferDescription.setText(offer_desc.substring(0, 50)+"...");
            else
                tvOfferDescription.setText(offer_desc);


            if(item.getRetailvalue() > 0)
                tvRetailValue.setText(cur_sym+String.valueOf(item.getRetailvalue()));

            if(item.getPayValue() > 0)
                tvPayValue.setText(cur_sym+(String.valueOf(item.getPayValue())));



            if(address_distance.equalsIgnoreCase("")) {
                distanceLayout.setVisibility(View.INVISIBLE);
            }
            else {

                tvDistance.setText(address_distance);
                distanceLayout.setVisibility(View.VISIBLE);
            }

            if(item.getOnDemand() == 1) {
                tVOnDemand.setVisibility(View.VISIBLE);
            }
            else {
                tVOnDemand.setVisibility(View.GONE);
            }

            tvOfferDescription.setTypeface(myFont);
            tvRetailValue.setTypeface(myFont);
            tvPayValue.setTypeface(myFont);
            tvDistance.setTypeface(myFont);
            tvDiscount.setTypeface(myFont);

            tvRetailValue.setPaintFlags(tvRetailValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int valCalc = item.getValueCalculate();
            Double discVal = item.getDiscount();
            String imageUrl = item.getImageUrl();

            if(discVal > 0) {
                discountLayout.setVisibility(View.VISIBLE);
            }
            else {
                discountLayout.setVisibility(View.INVISIBLE);
            }


            switch(valCalc)
            {
                case 1 :
                    sb.append(cur_sym).append(discVal).append(" ").append(off);
                    break;
                case 2 :
                    sb.append(discVal).append(perc_sym).append(" ").append(off);
                    break;
                case 3 :
                    sb.append(cur_sym).append(discVal).append(" ").append(disc);
                    break;
                case 4 :
                    sb.append(discVal).append(perc_sym).append(" ").append(disc);
                    break;
                case 5 :
                    sb.append(save).append(" ").append(cur_sym).append(discVal);
                    break;
                case 6 :
                    sb.append(save).append(" ").append(discVal).append(perc_sym);
                    break;
                default :
                    sb.append(cur_sym).append(discVal).append(" ").append(off);
            }


            if(discVal > 0) {
                tvDiscount.setText(sb);
                tvDiscount.setVisibility(View.VISIBLE);
            }
            else {
                tvDiscount.setVisibility(View.GONE);
            }

            mImageLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();

            // Instantiate the RequestQueue.

            if(imageUrl != "") {
                imageUrl = UrlEndpoints.serverBaseUrl + imageUrl;
                mImageLoader.get(imageUrl, ImageLoader.getImageListener(thumbnail,
                        R.drawable.icon_watermark, android.R.drawable
                                .ic_dialog_alert));
                thumbnail.setImageUrl(imageUrl, mImageLoader);
            }

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.item_header, parent));
            case TYPE_PERSON:
            default:
                return new PersonViewHolder(inflate(R.layout.browse_swipe_row_item, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder viewHolder, int position) {

        Log.d(LOGTAG, "Item View Type: "+getItemViewType(position));
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) viewHolder).bind(position);
                break;
            case TYPE_PERSON:
            default:
                ((PersonViewHolder) viewHolder).bind(position);
                break;
        }
    }



    public void setOffers(ArrayList<Offer> listOffers) {

        Log.d(LOGTAG, "Offer lists now: "+listOffers.size());
        this.offerList = listOffers;
        //update the adapter to reflect the new set of Offers
        notifyDataSetChanged();


    }



    private List<PeopleListItem> getSampleItems() {
        List<PeopleListItem> items = new ArrayList<>();

        items.add(new PeopleListItem("Friends"));
        items.add(new PeopleListItem("Bill", "Smith"));
        items.add(new PeopleListItem("John", "Doe"));
        items.add(new PeopleListItem("Frank", "Hall"));
        items.add(new PeopleListItem("Sue", "West"));
        items.add(new PeopleListItem("Family"));
        items.add(new PeopleListItem("Drew", "Smith"));
        items.add(new PeopleListItem("Chris", "Doe"));
        items.add(new PeopleListItem("Alex", "Hall"));
        items.add(new PeopleListItem("Associates"));
        items.add(new PeopleListItem("John", "Jones"));
        items.add(new PeopleListItem("Ed", "Smith"));
        items.add(new PeopleListItem("Jane", "Hall"));
        items.add(new PeopleListItem("Tim", "Lake"));
        items.add(new PeopleListItem("Colleagues"));
        items.add(new PeopleListItem("Carol", "Jones"));
        items.add(new PeopleListItem("Alex", "Smith"));
        items.add(new PeopleListItem("Kristin", "Hall"));
        items.add(new PeopleListItem("Pete", "Lake"));

        return items;
    }
}
