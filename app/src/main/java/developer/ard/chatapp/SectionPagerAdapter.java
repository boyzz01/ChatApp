package developer.ard.chatapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;


import developer.ard.chatapp.fragment.ChatFragment;
import developer.ard.chatapp.fragment.UserFragment;
import developer.ard.chatapp.fragment.GrupFragment;

/**
 * Created by MSI-PC on 10/8/2017.
 */

class SectionPagerAdapter extends FragmentPagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    private FragmentManager mFragmentManager;

    public SectionPagerAdapter(FragmentManager fm) {

        super(fm);
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {

//
            case 0:
               ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                GrupFragment grupFragment  = new GrupFragment();
                return grupFragment;
            case 2:
                UserFragment friendFragment = new UserFragment();
                return friendFragment;


            default:
                return null;
        }



    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
           case 0:
               return "CHAT";
            case 1:
                return "GRUP";
            case 2:
                return "KONTAK";

            default:
                return null;
        }

    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);

    }
}
