package com.example.facelook.Network.Package.Receive;


import com.example.facelook.data.History;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResHistory extends BaseResponse {
    List<History> histories;

    public ResHistory(ResponsePackage resPackage) {
        super(resPackage);
    }

    public List<History> getHistories() {
        return histories;
    }

    @Override
    public void unpackData(){
        ByteBuffer bf = makeBuffer();
        int length = bf.getInt();
        this.histories = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            long time = bf.getLong();
            String name = readString(bf);
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(time);
            History history = new History(date, name);
            this.histories.add(history);
        }
    }
}
