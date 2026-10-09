package vn.edu.vhu.ltdd.a5intent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NoteActivity extends AppCompatActivity {

    // Khóa dữ liệu để đóng gói kết quả
    public static final String EXTRA_GHI_CHU = "extra_ghi_chu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);

        // Xử lý viền màn hình (Edge to edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        EditText edtGhiChu = findViewById(R.id.edtGhiChu);
        Button btnLuuGhiChu = findViewById(R.id.btnLuuGhiChu);

        btnLuuGhiChu.setOnClickListener(v -> {
            // Đóng gói ghi chú và gửi kết quả về
            Intent ketQua = new Intent();
            ketQua.putExtra(EXTRA_GHI_CHU, edtGhiChu.getText().toString().trim());
            setResult(RESULT_OK, ketQua);

            // Đóng màn hình hiện tại
            finish();
        });
    }
}