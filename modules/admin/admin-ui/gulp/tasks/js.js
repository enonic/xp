/*
 Compile TypeScript and create modules.
 TSC + Gulp for old modules, webpack for new ones.
 */

import CONFIG from "../config";
import gulp from "gulp";
import gulpSequence from "gulp-sequence";
import typescript from "typescript";
import tsc from "gulp-typescript";
import webpack from "webpack";
import assign from "deep-assign";
import path from "path";
import nameResolver from "../util/nameResolver";
import webpackConfig from "../util/webpackConfig";
import logger from "../util/compileLogger";

const subtasks = CONFIG.tasks.js.files;

const tsResolver = nameResolver.bind(null, 'ts');

const filterTasks = (tasks, callback) => {
    const filtered = {};

    for (const name in tasks) {
        if (callback(tasks[name])) {
            filtered[name] = tasks[name];
        }
    }

    return filtered;
};

/*
 Modules processed with plain TS compiler and gulp.
 Will be moved to webpack in near future.
 js: common
 js: live
 */
const tsTasks = filterTasks(subtasks, task => !task.name);
const tsDest = CONFIG.root.src;

for (const name in tsTasks) {
    const task = tsTasks[name];
    const tsOptions = assign({typescript, out: task.dest}, CONFIG.tasks.js.ts);

    gulp.task(tsResolver(name), (cb) => {
        tsOptions.out = task.dest;

        const tsResult = gulp.src(path.join(CONFIG.root.src, task.src))
            .pipe(tsc(tsOptions));

        // generate *.js
        tsResult.js
            .pipe(gulp.dest(tsDest));

        // generate *.d.js
        return tsResult.dts
            .pipe(gulp.dest(tsDest))
    });
}

gulp.task('ts', gulpSequence(...Object.keys(tsTasks).map(tsResolver)));

/*
 Modules processed with webpack.
 js: home
 js: launcher
 js: applications
 js: content
 js: user
 */
const webpackTasks = filterTasks(subtasks, task => !!task.name);

gulp.task('webpack', (cb) => {
    webpack(webpackConfig(webpackTasks), (err, stats) => {
        logger(err, stats);
        cb();
    });
});

/*
 Main CSS task
 */
gulp.task('js', gulpSequence('ts: common', ['ts: live', 'webpack']));