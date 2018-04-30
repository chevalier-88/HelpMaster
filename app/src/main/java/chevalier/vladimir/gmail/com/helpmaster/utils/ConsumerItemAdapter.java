package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;


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
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
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
        holder.imgPhoto.setImageURI(Uri.parse(item.getPathToPhoto()));
        if ((position % 2) != 0) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.item_even_background));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.item_not_even_background));
        }

        return convertView;
    }
}
