/*
 Compile TypeScript and create modules.
 TSC + Gulp for old modules, webpack for new ones.
 */

var CONFIG = require("../config");
var fs = require("fs");
var path = require("path");
var gulp = require("gulp");
var gulpSequence = require("gulp-sequence");
var forOwn = require("lodash.forown");
var tsc = require("gulp-typescript");
var typescript = require("typescript");
var sourcemaps = require("gulp-sourcemaps");
var newer = require("gulp-newer");
var webpack = require("webpack");
var assign = require("deep-assign");
var newerStream = require("../util/newerStream");
var nameResolver = require("../util/nameResolver");
var pathResolver = require("../util/pathResolver");
var webpackConfig = require("../util/webpackConfig");
var logger = require("../util/compileLogger");

var subtasks = CONFIG.tasks.js.files;
var tsResolver = nameResolver.bind(null, 'ts');

function filterTasks(tasks, callback) {
    const filtered = {};

    forOwn(tasks, function (task, name) {
        if (callback(task)) {
            filtered[name] = task;
        }
    });

    return filtered;
}

/*
 Modules processed with plain TS compiler and gulp.
 Will be moved to webpack in the nearest future.
 js:common
 js:live
 */
var tsTasks = filterTasks(subtasks, function (task) {
    return !task.name;
});

forOwn(tsTasks, function (task, name) {
    var taskPath = pathResolver.commonPaths(task.src, task.dest, CONFIG.root.src);
    var newerPath = pathResolver.anyPath(taskPath.src.dir, 'ts');

    var tsOptions = assign({typescript: typescript, out: taskPath.dest.full}, CONFIG.tasks.js.ts);

    gulp.task(tsResolver(name), function () {
        var tsNewer = gulp.src(newerPath)
            .pipe(newer(taskPath.dest.full))
            .pipe(newerStream(taskPath.src.full));

        var tsResult = tsNewer
            .pipe(sourcemaps.init())
            .pipe(tsc(tsOptions));

        // generate *.js
        tsResult.js
            .pipe(sourcemaps.write('./'))
            .pipe(gulp.dest('./'));

        // generate *.d.js
        return tsResult.dts
            .pipe(gulp.dest('./'));
    });
});

gulp.task('ts', gulpSequence(Object.keys(tsTasks).map(tsResolver)));

/*
 Modules processed with webpack.
 js:home
 js:launcher
 js:applications
 js:content
 js:user
 */
var webpackTasks = filterTasks(subtasks, function (task) {
    return !!task.name;
});

gulp.task('webpack', function (cb) {
    webpack(webpackConfig(webpackTasks), function (err, stats) {
        logger.logWebpack(err, stats);
        cb();
    });
});

/*
 Finalizing tasks, like copying build sources after build
 1. Copy launcher to assets to be able to `require` it,
 when building custom elements.
 */
gulp.task('webpack-after', ['webpack'], function (cb) {
    var root = path.normalize(path.join('/', CONFIG.root.dest));
    var assets = path.normalize(path.join('/', CONFIG.assets.dest));
    var srcTemplate = "." + path.join(root, CONFIG.tasks.js.webpack.dest);

    CONFIG.tasks.js.assets.forEach(function (name) {
        var src = srcTemplate.replace(CONFIG.tasks.js.webpack.param, name);
        var dest = "." + path.join(assets, 'js', (name + '.js'));
        var f = fs.createWriteStream(dest);
        f.on('finish', function () {
            logger.log('Copied: ' + src + '\t->\t' + dest);
        });
        fs.createReadStream(src).pipe(f);
    });
    cb();
});

/*
 Main JS task
 */
gulp.task('js', gulpSequence('ts:common', 'ts:spec', ['ts:live', 'webpack-after']));