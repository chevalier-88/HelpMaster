package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;

public class EventItemAdapter extends BaseAdapter {

    private Context context;
    private List<EventItem> objects;

    public EventItemAdapter(Context context, List<EventItem> objects) {
        this.context = context;
        this.objects = objects;
    }

    class ViewHolder {
        TextView tvDate;
        TextView tvService;
        TextView tvCostumer;

        public ViewHolder(View view) {
            tvDate = (TextView) view.findViewById(R.id.id_cie_date);
            tvService = (TextView) view.findViewById(R.id.id_cie_service);
            tvCostumer = (TextView) view.findViewById(R.id.id_cie_consumer);
            view.setTag(this);
        }
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public EventItem getItem(int position) {
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
                    R.layout.custom_item_event, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        EventItem item = getItem(position);
        holder.tvDate.setText(item.getDate());
        holder.tvService.setText(item.getService());
        holder.tvCostumer.setText(item.getConsumer());

        return convertView;
    }

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        EventHolder holder;
//
//        String date = this.getItem(position).getDate();
//        String service = this.getItem(position).getService();
//        String consumer = this.getItem(position).getConsumer();
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        convertView = inflater.inflate(resource, parent, false);
//
//
//        holder = new EventHolder();
//
//        holder.tvDate = (TextView) convertView.findViewById(R.id.id_cie_date);
//        holder.tvService = (TextView) convertView.findViewById(R.id.id_cie_service);
//        holder.tvCostumer = (TextView) convertView.findViewById(R.id.id_cie_consumer);
//
//
//        holder.tvDate.setText(date);
//        holder.tvService.setText(service);
//        holder.tvCostumer.setText(consumer);
//
//        return convertView;
//    }

}










