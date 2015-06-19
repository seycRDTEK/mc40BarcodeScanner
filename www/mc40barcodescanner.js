cordova.define("cordova/plugin/mc40barcodescanner", 
  function(require, exports, module) {
    var exec = require("cordova/exec");
    var MC40BarcodeScanner = function() {};
    
  /**
   * Check if the device has a possibility to receive DataWedge output
   * the successCallback function receives one string as parameter
   */
  MC40BarcodeScanner.prototype.startDWReception = function(successCallback, failureCallback) {
    exec(successCallback, failureCallback, 'mc40BarcodeScanner', 'startDWReception', []);
  };

  /**
   * Stop Receiving.
   */
  MC40BarcodeScanner.prototype.stopDWReception = function(successCallback,failureCallback) {
    exec(successCallback, failureCallback, 'mc40BarcodeScanner', 'stopDWReception', []);
  };

    
    var mc40BarcodeScanner = new MC40BarcodeScanner();
    module.exports = mc40BarcodeScanner;

});


if(!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.mc40BarcodeScanner) {
    window.plugins.mc40BarcodeScanner = cordova.require("cordova/plugin/mc40barcodescanner");
}

var mc = cordova.require("cordova/plugin/mc40barcodescanner");
mc.startDWReception(function (msg) {
        }, function () {
            alert("Error while receiving scan MC40");
        });