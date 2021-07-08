package com.am_lab.View.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.am_lab.Model.DataModel.DataModel;
import com.am_lab.Model.DataModel.SensorModel;
import com.am_lab.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder>{
    private List<SensorModel> sensors;

    public SensorAdapter(List<SensorModel> _Data) {
        this.sensors = _Data;
    }

    public void update(DataModel newData) {
      sensors.clear();
       sensors.addAll(newData.getSensors());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        return new  SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  SensorViewHolder holder, int position) {
        holder.bind(sensors.get(position));
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    class SensorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dataType)
        TextView dataType;

        @BindView(R.id.value)
        TextView value;

        @BindView(R.id.unit)
        TextView unit;

        public  SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(SensorModel data) {
            dataType.setText(data.getType());
            value.setText("Value:    "+data.getValue());
            unit.setText("Unit:     "+data.getUnit());
        }
    }
}
