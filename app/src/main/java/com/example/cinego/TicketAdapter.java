package com.example.cinego;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<Ticket> ticketList;

    public TicketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket_list, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        // Đổ dữ liệu vào View
        holder.tvMovieName.setText(ticket.getMovieName());
        holder.imgPosterTicket.setImageResource(ticket.getPosterResId());

        // Nếu bạn đã thêm ID ở Bước 1 thì bỏ comment 3 dòng dưới này ra nhé:
        // holder.tvCinemaName.setText(ticket.getCinemaName());
        // holder.tvTime.setText(ticket.getTime());
        // holder.tvSeatInfo.setText("Ghế: " + ticket.getSeats());

        // Bắt sự kiện bấm vào nút "Xem vé"
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Đang mở vé: " + ticket.getMovieName(), Toast.LENGTH_SHORT).show();
            // (Sau này có thể dùng Intent chuyển sang TicketActivity để xem mã QR)
        });
    }

    @Override
    public int getItemCount() {
        return ticketList != null ? ticketList.size() : 0;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPosterTicket;
        TextView tvMovieName, tvCinemaName, tvTime, tvSeatInfo;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPosterTicket = itemView.findViewById(R.id.imgPosterTicket);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);

            // Nếu bạn đã thêm ID ở Bước 1 thì bỏ comment 3 dòng dưới này ra:
            // tvCinemaName = itemView.findViewById(R.id.tvCinemaName);
            // tvTime = itemView.findViewById(R.id.tvTime);
            // tvSeatInfo = itemView.findViewById(R.id.tvSeatInfo);
        }
    }
}