package org.lineageos.eleven.utils;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.EventLog;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;


/**
 * Implementation of the {@link android.view.ContextMenu} interface.
 * <p>
 * Most clients of the menu framework will never need to touch this
 * class.  However, if the client has a window that
 * is not a content view of a Dialog or Activity (for example, the
 * view was added directly to the window manager) and needs to show
 * context menus, it will use this class.
 * <p>
 * To use this class, instantiate it via {@link #ContextMenuBuilder(Context)},
 * and optionally populate it with any of your custom items.  Finally,
 * call {@link #show(View, IBinder)} which will populate the menu
 * with a view's context menu items and show the context menu.
 */
public class ContextMenuBuilder implements ContextMenu {

    MenuBuilder menuBuilder = null;

    public ContextMenuBuilder(Context context) {
    	menuBuilder = MenuBuilderHelper.createInstance(context);
    }

    public void setCallback(MenuBuilder.Callback cb) {
    	MenuBuilderHelper.setCallback(menuBuilder, cb);
    }
    
    public ContextMenu setHeaderIcon(Drawable icon) {
    	MenuBuilderHelper.setHeaderIconInt(menuBuilder, icon);
        return (ContextMenu) this;
    }

    public ContextMenu setHeaderIcon(int iconRes) {
    	MenuBuilderHelper.setHeaderIconInt(menuBuilder, iconRes);
        return (ContextMenu) this;
    }

    public ContextMenu setHeaderTitle(CharSequence title) {
    	MenuBuilderHelper.setHeaderTitleInt(menuBuilder, title);
        return (ContextMenu) this;
    }

    public ContextMenu setHeaderTitle(int titleRes) {
    	MenuBuilderHelper.setHeaderTitleInt(menuBuilder, titleRes);
        return (ContextMenu) this;
    }

    public ContextMenu setHeaderView(View view) {
    	MenuBuilderHelper.setHeaderViewInt(menuBuilder, view);
        return (ContextMenu) this;
    }
   
    public ArrayList<MenuItemImpl> getVisibleItems() {
        return MenuBuilderHelper.getVisibleItems(menuBuilder);
    }
    
    
    @Override
    public MenuItem add(CharSequence title) {
        return MenuBuilderHelper.add(menuBuilder, title);
    }

    @Override
    public MenuItem add(int titleRes) {
        return MenuBuilderHelper.add(menuBuilder, titleRes);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        return MenuBuilderHelper.add(menuBuilder, groupId, itemId, order, title);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return MenuBuilderHelper.add(menuBuilder, groupId, itemId, order, titleRes);
    }

    @Override
    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics,
            Intent intent, int flags, MenuItem[] outSpecificItems) {
        return MenuBuilderHelper.addIntentOptions(menuBuilder, groupId, itemId, order, caller, specifics, intent, flags, outSpecificItems);
    }

    @Override
    public SubMenu addSubMenu(CharSequence title) {
        return MenuBuilderHelper.addSubMenu(menuBuilder, title);
    }

    @Override
    public SubMenu addSubMenu(int titleRes) {
        return MenuBuilderHelper.addSubMenu(menuBuilder, titleRes);
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        return MenuBuilderHelper.addSubMenu(menuBuilder, groupId, itemId, order, title);
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        return MenuBuilderHelper.addSubMenu(menuBuilder, groupId, itemId, order, titleRes);
    }

    @Override
    public void clear() {
        MenuBuilderHelper.clear(menuBuilder);
    }

    @Override
    public void close() {
        MenuBuilderHelper.close(menuBuilder);
    }

    @Override
    public MenuItem findItem(int id) {
        return MenuBuilderHelper.findItem(menuBuilder, id);
    }

    @Override
    public MenuItem getItem(int index) {
        return MenuBuilderHelper.getItem(menuBuilder, index);
    }

    @Override
    public boolean hasVisibleItems() {
        return MenuBuilderHelper.hasVisibleItems(menuBuilder);
    }

    @Override
    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return MenuBuilderHelper.isShortcutKey(menuBuilder, keyCode, event);
    }

    @Override
    public boolean performIdentifierAction(int id, int flags) {
        return MenuBuilderHelper.performIdentifierAction(menuBuilder, id, flags);
    }

    @Override
    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        return MenuBuilderHelper.performShortcut(menuBuilder, keyCode, event, flags);
    }

    @Override
    public void removeGroup(int groupId) {
        MenuBuilderHelper.removeGroup(menuBuilder, groupId);
    }

    @Override
    public void removeItem(int id) {
        MenuBuilderHelper.removeItem(menuBuilder, id);
    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        MenuBuilderHelper.setGroupCheckable(menuBuilder, group, checkable, exclusive);
    }

    @Override
    public void setGroupEnabled(int group, boolean enabled) {
        MenuBuilderHelper.setGroupEnabled(menuBuilder, group, enabled);
    }

    @Override
    public void setGroupVisible(int group, boolean visible) {
        MenuBuilderHelper.setGroupVisible(menuBuilder, group, visible);
    }

    @Override
    public void setQwertyMode(boolean isQwerty) {
        MenuBuilderHelper.setQwertyMode(menuBuilder, isQwerty);
    }

    @Override
    public int size() {
        return MenuBuilderHelper.size(menuBuilder);
    }

    @Override
    public void clearHeader() {
        MenuBuilderHelper.clearHeader(menuBuilder);
    }

    /**
     * Shows this context menu, allowing the optional original view (and its
     * ancestors) to add items.
     * 
     * @param originalView Optional, the original view that triggered the
     *        context menu.
     * @param token Optional, the window token that should be set on the context
     *        menu's window.
     * @return If the context menu was shown, the {@link MenuDialogHelper} for
     *         dismissing it. Otherwise, null.
     */
    public MenuDialogHelper show(View originalView, IBinder token) {
        if (originalView != null) {
            // Let relevant views and their populate context listeners populate
            // the context menu
            originalView.createContextMenu(this);
        }

        if (getVisibleItems().size() > 0) {
            EventLog.writeEvent(50001, 1);
            
            MenuDialogHelper helper = new MenuDialogHelper(this.menuBuilder); 
            helper.show(token);
            
            return helper;
        }
        
        return null;
    }
    
}
