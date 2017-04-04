cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "id": "com.instabug.cordova.plugin.Instabug",
        "file": "plugins/com.instabug.cordova.plugin/www/instabug.js",
        "pluginId": "com.instabug.cordova.plugin",
        "clobbers": [
            "cordova.plugins.instabug"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.3.2",
    "cordova-plugin-crosswalk-webview": "2.3.0",
    "me.tonny.cordova.plugins.multidex": "0.1.0",
    "com.instabug.cordova.plugin": "1.0.0"
};
// BOTTOM OF METADATA
});