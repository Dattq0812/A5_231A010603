package vn.edu.vhu.ltdd.a5intent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Thay bằng MSSV của bạn
    private static final String TAG = "A5_231A010603";

    public static final String EXTRA_CONTACT = "extra_contact";
    public static final String EXTRA_NGUOI_GUI = "extra_nguoi_gui";
    public static final String EXTRA_DANH_SACH = "extra_danh_sach";

    private EditText edtHoTen, edtDienThoai, edtEmail;
    private TextView tvKetQuaTraVe;

    // Bộ nhận kết quả trả về từ DetailActivity
    private final ActivityResultLauncher<Intent> chiTietLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Đọc danh sách trả về từ Màn hình 2
                    java.util.ArrayList<Contact> danhSachTraVe = IntentCompat.getParcelableArrayListExtra(
                            result.getData(), EXTRA_DANH_SACH, Contact.class);

                    if (danhSachTraVe != null && !danhSachTraVe.isEmpty()) {
                        // Lấy phần tử đầu tiên ra để kiểm tra xem đã được Màn hình 2 và 3 cập nhật chưa
                        Contact daSua = danhSachTraVe.get(0);

                        edtHoTen.setText(daSua.getHoTen());

                        String textTraVe = "Nhận về danh sách có " + danhSachTraVe.size() + " liên hệ.\n";
                        textTraVe += "Cập nhật liên hệ 1: " + daSua.getHoTen();

                        if (daSua.getGhiChu() != null && !daSua.getGhiChu().trim().isEmpty()) {
                            textTraVe += "\nGhi chú từ MH3: " + daSua.getGhiChu();
                        }

                        tvKetQuaTraVe.setText(textTraVe);
                        Log.d(TAG, "Đã nhận danh sách trả về!");
                    }
                } else {
                    tvKetQuaTraVe.setText(R.string.returned_cancel);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        edtHoTen = findViewById(R.id.edtHoTen);
        edtDienThoai = findViewById(R.id.edtDienThoai);
        edtEmail = findViewById(R.id.edtEmail);
        tvKetQuaTraVe = findViewById(R.id.tvKetQuaTraVe);

        Button btnChiTiet = findViewById(R.id.btnChiTiet);
        Button btnGoi = findViewById(R.id.btnGoi);
        Button btnWeb = findViewById(R.id.btnWeb);
        Button btnChiaSe = findViewById(R.id.btnChiaSe);

        btnChiTiet.setOnClickListener(v -> moManHinhChiTiet());
        btnGoi.setOnClickListener(v -> goiDien());
        btnWeb.setOnClickListener(v -> moTrangWeb());
        btnChiaSe.setOnClickListener(v -> chiaSe());
    }

    private void moManHinhChiTiet() {
        String hoTen = edtHoTen.getText().toString().trim();
        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_empty));
            return;
        }

        // Tạo phần tử người dùng nhập
        Contact contactThuc = new Contact(
                hoTen,
                edtDienThoai.getText().toString().trim(),
                edtEmail.getText().toString().trim(),
                ""
        );

        // ===== CẬP NHẬT NC2: Tạo danh sách =====
        java.util.ArrayList<Contact> danhSach = new java.util.ArrayList<>();
        danhSach.add(contactThuc); // Phần tử số 0 (sẽ mang đi chỉnh sửa)
        danhSach.add(new Contact("Người Ảo Số 1", "0111", "ao1@vhu.edu.vn", "")); // Data giả
        danhSach.add(new Contact("Người Ảo Số 2", "0222", "ao2@vhu.edu.vn", "")); // Data giả

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_DANH_SACH, danhSach);
        intent.putExtra(EXTRA_NGUOI_GUI, TAG);

        chiTietLauncher.launch(intent);
    }

    private void goiDien() {
        String sdt = edtDienThoai.getText().toString().trim();
        if (sdt.isEmpty()) {
            edtDienThoai.setError(getString(R.string.err_empty));
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + sdt));
        moAnToan(intent);
    }

    private void moTrangWeb() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.school_url)));
        moAnToan(intent);
    }

    private void chiaSe() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text,
                edtHoTen.getText().toString(), edtDienThoai.getText().toString()));
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }

    private void moAnToan(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.err_no_app, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Không có ứng dụng nào xử lý: " + intent.getAction(), e);
        }
    }
}