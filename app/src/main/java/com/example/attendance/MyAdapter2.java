package com.example.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    private List<Timetable> timetableList;
    private Context ct;

    public MyAdapter2(FragmentActivity activity, List<Timetable> timetableList) {
        this.timetableList = timetableList;
        this.ct = activity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.my_row2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Timetable timetable = timetableList.get(position);

        holder.unit_Name.setText(timetable.getName());
        holder.unit_Code.setText(timetable.getCode());
        holder.unit_Lecturer.setText(timetable.getLecturer());
        holder.unit_Venue.setText(timetable.getVenue());
        holder.unit_Time.setText(timetable.getTime());

    }

    @Override
    public int getItemCount() {
        return timetableList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView unit_Name, unit_Code, unit_Lecturer, unit_Venue, unit_Time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unit_Name = itemView.findViewById(R.id.unit_Name);
            unit_Code = itemView.findViewById(R.id.unit_Code);
            unit_Lecturer = itemView.findViewById(R.id.unit_Lecturer);
            unit_Venue = itemView.findViewById(R.id.unit_Venue);
            unit_Time = itemView.findViewById(R.id.unit_Time);
        }
    }

}
