package org.lineageos.eleven.ui.fragments;

/*
 * Copyright (C) 2014 The Android Open Source Project
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

import java.lang.reflect.Method;
import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Interface by which a View builds its {@link Outline}, used for shadow casting and clipping.
 */
public abstract class ViewOutlineProviderCompat {
    /**
     * Default outline provider for Views, which queries the Outline from the View's background,
     * or generates a 0 alpha, rectangular Outline the size of the View if a background
     * isn't present.
     *
     * @see Drawable#getOutline(Outline)
     */
    public static final ViewOutlineProviderCompat BACKGROUND = new ViewOutlineProviderCompat() {
        @Override
        public void getOutline(View view, Outline outline) {
            Drawable background = view.getBackground();
            if (background != null) {        
            	try {
            		//background.getOutline(outline);
            	    Class<?> clazz = background.getClass();
            	    Method m = clazz.getMethod("getOutline", outline.getClass());
            	    m.invoke(background, outline);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
            	try {
            		//outline.setRect(0, 0, view.getWidth(), view.getHeight());
            	    Class<?> clazz = outline.getClass();
            	    Method m = clazz.getMethod("setRect", new Class[] { int.class, int.class, int.class, int.class});
            	    m.invoke(outline, 0, 0, view.getWidth(), view.getHeight());
            	    // outline.setAlpha(0.0f);
            	    m = clazz.getMethod("setAlpha", new Class[] {float.class});
            	    m.invoke(outline, 0.0f);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    };
    /**
     * Maintains the outline of the View to match its rectangular bounds,
     * at <code>1.0f</code> alpha.
     *
     * This can be used to enable Views that are opaque but lacking a background cast a shadow.
     */
    public static final ViewOutlineProviderCompat BOUNDS = new ViewOutlineProviderCompat() {
        @Override
        public void getOutline(View view, Outline outline) {
        	try {
        		//outline.setRect(0, 0, view.getWidth(), view.getHeight());
        	    Class<?> clazz = outline.getClass();
        	    Method m = clazz.getMethod("setRect", new Class[] { int.class, int.class, int.class, int.class});
        	    m.invoke(outline, 0, 0, view.getWidth(), view.getHeight());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
                
        }
    };
    /**
     * Maintains the outline of the View to match its rectangular padded bounds,
     * at <code>1.0f</code> alpha.
     *
     * This can be used to enable Views that are opaque but lacking a background cast a shadow.
     */
    public static final ViewOutlineProviderCompat PADDED_BOUNDS = new ViewOutlineProviderCompat() {
        @Override
        public void getOutline(View view, Outline outline) {
        	try {
        		//outline.setRect(view.getPaddingLeft(),
                //view.getPaddingTop(),
                //view.getWidth() - view.getPaddingRight(),
                //view.getHeight() - view.getPaddingBottom());
        	    Class<?> clazz = outline.getClass();
        	    Method m = clazz.getMethod("setRect", new Class[] { int.class, int.class, int.class, int.class});
        	    m.invoke(outline, view.getPaddingLeft(), 
        	    		view.getPaddingTop(),
        	    		view.getWidth() - view.getPaddingRight(), 
        	    		view.getHeight() - view.getPaddingBottom());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    };
    /**
     * Called to get the provider to populate the Outline.
     *
     * This method will be called by a View when its owned Drawables are invalidated, when the
     * View's size changes, or if {@link View#invalidateOutline()} is called
     * explicitly.
     *
     * The input outline is empty and has an alpha of <code>1.0f</code>.
     *
     * @param view The view building the outline.
     * @param outline The empty outline to be populated.
     */
    public abstract void getOutline(View view, Outline outline);
    
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static class ViewOutlineProviderL extends ViewOutlineProvider {

        private final ViewOutlineProviderCompat myProvider;

        public ViewOutlineProviderL(ViewOutlineProviderCompat provider) {
            this.myProvider = provider;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            myProvider.getOutline(view, outline);
        }
    }    
    
}