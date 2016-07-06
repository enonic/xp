/*
 Compile TypeScript and create modules.
 TSC + Gulp for old modules, webpack for new ones.
 */

import CONFIG from "../config";
import gulp from "gulp";
import gulpSequence from "gulp-sequence";
import tsc from "gulp-typescript";
import typescript from "typescript";
import sourcemaps from "gulp-sourcemaps";
import newer from "gulp-newer";
import webpack from "webpack";
import assign from "deep-assign";
import newerStream from "../util/newerStream";
import nameResolver from "../util/nameResolver";
import pathResolver, {anyPath} from "../util/pathResolver";
import webpackConfig from "../util/webpackConfig";
import logger from "../util/compileLogger";

const subtasks = CONFIG.tasks.js.files;

const tsResolver = nameResolver.bind(null, 'ts');

function filterTasks(tasks, callback) {
    const filtered = {};

    for (const name in tasks) {
        if (callback(tasks[name])) {
            filtered[name] = tasks[name];
        }
    }

    return filtered;
}

/*
 Modules processed with plain TS compiler and gulp.
 Will be moved to webpack in near future.
 js: common
 js: live
 */
const tsTasks = filterTasks(subtasks, task => !task.name);

for (const name in tsTasks) {
    const task = tsTasks[name];

    const taskPath = pathResolver(task.src, task.dest, CONFIG.root.src);
    const newerPath = anyPath(taskPath.src.dir, 'ts');

    const tsOptions = assign({typescript, out: taskPath.dest.full}, CONFIG.tasks.js.ts);

    gulp.task(tsResolver(name), () => {

        const tsNewer = gulp.src(newerPath)
            .pipe(newer(taskPath.dest.full))
            .pipe(newerStream(taskPath.src.full));

        const tsResult = tsNewer
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