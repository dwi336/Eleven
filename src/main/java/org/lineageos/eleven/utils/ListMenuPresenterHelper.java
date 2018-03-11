package org.lineageos.eleven.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.ListMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;

public class ListMenuPresenterHelper {

    /**
     * Calling internal methods via reflection
     */
    private static Class<?> cListMenuPresenter;
    private static Constructor<?> mConstructor;
    private static Method mSetCallback;
    private static Method mGetAdapter;
    private static Method mOnCloseMenu;
    
    static {
        try {
            cListMenuPresenter = ListMenuPresenter.class;
            mConstructor = cListMenuPresenter.getConstructor(new Class[] {Context.class, int.class});
            mSetCallback = cListMenuPresenter.getMethod("setCallback", new Class[] {MenuPresenter.Callback.class});
            mGetAdapter = cListMenuPresenter.getMethod("getAdapter", new Class[0]);
            mOnCloseMenu = cListMenuPresenter.getMethod("onCloseMenu", new Class[] {MenuBuilder.class, boolean.class});
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static ListMenuPresenter createInstance(Context context, int itemLayoutRes) {
        ListMenuPresenter result = null;
        try {
            result = (ListMenuPresenter) mConstructor.newInstance(new Object[] { context, itemLayoutRes });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    public static void setCallback(ListMenuPresenter listMenuPresenter, MenuPresenter.Callback cb) {
        try {
            mSetCallback.invoke(listMenuPresenter, cb);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static ListAdapter getAdapter(ListMenuPresenter listMenuPresenter) {
        ListAdapter result = null;
        try {
            result = (ListAdapter)(mGetAdapter.invoke(listMenuPresenter));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    public static void onCloseMenu(ListMenuPresenter listMenuPresenter, MenuBuilder menu, boolean allMenusAreClosing) {
        try {
            mOnCloseMenu.invoke(listMenuPresenter, menu, allMenusAreClosing);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }    
    }
    
}
