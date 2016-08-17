var gulpUtil = require("gulp-util");
var prettifyTime = require("./prettifyTime");
var handleErrors = require("./handleErrors");

module.exports.logWebpack = function (err, stats) {
    if (err) {
        throw new gulpUtil.PluginError('webpack', err);
    }

    var statColor = stats.compilation.warnings.length < 1 ? 'green' : 'yellow';
    if (stats.compilation.errors.length > 0) {
        // stats.compilation.errors.forEach(error => {
        //     handleErrors(error);
        //     statColor = 'red';
        // });
    } else {
        var compileTime = prettifyTime(stats.endTime - stats.startTime);
        var options = {hash: false, timings: false, chunks: false};
        gulpUtil.log(gulpUtil.colors[statColor](stats.toString(options)));
        gulpUtil.log('Compiled with', gulpUtil.colors.cyan('webpack'), 'in', gulpUtil.colors.magenta(compileTime));
    }
};

module.exports.pipeError = function (callback, err) {
    handleErrors("Error in plugin [" + (err.plugin || 'unknown' ) + "]");
    gulpUtil.log(gulpUtil.colors.red(err.message));
    if (err.stack) {
        gulpUtil.log(gulpUtil.colors.red(err.stack));
    }
    return callback();
};

module.exports.log = function (message, color) {
    var logger = gulpUtil.colors[color] || gulpUtil.colors.green;
    gulpUtil.log(logger(message));
};

