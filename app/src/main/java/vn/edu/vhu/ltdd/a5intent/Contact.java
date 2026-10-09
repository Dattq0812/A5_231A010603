package vn.edu.vhu.ltdd.a5intent;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Đối tượng được đóng gói để truyền qua Intent.
 * Parcelable nhanh hơn Serializable vì Android tự sinh mã đọc/ghi thay cho cơ chế phản chiếu.
 * Mẹo: gõ "implements Parcelable" rồi nhấn Alt+Enter, Android Studio sinh sẵn phần khung.
 */
public class Contact implements Parcelable {

    private String hoTen;
    private String dienThoai;
    private String email;
    // Thêm thuộc tính NC1
    private String ghiChu;

    public Contact(String hoTen, String dienThoai, String email, String ghiChu) {
        this.hoTen = hoTen;
        this.dienThoai = dienThoai;
        this.email = email;
        this.ghiChu = ghiChu;
    }

    /** Đọc lại dữ liệu ĐÚNG THỨ TỰ đã ghi trong writeToParcel. */
    protected Contact(Parcel in) {
        hoTen = in.readString();
        dienThoai = in.readString();
        email = in.readString();
        ghiChu = in.readString();; //NC1
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hoTen);
        dest.writeString(dienThoai);
        dest.writeString(email);
        dest.writeString(ghiChu);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getHoTen() { return hoTen; }
    public String getDienThoai() { return dienThoai; }
    public String getEmail() { return email; }
    public  String getGhiChu(){return ghiChu; }


    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public void setGhiChu(String ghiChu){this.ghiChu = ghiChu;}
}

