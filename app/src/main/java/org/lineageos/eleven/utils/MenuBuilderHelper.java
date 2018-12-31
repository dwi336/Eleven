package org.lineageos.eleven.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuPresenter;

public class MenuBuilderHelper {

    /**
     * Calling internal methods via reflection
     */
    private static Class<?> cMenuBuilder;
    private static Constructor<?> mConstructor;
    private static Method mSetCallback;
    private static Method mAddMenuPresenter;
    private static Method mSetHeaderIconIntDrawable;
    private static Method mSetHeaderIconIntInt;
    private static Method mSetHeaderTitleIntCharSequence;
    private static Method mSetHeaderTitleIntInt;
    private static Method mSetHeaderViewInt;
    private static Method mGetHeaderIcon;
    private static Method mGetHeaderTitle;
    private static Method mGetHeaderView;
    private static Method mGetVisibleItems;
    private static Method mGetContext;
    
    private static Method mAddCharSequence;
    private static Method mAddInt;
    private static Method mAddIntIntIntCharSequence;
    private static Method mAddIntIntIntInt;
    private static Method mAddIntentOptions;
    private static Method mAddSubMenuCharSequence;
    private static Method mAddSubMenuInt;
    private static Method mAddSubMenuIntIntIntCharSequence;
    private static Method mAddSubMenuIntIntIntInt;
    private static Method mClear;
    private static Method mClose;
    private static Method mCloseBoolean;
    private static Method mFindItem;
    private static Method mGetItem;
    private static Method mHasVisibleItems;
    private static Method mIsShortcutKey;
    private static Method mPerformIdentifierAction;
    private static Method mPerformItemAction;
    private static Method mPerformShortcut;
    private static Method mRemoveGroup;
    private static Method mRemoveItem;
    private static Method mSetGroupCheckable;
    private static Method mSetGroupEnabled;
    private static Method mSetGroupVisible;
    private static Method mSetQwertyMode;
    private static Method mSize;
    private static Method mClearHeader;

