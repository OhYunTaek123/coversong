package com.example.coversong.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

/**
 * 게시판 내용
 *
 * @ TODO: 2021-07-29  의료기관명, 병실, 환자명, 혈액형, 필요한 혈액 종류, 환자번호 ...
 * @see Parcelable <br>
 * [Parcelable] Intent로 보내기 위한 인터페이스
 */
public class BoardItem implements Parcelable {
    //내용
    public String patient;
    public String patientNum;
    public String bloodType;
    public String hospital;
    public String room;

    //작성정보
    public String writer;
    public String email;
    public String key;

    private static final String EMPTY = "";

    public BoardItem() {
    }

    public BoardItem(FirebaseUser user) {
        this(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, user);
    }

    public BoardItem(String patient, String patientNum, String blood_type, String hospital, String room, FirebaseUser user) {
        this.patient = patient;
        this.patientNum = patientNum;
        this.bloodType = blood_type;
        this.hospital = hospital;
        this.room = room;
        this.writer = user.getDisplayName();
        this.email = user.getEmail();
    }

    protected BoardItem(Parcel in) {
        patient = in.readString();
        patientNum = in.readString();
        bloodType = in.readString();
        hospital = in.readString();
        room = in.readString();
        writer = in.readString();
        email = in.readString();
        key = in.readString();
    }

    public boolean isWriter(FirebaseUser user) {
        return (!user.isAnonymous() && email.equals(user.getEmail()));
    }

    public static final Creator<BoardItem> CREATOR = new Creator<BoardItem>() {
        @Override
        public BoardItem createFromParcel(Parcel in) {
            return new BoardItem(in);
        }

        @Override
        public BoardItem[] newArray(int size) {
            return new BoardItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(patient);
        dest.writeString(patientNum);
        dest.writeString(bloodType);
        dest.writeString(hospital);
        dest.writeString(room);
        dest.writeString(writer);
        dest.writeString(email);
        dest.writeString(key);
    }
}
