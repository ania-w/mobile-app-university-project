package com.am_lab.View.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.am_lab.Model.DataModel.DataModel;
import com.am_lab.Model.DataModel.JoystickModel;
import com.am_lab.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JoystickAdapter extends RecyclerView.Adapter<JoystickAdapter.JoystickViewHolder>{
    private List<JoystickModel> Data;

    public JoystickAdapter(List<JoystickModel> _Data) {
        this.Data = _Data;
    }

    public void update(DataModel newData) {
        Data.clear();
        Data.addAll(newData.getJoystick());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JoystickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        return new  JoystickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  JoystickViewHolder holder, int position) {
        holder.bind(Data.get(position));
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    class JoystickViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dataType)
        TextView joystick;

        @BindView(R.id.value)
        TextView XY;

        @BindView(R.id.unit)
        TextView center;

        public  JoystickViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(JoystickModel data) {
            joystick.setText("Joystick:");
            XY.setText("x:   " +data.getY()+ "   y:   "+data.getY());
            center.setText("center:   "+data.getCenter());
        }
    }
}
