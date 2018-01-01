package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;

/**
 * Created by chevalier on 17.09.17.
 */

public class ConsumerItemAdapter extends BaseAdapter {
    private Context context;
    private List<Consumer> objects;

    public ConsumerItemAdapter(Context context, List<Consumer> objects) {
        this.context = context;
        this.objects = objects;
    }

    class ConsumerItemHolder {
        ImageView imgPhoto;
        TextView tvName;
        TextView tvBalance;
        TextView tvDiscount;
        public ConsumerItemHolder(View view) {
            imgPhoto = (ImageView) view.findViewById(R.id.id_item_consumer_img);
            tvName = (TextView) view.findViewById(R.id.id_item_consumer_name);
            tvBalance = (TextView) view.findViewById(R.id.id_item_consumer_balance);
            tvDiscount = (TextView) view.findViewById(R.id.id_item_consumer_discount);
            view.setTag(this);
        }
    }
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Consumer getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position % 3;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context,
                    R.layout.custom_item_consumer, null);
            new ConsumerItemHolder(convertView);
        }
        ConsumerItemHolder holder = (ConsumerItemHolder) convertView.getTag();
        Consumer item = getItem(position);

        holder.tvName.setText(item.getName() + " " + item.getSurname());
        holder.tvBalance.setText("" + item.getBalance());
        if (item.getBalance() > 0) {
            holder.tvBalance.setTextColor(Color.GREEN);
        } else {
            holder.tvBalance.setTextColor(Color.RED);
        }
        holder.tvDiscount.setText("" + item.getDiscount());
        holder.imgPhoto.setImageURI(Uri.parse(new File(item.getPathToPhoto()).exists()?item.getPathToPhoto():
                "android.resource://chevalier.vladimir.gmail.com.helpmaster/" + R.drawable.no_name));
        return convertView;
    }
}