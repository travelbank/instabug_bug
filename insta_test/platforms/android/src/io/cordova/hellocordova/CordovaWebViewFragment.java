package io.cordova.hellocordova;

/**
 * Created by seljabali on 4/4/17.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.PluginEntry;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CordovaWebViewFragment extends Fragment implements CordovaInterface {
    private static final String URL_KEY = "URL-KEY";

    private CordovaInterfaceImpl cordovaInterface;
    private CordovaWebView cordovaWebView;

    private boolean keepRunning = true;
    // This flag is used for cordova multitasking? Why does it need to be true from the
    // start. It only ever gets set to false later on...

    public static CordovaWebViewFragment newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(URL_KEY, url);
        CordovaWebViewFragment fragment = new CordovaWebViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // The CordovaContext needs to delegate calls made on the activity (by the web view) to
        // this fragment.
        // For this we need to clone the inflater context and pass our CordovaContext basically
        // bridging the gap...
        LayoutInflater li = inflater.cloneInContext(new CordovaContext(getActivity(), this));
        FrameLayout view = (FrameLayout) li.inflate(R.layout.fragment_cordova_webview, container, false);

        // Loading cordova configs
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(getActivity());

        CordovaPreferences preferences = parser.getPreferences();
        preferences.setPreferencesBundle(getActivity().getIntent().getExtras());

        String launchUrl;
        if (getArguments() != null) {
            launchUrl = getArguments().getString(URL_KEY);
        }  else {
            launchUrl = parser.getLaunchUrl();
        }
        ArrayList<PluginEntry> pluginEntries = parser.getPluginEntries();

        // Initializing cordova
        cordovaInterface = new CordovaInterfaceImpl(getActivity());

        if (savedInstanceState != null) cordovaInterface.restoreInstanceState(savedInstanceState);

        cordovaWebView = new CordovaWebViewImpl(CordovaWebViewImpl.createEngine(getActivity(), preferences));
        cordovaWebView.getView().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        if (!cordovaWebView.isInitialized()) {
            cordovaWebView.init(cordovaInterface, pluginEntries, preferences);
        }

        // Adding the proper cordova web view child to our root container
        View viewToBeAddedToRoot = (cordovaWebView.getView().getRootView() != null)
                ? cordovaWebView.getView().getRootView() : cordovaWebView.getView();
        view.addView(viewToBeAddedToRoot);

        // Loading the launch url, this takes care of initializing all the javascript for the web
        // app
//        cordovaWebView.showWebPage(launchUrl, true, true, null);
        cordovaWebView.loadUrl("file:///android_asset/www/baba.html");
//        cordovaWebView.loadUrl(launchUrl);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (cordovaWebView != null) cordovaWebView.handleResume(keepRunning);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (cordovaWebView != null) cordovaWebView.handlePause(keepRunning);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (cordovaWebView != null) cordovaWebView.handleDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (cordovaInterface != null) {
            cordovaInterface.onActivityResult(requestCode, resultCode,
                    data);
        }
    }

    @Override
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
        // If multitasking turned on, then disable it for activities that return results
        if (command != null) keepRunning = false; // Then why is it set to true from the start?

        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin plugin) {
    }

    @Override
    public Object onMessage(String id, Object data) {
        return null;
    }

    @Override
    public ExecutorService getThreadPool() {
        return Executors.newCachedThreadPool(); // This may generate multi thread pools. Is this
        // bad?
    }

    @Override
    public void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
        requestPermissions(plugin, requestCode, new String[]{permission});
    }

    @Override
    public void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
        if (getActivity() != null && isPermissionAPISupported()) {
            getActivity().requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        if (getActivity() == null || !isPermissionAPISupported()) return true; // really??

        int result = getActivity().checkSelfPermission(permission);
        return PackageManager.PERMISSION_GRANTED == result;
    }

//    public void runScript(Action action, String... args) {
//        String actionString = "";
//
//        switch (action) {
//            case NavigateTo:
//                if (args != null && args.length == 1) {
//                    actionString = "cordovaBridge.navigateTo('" + args[0] + "');";
//                }
//                if (args != null && args.length == 2) {
//                    actionString =
//                            "cordovaBridge.navigateTo('" + args[0] + "', '" + args[1] + "');";
//                }
//                break;
//
//            case ClickedMenuItem:
//                if (args != null && args.length == 1) {
//                    actionString = "cordovaBridge.clickedMenuItem('" + args[0] + "');";
//                }
//                break;
//
//            case CreateProjectExpense:
//                if (args != null && args.length == 1) {
//                    actionString =
//                            "cordovaBridge.setExpenseData({expenseReportId: '" + args[0]
//                                    + "', tripId: ''}); cordovaBridge.createExpense('', {});";
//                }
//                break;
//
//            case CreateTripExpense:
//                if (args != null && args.length == 1) {
//                    actionString =
//                            "cordovaBridge.setExpenseData({expenseReportId: '', tripId: '" + args[0]
//                                    + "'}); cordovaBridge.createExpense('', {});";
//                }
//                break;
//
//            case CreateGeneralExpense:
//                actionString =
//                        "cordovaBridge.setExpenseData({expenseReportId: '', tripId: ''}); "
//                                + "cordovaBridge.createExpense('', {});";
//                break;
//
//            case LoadExpense:
//                if (args != null && args.length == 1) {
//                    actionString =
//                            "cordovaBridge.loadExpense({'expenseReportId' : '','expenseId' : '"
//                                    + args[0] + "','tripId' : ''});";
//                }
//                break;
//
//            case ExpenseReportSubmitted:
//                if (args != null && args.length == 1) {
//                    actionString = "cordovaBridge.expenseReportSubmitted(" + args[0] + ");";
//                }
//                break;
//        }
//
//        String script = "javascript: {" + actionString + "}";
//        System.out.println("running cordova js script: " + script);
//        cordovaWebView.loadUrl(script);
//    }

    private boolean isPermissionAPISupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
