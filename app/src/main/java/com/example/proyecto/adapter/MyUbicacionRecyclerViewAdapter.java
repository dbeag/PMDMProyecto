package com.example.proyecto.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.MainActivity;
import com.example.proyecto.MapsFragment;
import com.example.proyecto.R;
import com.example.proyecto.databinding.FragmentUbicacionItemBinding;
import com.example.proyecto.model.Ubicacion;

import java.util.ArrayList;
import java.util.List;

public class MyUbicacionRecyclerViewAdapter extends RecyclerView.Adapter<MyUbicacionRecyclerViewAdapter.ViewHolder> {

    private final List<Ubicacion> mValues;
    private Context context;

    public MyUbicacionRecyclerViewAdapter(Context ctx, ArrayList<Ubicacion> lstUbicaciones) {
        context = ctx;
        mValues = lstUbicaciones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentUbicacionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvName.setText(mValues.get(position).getNombre());
        holder.tvType.setText(mValues.get(position).getTipoUbicacion());
        if (mValues.get(position).getDescripcion() != null) {
            holder.tvDescription.setText(mValues.get(position).getDescripcion());
        } else {
            holder.tvDescription.setText(R.string.app_noDescription);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvType;
        public Ubicacion mItem;
        public final TextView tvDescription;

        public ViewHolder(FragmentUbicacionItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvName;
            tvType = binding.tvType;
            tvDescription = binding.tvDescription;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvName.getText() + "'";
        }
    }

}