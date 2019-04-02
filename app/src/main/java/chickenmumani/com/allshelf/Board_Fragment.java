package chickenmumani.com.allshelf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Board_Fragment extends Fragment {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;


    public Board_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_board_main, container, false);

        mViewPager = (ViewPager)view.findViewById(R.id.board_pager);
        mViewPager.setAdapter( new PagerAdapter(getActivity().getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);

        mTabLayout = (TabLayout)view.findViewById(R.id.board_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("교환");
        mTabLayout.getTabAt(1).setText("토론");
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter{
        public PagerAdapter(FragmentManager fm) { super(fm); }
        /** * View Pager의 Fragment 들은 각각 Index를 가진다. * Android OS로 부터 요청된 Pager의 Index를 보내주면, * 해당되는 Fragment를 리턴시킨다. * @param position * @return */
        @Override public Fragment getItem(int position) {
            if( position == 0 ) {
                return new Board_Fragment_Trade(); }
            else {
                return new Board_Fragment_Toron(); }
        } /** * View Pager에 몇개의 Fragment가 들어가는지 설정 * @return */
        @Override public int getCount() {
            return 2;
        }
    }

    public void onResume(){
        super.onResume();
        ((Navi_Activity) getActivity()).setActionBarTitle("게시판");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.board_menu, menu) ;
        final MenuItem item1 = menu.findItem(R.id.action_board_add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.action_board_add:
                Intent intent = new Intent(getContext(), Board_WriteActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}

class TimingN2 implements Comparator<Board_Item> {
    @Override
    public int compare(Board_Item o1, Board_Item o2) {
        return o2.getDbid().compareTo(o1.getDbid());
    }
}

class TimingN2C implements Comparator<Comment_Item> {
    @Override
    public int compare(Comment_Item o1, Comment_Item o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}