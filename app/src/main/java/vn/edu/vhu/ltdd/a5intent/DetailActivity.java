package vn.edu.vhu.ltdd.a5intent;

import android.content.Intent;
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

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "A5_231A010603";

    private Contact contact;
    private EditText edtHoTenMoi;
    private TextView tvThongTin;
    private TextView tvDanhSach;
    private java.util.ArrayList<Contact> danhSachContact;

    // Bộ nhận kết quả trả về từ Màn hình 3 (NoteActivity)
    private final ActivityResultLauncher<Intent> noteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String ghiChuTraVe = result.getData().getStringExtra(NoteActivity.EXTRA_GHI_CHU);

                    if (contact != null) {
                        // Gán ghi chú vào đối tượng contact hiện tại
                        contact.setGhiChu(ghiChuTraVe);
                        Toast.makeText(this, "Đã thêm ghi chú: " + ghiChuTraVe, Toast.LENGTH_SHORT).show();

                        // Cập nhật lại TextView để hiển thị ngay ghi chú vừa thêm
                        capNhatGiaoDienThongTin();
                        //Nc2
                        hienThiDanhSachMang();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        tvThongTin = findViewById(R.id.tvThongTin);
        TextView tvNguoiGui = findViewById(R.id.tvNguoiGui);
        edtHoTenMoi = findViewById(R.id.edtHoTenMoi);
        tvDanhSach = findViewById(R.id.tvDanhSach);
        Button btnLuu = findViewById(R.id.btnLuu);
        Button btnHuy = findViewById(R.id.btnHuy);
        Button btnThemGhiChu = findViewById(R.id.btnThemGhiChu);

        // Lấy dữ liệu do màn hình 1 gửi sang
        // ===== CẬP NHẬT NC2: Nhận về danh sách =====
        danhSachContact = IntentCompat.getParcelableArrayListExtra(
                getIntent(), MainActivity.EXTRA_DANH_SACH, Contact.class);

        String nguoiGui = getIntent().getStringExtra(MainActivity.EXTRA_NGUOI_GUI);

        if (danhSachContact == null || danhSachContact.isEmpty()) {
            tvThongTin.setText(R.string.no_data);
            return;
        }

        // Lấy phần tử đầu tiên để làm việc
        contact = danhSachContact.get(0);
        //NC2 gọi danh sách liên hệ
        hienThiDanhSachMang();
        capNhatGiaoDienThongTin();
        tvNguoiGui.setText(getString(R.string.sent_by, nguoiGui));
        edtHoTenMoi.setText(contact.getHoTen());

        // Mở màn hình 3 để ghi chú
        btnThemGhiChu.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteActivity.class);
            noteLauncher.launch(intent);
        });

        btnLuu.setOnClickListener(v -> luuVaQuayLai());

        btnHuy.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    // Tách riêng hàm này để tái sử dụng: lúc mới vào màn hình và lúc nhận được ghi chú
    private void capNhatGiaoDienThongTin() {
        String thongTin = getString(R.string.detail_format,
                contact.getHoTen(),
                contact.getDienThoai(),
                contact.getEmail());

        // Nếu đã có ghi chú thì nối thêm vào chuỗi để hiển thị
        if (contact.getGhiChu() != null && !contact.getGhiChu().trim().isEmpty()) {
            thongTin += "\nGhi chú: " + contact.getGhiChu();
        }
        tvThongTin.setText(thongTin);
    }

    private void luuVaQuayLai() {
        String hoTenMoi = edtHoTenMoi.getText().toString().trim();
        if (hoTenMoi.isEmpty()) {
            edtHoTenMoi.setError(getString(R.string.err_empty));
            return;
        }

        // Cập nhật họ tên mới (ghi chú đã được set bởi Màn hình 3 trước đó rồi)
        contact.setHoTen(hoTenMoi);
        // -> Lúc này danhSachContact.get(0) tự động mang dữ liệu mới

        // ===== CẬP NHẬT NC2: Trả về danh sách =====
        Intent ketQua = new Intent();
        ketQua.putParcelableArrayListExtra(MainActivity.EXTRA_DANH_SACH, danhSachContact);

        setResult(RESULT_OK, ketQua);
        finish();
    }
    // Hàm duyệt qua mảng và in ra giao diện
    private void hienThiDanhSachMang() {
        if (danhSachContact == null || danhSachContact.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < danhSachContact.size(); i++) {
            Contact c = danhSachContact.get(i);
            sb.append(i + 1).append(". ")
                    .append(c.getHoTen())
                    .append(" (").append(c.getDienThoai()).append(")");

            // Nếu phần tử có ghi chú thì in thêm ra
            if (c.getGhiChu() != null && !c.getGhiChu().trim().isEmpty()) {
                sb.append(" -> Ghi chú: ").append(c.getGhiChu());
            }
            sb.append("\n"); // Xuống dòng cho liên hệ tiếp theo
        }

        tvDanhSach.setText(sb.toString().trim());
    }
}