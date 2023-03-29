package dattvph16984.fpoly.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db ; ;
    String TAG = "zzzzzzz";
    List<Book> listBook = new ArrayList<>();
    BookAdater adapter;
    private RecyclerView rcvBook;
    EditText edTenSach,edTacGia,edGia;
    Button btnThem,btn_select;
    private Uri filePath; // đường dẫn file
    // khai báo request code để chọn ảnh
    private final int PICK_IMAGE_REQUEST = 22;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView img_preview;
    String urlImg;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edTenSach = findViewById(R.id.ed_namebook);
        edTacGia = findViewById(R.id.ed_author);
        edGia = findViewById(R.id.ed_price);
        btnThem = findViewById(R.id.btn_add);
        rcvBook = findViewById(R.id.rcv_book);
        img_preview = findViewById(R.id.img_preview);
        btn_select = findViewById(R.id.btn_select_image);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        rcvBook.setLayoutManager(linearLayoutManager);


        // Khởi tạo firebase
        FirebaseApp.initializeApp(MainActivity.this);
        storage = FirebaseStorage.getInstance("gs://fir-demo-a5d0f.appspot.com");
        storageReference = storage.getReference();

        // Khởi tạo làm việc với Firebase
        db  = FirebaseFirestore.getInstance();
        getListAsync();
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBook();
                uploadImage();
                getListAsync();
                Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                adapter = new BookAdater(MainActivity.this,listBook);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
//                rcvBook.setLayoutManager(linearLayoutManager);
                rcvBook.setAdapter(adapter);

                return;

            }
        });
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

    }

    private void onClickDelete() {
        db  = FirebaseFirestore.getInstance();

    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(  Intent.createChooser( intent, "Select Image from here..."), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            // Lấy dữ liệu từ màn hình chọn ảnh truyền về
            filePath = data.getData();
            Log.d("zzzzz", "onActivityResult: " + filePath.toString());
            try {
                // xem thử ảnh , nếu không muốn xem thử thì bỏ đoạn code này
                Bitmap bitmap = MediaStore.Images .Media  .getBitmap( getContentResolver(), filePath);
                img_preview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage() {
        if (filePath != null) {

            // Hiển thị dialog
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Tạo đường dẫn lưu trữ file, images/ là 1 thư mục trên firebase, chuỗi uuid... là tên file, tạm thời có thể phải lên web firebase tạo sẵn thư mục images
            StorageReference ref = storageReference .child(  "Books/" + UUID.randomUUID().toString());

            Log.d(TAG, "uploadImage: " + ref.getPath());

            // Tiến hành upload file
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess( UploadTask.TaskSnapshot taskSnapshot) {
                                    // upload thành công, tắt dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT) .show();

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace(); // có lỗi upload
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(),  Toast.LENGTH_SHORT) .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    // cập nhật tiến trình upload
                                    double progress  = (100.0   * taskSnapshot.getBytesTransferred()  / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage( "Uploaded " + (int) progress + "%");
                                }
                            })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // gọi task để lấy URL sau khi upload thành công
                            return ref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                urlImg = downloadUri.toString();
                                // upload thành công, lấy được url ảnh, ghi ra log. Bạn có thể ghi vào CSdl....
                                Log.d(TAG, "onComplete: url download = " + downloadUri.toString() );

                            } else {
                                // lỗi lấy url download
                            }
                        }
                    });
        }
    }

    void AddBook(){
        Book book = new Book();
        book.name = edTenSach.getText().toString();
        book.author = edTacGia.getText().toString();
        book.price = Integer.parseInt(edGia.getText().toString());
        book.images = urlImg;

        db.collection("Books")
                .add( book )
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("zzzz", "onSuccess: Thêm sách thành công");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("zzzz", "onFailure: lỗi thêm sách");
                        e.printStackTrace();
                    }
                });

    }
    void getListAsync(){

        db.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, "onComplete: " + document.toString());
                                Book book = document.toObject(Book.class);
                                Log.d(TAG, "onComplete: objbook" + book.name + "===" + book.price );
                                listBook.add(book);
                            }
                            // add xong thi update list view
                            UpdateListView();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        // return listBook;
    }

    void UpdateListView(){
        Log.d(TAG, "onCreate: listbook = " + listBook.size());
    }


}
