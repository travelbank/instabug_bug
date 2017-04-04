package io.cordova.hellocordova;

/**
 * Created by seljabali on 4/4/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;

import java.util.concurrent.ExecutorService;

public class CordovaContext extends ContextWrapper implements CordovaInterface {
    private CordovaInterface cordovaInterface;

    public CordovaContext(Context context, CordovaInterface cordovaInterface) {
        super(context);

        this.cordovaInterface = cordovaInterface;
    }

    @Override
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
        cordovaInterface.startActivityForResult(command, intent, requestCode);
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin plugin) {
        cordovaInterface.setActivityResultCallback(plugin);
    }

    @Override
    public Activity getActivity() {
        return cordovaInterface.getActivity();
    }

    @Override
    public Object onMessage(String id, Object data) {
        return cordovaInterface.onMessage(id, data);
    }

    @Override
    public ExecutorService getThreadPool() {
        return cordovaInterface.getThreadPool();
    }

    @Override
    public void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
        cordovaInterface.requestPermission(plugin, requestCode, permission);
    }

    @Override
    public void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
        cordovaInterface.requestPermissions(plugin, requestCode, permissions);
    }

    @Override
    public boolean hasPermission(String permission) {
        return cordovaInterface.hasPermission(permission);
    }
}
