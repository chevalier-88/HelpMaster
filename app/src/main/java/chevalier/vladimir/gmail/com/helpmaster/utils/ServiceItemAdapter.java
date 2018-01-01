package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Service;

/**
 * Created by chevalier on 02.09.17.
 */

public class ServiceItemAdapter extends BaseAdapter {

    private Context context;
    private List<Service> objects;


    public ServiceItemAdapter(Context context, List<Service> objects) {
        this.context = context;
        this.objects = objects;
    }

    class ServiceItemHolder {
        TextView nameService;
        TextView durationService;
        TextView costService;

        public ServiceItemHolder(View view) {
            nameService = (TextView) view.findViewById(R.id.id_custom_item_service_name);
            costService = (TextView) view.findViewById(R.id.id_custom_item_service_cost);
            durationService = (TextView) view.findViewById(R.id.id_custom_item_service_duration);
            view.setTag(this);
        }
    }

    @Override
    public int getCount() {
        return (objects != null ? objects.size() : 0);
    }

    @Override
    public Service getItem(int position) {
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

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context,
                    R.layout.custom_item_service, null);
            new ServiceItemHolder(convertView);
        }
        ServiceItemHolder holder = (ServiceItemHolder) convertView.getTag();
        Service item = getItem(position);

        holder.nameService.setText(item.getNameService());
        holder.durationService.setText("" + item.getDurationService());
        holder.costService.setText("" + item.getCostService());

        return convertView;
    }
}
