package dattvph16984.fpoly.firebasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailBookActivity extends AppCompatActivity {
    TextView tv_tenS,tv_tenTG,tv_Gia;
    ImageView avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_book);
        tv_tenS = findViewById(R.id.tv_tenSachDt);
        tv_tenTG = findViewById(R.id.tv_tacGiaDt);
        tv_Gia = findViewById(R.id.tv_GiaDt);
        avatar = findViewById(R.id.avts);
        Intent intent = getIntent();
        Book book = (Book) intent.getSerializableExtra("book");
        tv_tenS.setText("Tên sách: "+book.getName());
        tv_tenTG.setText("Tên tác giả: "+book.getAuthor());
        tv_Gia.setText("Giá: "+book.getPrice());
        Glide.with(this)
                .load(book.getImages())
                .centerCrop()
                .into(avatar);
    }

}