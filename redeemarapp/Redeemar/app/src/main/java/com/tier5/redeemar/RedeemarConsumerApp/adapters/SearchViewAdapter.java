package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.BrowseOffersActivity;
import com.tier5.redeemar.RedeemarConsumerApp.CustomVolleyRequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.LoginActivity;
import com.tier5.redeemar.RedeemarConsumerApp.OfferDetailsActivity;
import com.tier5.redeemar.RedeemarConsumerApp.OfferListActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.SearchResultActivity;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.ImageDownloadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Search;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.id;


public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.ViewHolder> {

    private static final String LOGTAG = "SearchViewAdapter";
    private Context mContext;
    private ArrayList<Search> keywordList;
    private Resources res;
    //Typeface myFont;

    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;



    public SearchViewAdapter(Context context) {
        this.mContext = context;
        res = context.getResources();
    }

    public SearchViewAdapter(Context context, ArrayList<Search> objects) {
        this.mContext = context;
        this.keywordList = objects;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0);

        editor = sharedpref.edit();

    }

    public void setKeyword(ArrayList<Search> listKeyword) {

        this.keywordList = listKeyword;
        Log.d(LOGTAG, "Keyword count: "+listKeyword.size());
        //update the adapter to reflect the new set of Offers
        notifyDataSetChanged();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View view = inflater.inflate(R.layout.search_row, parent, false);
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);
        //myFont = Typeface.createFromAsset(view.getResources().getAssets(),  view.getResources().getString(R.string.default_font));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewAdapter.ViewHolder holder, int position) {
        final Search item = keywordList.get(position);

        if(position == 0)
            holder.divider.setVisibility(View.GONE);


        Log.d(LOGTAG, "Hello "+item.getName());
        //holder.tvTitle.setTypeface(myFont);
        //holder.tvDescription.setTypeface(myFont);
        if(item.getType().equals("1"))
            holder.tvTitle.setText(item.getKeyword()+" in "+item.getName());
        else
            holder.tvTitle.setText(item.getKeyword()+" in Brands");

        holder.tvDescription.setText("");

        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = item.getType();

                String id = item.getId();
                Intent intent = new Intent(v.getContext(), BrowseOffersActivity.class);

                if(type.equals("1")) {
                    editor.putString(res.getString(R.string.spf_redir_action), "CategoryOffers");
                    editor.putString(res.getString(R.string.spf_search_keyword), item.getKeyword());
                    editor.putString(res.getString(R.string.spf_category_name), item.getName());
                    editor.putString(res.getString(R.string.spf_category_id), id);
                    editor.commit();
                }
                else {
                    editor.putString(res.getString(R.string.spf_redir_action), "BrandOffers");
                    editor.putString(res.getString(R.string.spf_search_keyword), item.getKeyword());
                    editor.putString(res.getString(R.string.spf_brand_name), item.getName());
                    editor.putString(res.getString(R.string.spf_redeemer_id), id);
                    editor.commit();
                }


                /*intent.putExtra(v.getContext().getString(R.string.ext_redir_to), "CategoryOffers");
                intent.putExtra(v.getContext().getString(R.string.spf_search_keyword), item.getDescription());
                intent.putExtra(v.getContext().getString(R.string.spf_category_name), item.getCategoryName());
                intent.putExtra(v.getContext().getString(R.string.spf_category_id), id);*/
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                v.getContext().startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return keywordList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription;
        private View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            divider = (View) itemView.findViewById(R.id.divider);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvDescription = (TextView) itemView.findViewById(R.id.description);
        }
    }


}
