package dattvph16984.fpoly.firebasedemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookAdater extends RecyclerView.Adapter<BookAdater.UserViewHolder>{
    private List<Book> list;
    private Context mContext;
   // private IClickListener mClickListener;

    public void setData(ArrayList list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public interface IClickListener{
        void onClickDeltete(Book book);

    }

    public BookAdater(Context mContext, List<Book> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BookAdater.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserViewHolder userViewHolder = new UserViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_book,parent,false));
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdater.UserViewHolder holder, int position) {
        Book book = list.get(position);
        holder.tv_tenSach.setText("Tên sách: "+book.getName());
        holder.tvTenTG.setText("Tên tác giả: "+book.getAuthor());
        holder.tvGia.setText("Giá: "+book.getPrice());
        Glide.with(mContext)
                .load(book.getImages())
                .centerCrop()
                .into(holder.imgBook);
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailBookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("book", book);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        holder.img_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               // mClickListener.
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tv_tenSach,tvTenTG,tvGia;
        ImageView imgBook;
        LinearLayout layoutItem;
        ImageView img_delete,img_update;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tenSach = itemView.findViewById(R.id.tv_tenSach);
            tvTenTG = itemView.findViewById(R.id.tv_tacGia);
            tvGia = itemView.findViewById(R.id.tv_Gia);
            imgBook = itemView.findViewById(R.id.img_book);
            layoutItem = itemView.findViewById(R.id.layout_item);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_update =itemView.findViewById(R.id.img_update);
        }
    }
}
