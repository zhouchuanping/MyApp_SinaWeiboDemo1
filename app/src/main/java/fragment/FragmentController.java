package fragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import base.BaseFragment;

/**
 * Created by ZCP_ing on 2016/5/10.
 */
public class FragmentController {


    private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragments;

    private static FragmentController controller;

    private FragmentController(FragmentActivity activity, int containerId) {
        this.containerId = containerId;
        fm = activity.getSupportFragmentManager();
        initFragment();
    }

    private void initFragment() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment());
        fragments.add(new MessageFragment());
        fragments.add(new SearchFragment());
        fragments.add(new UserFragment());

        FragmentTransaction ft = fm.beginTransaction();

        for (Fragment fragment : fragments) {
            ft.add(containerId, fragment);
        }
        ft.commit();

    }

    public static void onDestroy() {
        controller = null;
    }

    /**
     * 单例模式、获取单例类对象、防止数据的冲突、节省内存空间
     * 单例三步骤：
     *      1、私有化构造方法，防止在其他地方new本例对象
     *      2、创建一个本例对象的静态应用：private static FragmentController controller;
     *      3、提供一个获取对象的公有静态方法：
     *              进行判断：如果为空，则new一个对象，否则就不new对象，最后return回去
     *      保证外部只能通过getInstance来获取本例对象，且永远都是同一个对象。
     */
    public static FragmentController getInstance(FragmentActivity activity, int containerId) {
        if (controller == null) {
            controller = new FragmentController(activity, containerId);
        }
        return controller;
    }

    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = fragments.get(position);
        FragmentTransaction ft = fm.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    private void hideFragments() {

        FragmentTransaction ft = fm.beginTransaction();

        for (Fragment fragment : fragments) {
            if (fragment != null) {
                ft.hide(fragment);
            }
        }
        ft.commit();
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }


}
