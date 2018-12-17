package com.james.logbinding_core;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by James on 2018/12/17.
 */
public class LogBinding {

    public static void bind(Activity activity) {
        String name = activity.getClass().getName();
        try {
            Class<?> clazz = Class.forName(name + "_LogBinding");
            clazz.getConstructor(activity.getClass()).newInstance(activity);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
