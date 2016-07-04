import gulpUtil from "gulp-util";
import prettifyTime from "./prettifyTime";
import handleErrors from "./handleErrors";

export default (err, stats) => {
    if (err) {
        throw new gulpUtil.PluginError('webpack', err);
    }

    let statColor = stats.compilation.warnings.length < 1 ? 'green' : 'yellow';

    if (stats.compilation.errors.length > 0) {
        // stats.compilation.errors.forEach(error => {
        //     handleErrors(error);
        //     statColor = 'red';
        // });
    } else {
        const compileTime = prettifyTime(stats.endTime - stats.startTime);
        const options = {hash: false, timings: false, chunks: false};
        gulpUtil.log(gulpUtil.colors[statColor](stats.toString(options)));
        gulpUtil.log('Compiled with', gulpUtil.colors.cyan('webpack'), 'in', gulpUtil.colors.magenta(compileTime));
    }
};

export function pipeError(callback, err) {
    handleErrors(`Error in plugin [${ err.plugin || 'unknown' }]`);
    gulpUtil.log(gulpUtil.colors.red(err.message));
    if (err.stack) {
        gulpUtil.log(gulpUtil.colors.red(err.stack));
    }
    return callback();
}

export function log(message, color = 'green') {
    const logger = gulpUtil.colors[color] || gulpUtil.colors.green;
    gulpUtil.log(logger(message));
}
