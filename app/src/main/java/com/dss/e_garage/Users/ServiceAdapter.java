package com.dss.e_garage.Users;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dss.e_garage.ImageViewer;
import com.dss.e_garage.ModelGarage;
import com.dss.e_garage.ModelService;
import com.dss.e_garage.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    List<ModelService> slist;
    Context context;

    public ServiceAdapter(List<ModelService> slist, Context context) {
        this.slist = slist;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_service,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        viewHolder.tv_nametag.setText("Garage:");
        int i=pos;
        FirebaseDatabase.getInstance().getReference("Garages").child(slist.get(i).getGID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelGarage gm=snapshot.getValue(ModelGarage.class);
                viewHolder.tv_Gname.setText(gm.getGname());
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String dateString = formatter.format(new Date(Long.valueOf(slist.get(i).getDate())));
                viewHolder.tv_date.setText(dateString);
                viewHolder.tv_amount.setText(slist.get(i).getAmount());
                viewHolder.tv_sid.setText(slist.get(i).getOrderId());
                viewHolder.fab_call.setTag(gm.getGmob());
                viewHolder.fab_nav.setTag(gm.getGlatlang());
                viewHolder.fab_detail.setTag(slist.get(i).getBillUrl());
                viewHolder.tv_vno.setText(slist.get(i).getVno());
                switch (slist.get(i).getStatus().toString()){
                    case "In-Progress":
                        viewHolder.cv_order.setCardBackgroundColor(Color.parseColor("#80ff0000"));
                        break;
                    case "Completed":
                        viewHolder.cv_order.setCardBackgroundColor(Color.parseColor("#804CAF50"));
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewHolder.fab_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.fab_detail.getTag().toString()!=null&&!viewHolder.fab_detail.getTag().toString().equals("-")) {
                    Intent i = new Intent(context, ImageViewer.class);
                    i.putExtra("url", viewHolder.fab_detail.getTag().toString());
                    context.startActivity(i);
                }
            }
        });
        viewHolder.fab_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latlan=viewHolder.fab_nav.getTag().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q="+latlan+" (" + viewHolder.tv_Gname.getText() + ")"));
                context.startActivity(intent);
            }
        });
        viewHolder.fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",viewHolder.fab_call.getTag().toString(), null));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return slist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_sid,tv_date,tv_Gname,tv_amount,tv_nametag,tv_vno;
        CardView cv_order;
        FloatingActionButton fab_call,fab_nav,fab_detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fab_call=itemView.findViewById(R.id.fab_call);
            cv_order=itemView.findViewById(R.id.cv_main);
            tv_sid=itemView.findViewById(R.id.tv_servid);
            tv_date=itemView.findViewById(R.id.tv_date);
            tv_Gname=itemView.findViewById(R.id.tv_Gname);
            tv_amount=itemView.findViewById(R.id.tv_amount);
            fab_nav=itemView.findViewById(R.id.fab_nav);
            fab_detail=itemView.findViewById(R.id.fab_detail);
            tv_nametag=itemView.findViewById(R.id.tv_nametag);
            tv_vno=itemView.findViewById(R.id.tv_vno);


        }
    }
}
