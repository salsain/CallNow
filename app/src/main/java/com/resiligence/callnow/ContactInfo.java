package com.resiligence.callnow;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Айрат on 04.06.2017.
 */
public class ContactInfo implements Comparable<ContactInfo>  {
    String name;
    ArrayList<String> phones;
    public boolean cheched;

    public ContactInfo(String name, ArrayList<String> phones) {
        this.name = name;
        this.phones = phones;
    }

    @Override
    public int compareTo(@NonNull ContactInfo f)
    {
        return this.name.compareTo(f.name);
    }
}
