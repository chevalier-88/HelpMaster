package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.SalaryItem;


public class SalaryItemAdapter extends BaseAdapter {

    private Context context;
    private List<SalaryItem> objects;

    public SalaryItemAdapter(Context context, List<SalaryItem> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public SalaryItem getItem(int position) {
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

    class SalaryHolder {
        TextView tvDate;
        TextView tvService;
        TextView tvConsumerName;
        TextView tvCost;

        public SalaryHolder(View view) {
            tvDate = (TextView) view.findViewById(R.id.id_item_salary_date);
            tvService = (TextView) view.findViewById(R.id.id_item_salary_service);
            tvConsumerName = (TextView) view.findViewById(R.id.id_item_salary_consumer);
            tvCost = (TextView) view.findViewById(R.id.id_item_salary_cost);
            view.setTag(this);
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context,
                    R.layout.custom_item_salary, null);
            new SalaryHolder(convertView);
        }
        SalaryHolder holder = (SalaryHolder) convertView.getTag();
        SalaryItem item = getItem(position);

        String eventDate = item.getDate().split(" ")[0];
        String dateMod = eventDate.split("-")[0] + "-" + context.getResources().getStringArray(R.array.Months)[Integer.parseInt(eventDate.split("-")[1])] + "-" + eventDate.split("-")[2] + " " + "\n" + item.getDate().split(" ")[1];

        holder.tvDate.setText(dateMod);
        holder.tvService.setText(item.getServiceName());
        holder.tvConsumerName.setText(item.getConsumerName());
        holder.tvCost.setText("" + item.getSum());
        if ((position % 2) != 0) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.item_even_background));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.item_not_even_background));
        }

        return convertView;
    }
}
