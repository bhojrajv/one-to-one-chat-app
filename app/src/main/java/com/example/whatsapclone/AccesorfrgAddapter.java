package com.example.whatsapclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AccesorfrgAddapter extends FragmentPagerAdapter {
    public AccesorfrgAddapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
         switch (position){
             case 0:
                 Chatsfragment chatsfragment=new Chatsfragment();
                 return  chatsfragment;
             case 1:
                 Groupfrgament groupfrgament=new Groupfrgament();
                 return groupfrgament;
             case 2:
                 Contactsfrgment contactsfrgment=new Contactsfrgment();
                 return  contactsfrgment;
             case 3:
             Chatrequestfrg chatsfragment1=new Chatrequestfrg();
             return  chatsfragment1;
                 default:
                     return null;
         }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                 return  "Chats";
            case 1:
               return "Groups";
            case 2:
             return  "Contacts";
            case 3:
             return "ChatRequest";
            default:
                return null;
        }
    }
}
