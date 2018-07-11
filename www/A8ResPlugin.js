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
exports.printSalesSmallSummary = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'printSalesSmallSummary', [arg0]);
};
exports.printSales = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'printSales', [arg0]);
};
exports.printSalesSummary = function (arg0, success, error) {
    exec(success, error, 'A8ResPlugin', 'printSalesSummary', [arg0]);
};