    static {
        try {
            cMenuBuilder = MenuBuilder.class;
            mConstructor = cMenuBuilder.getConstructor(new Class[] {Context.class});
            mSetCallback = cMenuBuilder.getMethod("setCallback", new Class[] {MenuBuilder.Callback.class});
            mAddMenuPresenter = cMenuBuilder.getMethod("addMenuPresenter", new Class[] {MenuPresenter.class});
            mSetHeaderIconIntDrawable = cMenuBuilder.getDeclaredMethod("setHeaderIconInt", new Class[] {Drawable.class});
            mSetHeaderIconIntDrawable.setAccessible(true);
            mSetHeaderIconIntInt = cMenuBuilder.getDeclaredMethod("setHeaderIconInt", new Class[] {int.class});
            mSetHeaderIconIntInt.setAccessible(true);
            mSetHeaderTitleIntCharSequence = cMenuBuilder.getDeclaredMethod("setHeaderTitleInt", new Class[] {CharSequence.class});
            mSetHeaderTitleIntCharSequence.setAccessible(true);
            mSetHeaderTitleIntInt = cMenuBuilder.getDeclaredMethod("setHeaderTitleInt", new Class[] {int.class});
            mSetHeaderTitleIntInt.setAccessible(true);
            mSetHeaderViewInt = cMenuBuilder.getDeclaredMethod("setHeaderViewInt", new Class[] {View.class});
            mSetHeaderViewInt.setAccessible(true);
            mGetHeaderIcon = cMenuBuilder.getMethod("getHeaderIcon", new Class[0]);
            mGetHeaderTitle = cMenuBuilder.getMethod("getHeaderTitle", new Class[0]);
            mGetHeaderView = cMenuBuilder.getMethod("getHeaderView", new Class[0]);
            mGetVisibleItems = cMenuBuilder.getMethod("getVisibleItems", new Class[0]);
            mGetContext = cMenuBuilder.getMethod("getContext", new Class[0]);
            mAddCharSequence = cMenuBuilder.getMethod("add", new Class[] {CharSequence.class});
            mAddInt = cMenuBuilder.getMethod("add", new Class[] {int.class});
            mAddIntIntIntCharSequence = cMenuBuilder.getMethod("add", new Class[] {int.class, int.class, int.class, CharSequence.class});
            mAddIntIntIntInt = cMenuBuilder.getMethod("add", new Class[] {int.class, int.class, int.class, int.class});
            mAddIntentOptions = cMenuBuilder.getMethod("addIntentOptions", new Class[] {int.class, int.class, int.class, ComponentName.class, Intent[].class, Intent.class, int.class, MenuItem[].class});
            mAddSubMenuCharSequence = cMenuBuilder.getMethod("addSubMenu", new Class[] {CharSequence.class});
            mAddSubMenuInt = cMenuBuilder.getMethod("addSubMenu", new Class[] {int.class});
            mAddSubMenuIntIntIntCharSequence = cMenuBuilder.getMethod("addSubMenu", new Class[] {int.class, int.class, int.class, CharSequence.class});
            mAddSubMenuIntIntIntInt = cMenuBuilder.getMethod("addSubMenu", new Class[] {int.class, int.class, int.class, int.class});
            mClear = cMenuBuilder.getMethod("clear", new Class[0]);
            mClose = cMenuBuilder.getMethod("close", new Class[0]);
            mCloseBoolean = cMenuBuilder.getMethod("close", new Class[] {boolean.class});
            mFindItem = cMenuBuilder.getMethod("findItem", new Class[] {int.class});
            mGetItem = cMenuBuilder.getMethod("getItem", new Class[] {int.class});
            mHasVisibleItems = cMenuBuilder.getMethod("hasVisibleItems", new Class[0]);
            mIsShortcutKey = cMenuBuilder.getMethod("isShortcutKey", new Class[] {int.class, KeyEvent.class});
            mPerformIdentifierAction = cMenuBuilder.getMethod("performIdentifierAction", new Class[] {int.class, int.class});
            mPerformItemAction = cMenuBuilder.getMethod("performItemAction", new Class[] {MenuItem.class, int.class});
            mPerformShortcut = cMenuBuilder.getMethod("performShortcut", new Class[] {int.class, KeyEvent.class, int.class});
            mRemoveGroup = cMenuBuilder.getMethod("removeGroup", new Class[] {int.class});
            mRemoveItem = cMenuBuilder.getMethod("removeItem", new Class[] {int.class});
            mSetGroupCheckable = cMenuBuilder.getMethod("setGroupCheckable", new Class[] {int.class, boolean.class, boolean.class});
            mSetGroupEnabled = cMenuBuilder.getMethod("setGroupEnabled", new Class[] {int.class, boolean.class});
            mSetGroupVisible = cMenuBuilder.getMethod("setGroupVisible", new Class[] {int.class, boolean.class});
            mSetQwertyMode = cMenuBuilder.getMethod("setQwertyMode", new Class[] {boolean.class});
            mSize = cMenuBuilder.getMethod("size", new Class[0]);
            mClearHeader = cMenuBuilder.getMethod("clearHeader", new Class[0]);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static MenuBuilder createInstance(Context context) {
        MenuBuilder result = null;
        try {
            result = (MenuBuilder) mConstructor.newInstance(new Object[] { context });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    public static void setCallback(MenuBuilder menuBuilder, MenuBuilder.Callback cb) {
        try {
            mSetCallback.invoke(menuBuilder, cb);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void addMenuPresenter(MenuBuilder menuBuilder, MenuPresenter presenter) {
        try {
            mAddMenuPresenter.invoke(menuBuilder, presenter);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static MenuBuilder setHeaderIconInt(MenuBuilder menuBuilder, Drawable icon) {
        MenuBuilder result = null;
        try {
            result = (MenuBuilder)(mSetHeaderIconIntDrawable.invoke(menuBuilder, icon));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    public static MenuBuilder setHeaderIconInt(MenuBuilder menuBuilder, int iconRes) {
        MenuBuilder result = null;
        try {
            result = (MenuBuilder)(mSetHeaderIconIntInt.invoke(menuBuilder, iconRes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    public static MenuBuilder setHeaderTitleInt(MenuBuilder menuBuilder, CharSequence title) {
        MenuBuilder result = null;
        try {
            result = (MenuBuilder)(mSetHeaderTitleIntCharSequence.invoke(menuBuilder, title));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    public static MenuBuilder setHeaderTitleInt(MenuBuilder menuBuilder, int titleRes) {
        MenuBuilder result = null;
        try {
            result = (MenuBuilder)(mSetHeaderTitleIntInt.invoke(menuBuilder, titleRes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    public static MenuBuilder setHeaderViewInt(MenuBuilder menuBuilder, View view) {
        MenuBuilder result = null;
        try {
            result = (MenuBuilder)(mSetHeaderViewInt.invoke(menuBuilder, view));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static Drawable getHeaderIcon(MenuBuilder menuBuilder) {
        Drawable icon = null;
        try {
            icon = (Drawable)(mGetHeaderIcon.invoke(menuBuilder));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return icon;
    }
    
    public static CharSequence getHeaderTitle(MenuBuilder menuBuilder) {
        CharSequence title = null;
        try {
            title = (CharSequence)(mGetHeaderTitle.invoke(menuBuilder));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return title;
    }
    
    public static View getHeaderView(MenuBuilder menuBuilder) {
        View view = null;
        try {
            view = (View)(mGetHeaderView.invoke(menuBuilder));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return view;
    }
    
    public static ArrayList<MenuItemImpl> getVisibleItems(MenuBuilder menuBuilder) {
        ArrayList<MenuItemImpl> mVisibleItems = null;
        try {
            Object obj = (mGetVisibleItems.invoke(menuBuilder));
            if (obj instanceof ArrayList<?>) {
                ArrayList<?> al = (ArrayList<?>) obj;
                mVisibleItems = new ArrayList<MenuItemImpl>();
                if (al.size() > 0) {
                    for (int i = 0; i < al.size(); i++) {
                        Object o = al.get(i);
                        if (o instanceof MenuItemImpl) {
                            mVisibleItems.add((MenuItemImpl) o);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return mVisibleItems;
    }
    
    public static Context getContext(MenuBuilder menuBuilder) {
        Context context = null;
        try {
            context = (Context)(mGetContext.invoke(menuBuilder));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return context;
    }
    
    public static MenuItem add(MenuBuilder menuBuilder, CharSequence title) {
        MenuItem menuItem = null;
        try {
            menuItem = (MenuItem)(mAddCharSequence.invoke(menuBuilder, title));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return menuItem;
    }

    public static MenuItem add(MenuBuilder menuBuilder, int titleRes) {
        MenuItem menuItem = null;
        try {
            menuItem = (MenuItem)(mAddInt.invoke(menuBuilder, titleRes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return menuItem;
    }

    public static MenuItem add(MenuBuilder menuBuilder, int groupId, int itemId, int order, CharSequence title) {
        MenuItem menuItem = null;
        try {
            menuItem = (MenuItem)(mAddIntIntIntCharSequence.invoke(menuBuilder, groupId, itemId, order, title));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return menuItem;
    }

    public static MenuItem add(MenuBuilder menuBuilder, int groupId, int itemId, int order, int titleRes) {
        MenuItem menuItem = null;
        try {
            menuItem = (MenuItem)(mAddIntIntIntInt.invoke(menuBuilder, groupId, itemId, order,titleRes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return menuItem;
    }

    public static int addIntentOptions(MenuBuilder menuBuilder, int groupId, int itemId, int order, ComponentName caller, Intent[] specifics,
            Intent intent, int flags, MenuItem[] outSpecificItems) {
        int result = 0;
        try {
            result = ((Integer)(mAddIntentOptions.invoke(menuBuilder, groupId, itemId, order, caller, specifics, intent, flags, outSpecificItems))).intValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static SubMenu addSubMenu(MenuBuilder menuBuilder, CharSequence title) {
        SubMenu subMenu = null;
        try {
            subMenu = (SubMenu)(mAddSubMenuCharSequence.invoke(menuBuilder, title));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return subMenu;
    }

    public static SubMenu addSubMenu(MenuBuilder menuBuilder, int titleRes) {
        SubMenu subMenu = null;
        try {
            subMenu = (SubMenu)(mAddSubMenuInt.invoke(menuBuilder, titleRes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return subMenu;
    }

    public static SubMenu addSubMenu(MenuBuilder menuBuilder, int groupId, int itemId, int order, CharSequence title) {
        SubMenu subMenu = null;
        try {
            subMenu = (SubMenu)(mAddSubMenuIntIntIntCharSequence.invoke(menuBuilder, groupId, itemId, order, title));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return subMenu;
    }

    public static SubMenu addSubMenu(MenuBuilder menuBuilder, int groupId, int itemId, int order, int titleRes) {
        SubMenu subMenu = null;
        try {
            subMenu = (SubMenu)(mAddSubMenuIntIntIntInt.invoke(menuBuilder, groupId, itemId, order, titleRes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return subMenu;
    }

    public static void clear(MenuBuilder menuBuilder) {
        try {
            mClear.invoke(menuBuilder);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void close(MenuBuilder menuBuilder) {
        try {
            mClose.invoke(menuBuilder);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void close(MenuBuilder menuBuilder, boolean closeAllMenus) {
        try {
            mCloseBoolean.invoke(menuBuilder, closeAllMenus);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static MenuItem findItem(MenuBuilder menuBuilder, int id) {
        MenuItem menuItem = null;
        try {
            menuItem = (MenuItem)(mFindItem.invoke(menuBuilder, id));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return menuItem;
    }

    public static MenuItem getItem(MenuBuilder menuBuilder, int index) {
        MenuItem menuItem = null;
        try {
            menuItem = (MenuItem)(mGetItem.invoke(menuBuilder, index));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return menuItem;
    }

    public static boolean hasVisibleItems(MenuBuilder menuBuilder) {
        boolean result = false;
        try {
            result = ((Boolean)(mHasVisibleItems.invoke(menuBuilder))).booleanValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static boolean isShortcutKey(MenuBuilder menuBuilder, int keyCode, KeyEvent event) {
        boolean result = false;
        try {
            result = ((Boolean)(mIsShortcutKey.invoke(menuBuilder, keyCode, event))).booleanValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static boolean performIdentifierAction(MenuBuilder menuBuilder, int id, int flags) {
        boolean result = false;
        try {
            result = ((Boolean)(mPerformIdentifierAction.invoke(menuBuilder, id, flags))).booleanValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static boolean performItemAction(MenuBuilder menuBuilder, MenuItem item, int flags) {
        boolean result = false;
        try {
            result = ((Boolean)(mPerformItemAction.invoke(menuBuilder, item, flags))).booleanValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    public static boolean performShortcut(MenuBuilder menuBuilder, int keyCode, KeyEvent event, int flags) {
        boolean result = false;
        try {
            result = ((Boolean)(mPerformShortcut.invoke(menuBuilder, keyCode, event, flags))).booleanValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static void removeGroup(MenuBuilder menuBuilder, int groupId) {
        try {
            mRemoveGroup.invoke(menuBuilder, groupId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void removeItem(MenuBuilder menuBuilder, int id) {
        try {
            mRemoveItem.invoke(menuBuilder, id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setGroupCheckable(MenuBuilder menuBuilder, int group, boolean checkable, boolean exclusive) {
        try {
            mSetGroupCheckable.invoke(menuBuilder, group, checkable, exclusive);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setGroupEnabled(MenuBuilder menuBuilder, int group, boolean enabled) {
        try {
            mSetGroupEnabled.invoke(menuBuilder, group, enabled);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setGroupVisible(MenuBuilder menuBuilder, int group, boolean visible) {
        try {
            mSetGroupVisible.invoke(menuBuilder, group, visible);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setQwertyMode(MenuBuilder menuBuilder, boolean isQwerty) {
        try {
            mSetQwertyMode.invoke(menuBuilder, isQwerty);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int size(MenuBuilder menuBuilder) {
        int result = 0;
        try {
            result = ((Integer)(mSize.invoke(menuBuilder))).intValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static void clearHeader(MenuBuilder menuBuilder) {
        try {
            mClearHeader.invoke(menuBuilder);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
