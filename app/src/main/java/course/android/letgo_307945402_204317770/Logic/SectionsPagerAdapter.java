package course.android.letgo_307945402_204317770.Logic;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import course.android.letgo_307945402_204317770.GUI.Frag_UserItems;
import course.android.letgo_307945402_204317770.R;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Frag_UserItems sellingFragment, soldFragment, faveFragment;
    private final String[] titles;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.fragmentNames);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a Fragment for each page view.
        switch(position) {
            case 0:
                sellingFragment = Frag_UserItems.newInstance(position + 1);
                return sellingFragment;
            case 1:
                soldFragment = Frag_UserItems.newInstance(position + 1);
                return soldFragment;
            case 2:
                faveFragment = Frag_UserItems.newInstance(position + 1);
                return faveFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public Frag_UserItems getSellingFragment() {
        return sellingFragment;
    }

    public Frag_UserItems getSoldFragment() {
        return soldFragment;
    }

    public Frag_UserItems getFaveFragment() {
        return faveFragment;
    }
}
