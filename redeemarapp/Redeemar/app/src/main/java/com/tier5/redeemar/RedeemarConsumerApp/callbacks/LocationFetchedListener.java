package com.tier5.redeemar.RedeemarConsumerApp.callbacks;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;

/**
 * Created by Windows on 02-03-2015.
 */
public interface LocationFetchedListener {
    public void onLocationFetched(User user);
}
