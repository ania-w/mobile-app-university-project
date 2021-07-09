package com.am_lab.View.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.am_lab.Model.DataModel.AngleModel;
import com.am_lab.Model.DataModel.DataModel;
import com.am_lab.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnglesAdapter extends RecyclerView.Adapter<AnglesAdapter.AnglesViewHolder>{
    private List<AngleModel> angles;

    public  AnglesAdapter(List<AngleModel> _Data) {
        this.angles=_Data;
    }

    public void update(DataModel newData) {
        angles.clear();
        angles.addAll(newData.getAngles());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnglesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        return new  AnglesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AnglesViewHolder holder, int position) {
        holder.bind(angles.get(position));
    }

    @Override
    public int getItemCount() {
        return angles.size();
    }

    class AnglesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dataType)
        TextView dataType;

        @BindView(R.id.value)
        TextView value;

        @BindView(R.id.unit)
        TextView unit;

        public  AnglesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(AngleModel data) {
            dataType.setText(data.getType());
            value.setText("Value:    "+data.getValue());
            unit.setText("Unit:     "+data.getUnit());
        }
    }
}