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
                    R.layout.custom_item_event, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        EventItem item = getItem(position);

        String eventDate = item.getDate().split(" ")[0];
        String dateMod = eventDate.split("-")[0] + "-" + context.getResources().getStringArray(R.array.Months)[Integer.parseInt(eventDate.split("-")[1])] + "-" + eventDate.split("-")[2] + " " + item.getDate().split(" ")[1];

        holder.tvDate.setText(dateMod);
        holder.tvService.setText(item.getService());
        holder.tvCostumer.setText(item.getConsumer());
        if ((position % 2) != 0) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.item_even_background));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.item_not_even_background));
        }
        return convertView;
    }
}