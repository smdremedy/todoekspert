package com.soldiersofmobile.todoekspert;


import android.os.Parcel;
import android.os.Parcelable;


public class Todo implements Parcelable {


    private String task;
    private boolean done;

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(task);
        out.writeByte((byte) (done ? 1 : 0));
    }

    public static final Parcelable.Creator<Todo> CREATOR
            = new Parcelable.Creator<Todo>() {
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    public Todo() {

    }

    private Todo(Parcel in) {
        task = in.readString();
        done = in.readByte() == 1;
    }
}
