package com.example.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.ui.timetable.Attendance;

import org.json.JSONArray;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

   private List<Attendance> attendanceList;
   private Context ct;

    public MyAdapter(FragmentActivity activity, List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
        this.ct = activity;
    }

    @NonNull
@Override
public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.my_row, viewGroup, false);
        return new MyViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);

        holder.unitid.setText(attendance.getId());
        holder.unitlecturer.setText(attendance.getLecturer());
        holder.unitcode.setText(attendance.getCode());
        holder.datetime.setText(attendance.getDate());
        }

@Override
public int getItemCount() {
        return attendanceList.size();
        }

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView unitid, unitcode, unitlecturer, datetime;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        unitid = itemView.findViewById(R.id.unitId);
        unitcode = itemView.findViewById(R.id.unitCode);
        unitlecturer = itemView.findViewById(R.id.unitLecturer);
        datetime = itemView.findViewById(R.id.dateTime);
    }
}

}
