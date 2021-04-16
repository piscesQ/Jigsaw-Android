package com.kore.sample.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author koreq
 * @date 2021-04-19
 * @description
 */
public class Person implements Parcelable {
    private static final String TAG = Person.class.getSimpleName();

    private String userName;
    private String passWord;

    public Person() {
        Log.d(TAG, "Person(): ");
    }

    public Person(String userName, String passWord) {
        Log.d(TAG, "Person() called with: " + "userName = [" + userName + "], passWord = [" + passWord + "]");
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getUserName() {
        Log.d(TAG, "getUserName: = " + userName);
        return userName;
    }

    public void setUserName(String userName) {
        Log.d(TAG, "setUserName: = " + userName);
        Log.d(TAG, "setUserName() called with: " + "userName = [" + userName + "]");
        this.userName = userName;
    }

    public String getPassWord() {
        Log.d(TAG, "getPassWord: =  " + passWord);
        return passWord;
    }

    public void setPassWord(String passWord) {
        Log.d(TAG, "setPassWord: = passWord");
        this.passWord = passWord;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        Log.d(TAG, "describeContents: ");
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        Log.d(TAG, "writeToParcel: = " + flags);
        dest.writeString(userName);
        dest.writeString(passWord);

    }

    public static final Parcelable.Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            Log.d(TAG, "createFromParcel: ");
            Person person = new Person();
            person.userName = source.readString();
            person.passWord = source.readString();
            Log.d(TAG, "createFromParcel: " + "person.userName = " + person.userName
                    + "\nperson.passWord" + person.passWord);
            Log.d(TAG, person.toString());
            return person;
        }

        @Override
        public Person[] newArray(int size) {
            Log.d(TAG, "newArray: size = " + size);
            return new Person[size];
        }
    };

    @Override
    public String toString() {
        return "Person{" +
                "userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}
