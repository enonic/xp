var notify = require("gulp-notify");
var gulpUtil = require("gulp-util");
var path = require("path");

module.exports.handleErrors = function (errorObject) {
    var context = this || global;
    notify.onError(errorObject.toString().replace(/:\s{1}/g, ':\n')).apply(context, arguments);

    // Keep gulp from hanging on this task
    if (context && typeof context.emit === 'function') {
        global.emit('end');
    }
};

module.exports.handleWebpackErrors = function (errorObject) {
    var logger = gulpUtil.colors.red;
    var filePath = '';
    var fileName = '';
    var location;
    if (errorObject.module) {
        filePath = errorObject.module.userRequest.replace('\\\\', '/');
        fileName = path.basename(errorObject.module.userRequest);
    } else if (errorObject.file) {
        fileName = path.basename(errorObject.file);
        filePath = errorObject.file.replace('\\\\', '/');
    }

    if (errorObject.location) {
        location = errorObject.location.line + ':' + errorObject.location.character;
    }

    var message = fileName + ' ' + errorObject.message + '\n' + filePath + (location ? ':' + location : '');

    gulpUtil.log(logger(message));
};
