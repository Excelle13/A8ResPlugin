var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'coolMethod', [arg0]);
};

exports.logInfo = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'logInfo', [arg0]);
};

exports.logDebug = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'logDebug', [arg0]);
};

exports.logWarn = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'logWarn', [arg0]);
};

exports.logError = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'logError', [arg0]);
};

exports.doPrint = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'doPrint', [arg0]);
};


