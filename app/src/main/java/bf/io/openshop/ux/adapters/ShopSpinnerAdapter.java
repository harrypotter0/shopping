package bf.io.openshop.ux.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.Location;
import bf.io.openshop.entities.Shop;

/**
 * Simple arrayAdapter for shop selection.
 */
public class ShopSpinnerAdapter extends ArrayAdapter<Location> {
    private static final int layoutID = R.layout.list_item_shops;
    private final boolean viewTextWhite;
    private LayoutInflater layoutInflater;
    private List<Location> locations;

    /**
     * Creates an adapter for shop selection.
     *
     * @param activity      activity context.
     * @param locations         list of items.
     * @param viewTextWhite true if text should be white.
     */
    public ShopSpinnerAdapter(Activity activity, List<Location> locations, boolean viewTextWhite) {
        super(activity, layoutID, locations);
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.locations = locations;
        this.viewTextWhite = viewTextWhite;
    }

    public int getCount() {
        return locations.size();
    }

    public Location getItem(int position) {
        return locations.get(position);
    }

    public long getItemId(int position) {
        return locations.get(position).getId();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent, boolean dropdown) {
//        Timber.d("getView Position: " + position + ". ConvertView: " + convertView);
        View v = convertView;
        ListItemHolder holder;

        if (v == null) {
            v = layoutInflater.inflate(layoutID, parent, false);
            holder = new ListItemHolder();
            holder.shopLanguageName = (TextView) v.findViewById(R.id.shop_language_name);
            holder.shopFlagIcon = (ImageView) v.findViewById(R.id.shop_flag_icon);
            v.setTag(holder);
        } else {
            holder = (ListItemHolder) v.getTag();
        }

        Location location = locations.get(position);

        if (dropdown || !viewTextWhite) {
            holder.shopLanguageName.setTextColor(ContextCompat.getColor(getContext(), R.color.textPrimary));
        } else {
            holder.shopLanguageName.setTextColor(ContextCompat.getColor(getContext(), R.color.textIconColorPrimary));
        }

        Picasso.with(getContext()).cancelRequest(holder.shopFlagIcon);
        if (location != null) {
            holder.shopLanguageName.setText(location.getName());
            //Picasso.with(getContext()).load(shop.getFlagIcon()).into(holder.shopFlagIcon);
        }

        return v;
    }

    static class ListItemHolder {
        TextView shopLanguageName;
        ImageView shopFlagIcon;
    }
}