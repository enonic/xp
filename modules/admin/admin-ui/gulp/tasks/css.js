/*
 Compile LESS files, apply autoprefixer and generate sourcemaps.
 */

import CONFIG from "../config";
import gulp from "gulp";
import less from "gulp-less";
import autoprefixer from "less-plugin-autoprefix";
import sourcemaps from "gulp-sourcemaps";
import rename from "gulp-rename";
import path from "path";
import nameResolver from "../util/nameResolver";
import {pipeError as error} from "../util/compileLogger";

const subtasks = CONFIG.tasks.css.files;
const autoprefix = new autoprefixer(CONFIG.tasks.css.autoprefixer);

const cssResolver = nameResolver.bind(null, 'css');

/*
 Generate a separate task for each dest of less file:
 css: common
 css: live
 css: home
 css: editor
 css: launcher
 */
for (const name in subtasks) {
    const task = subtasks[name];
    const dest = task.assets ? CONFIG.assets.dest : CONFIG.root.dest;

    gulp.task(cssResolver(name), (cb) => {
        return gulp.src(path.join(CONFIG.root.src, task.src))
            .pipe(sourcemaps.init())
            .pipe(less({
                plugins: [autoprefix]
            }))
            .on('error', error.bind(null, cb))
            .pipe(sourcemaps.write())
            .pipe(rename(task.dest))
            .pipe(gulp.dest(dest));
    });
}

/*
 Main CSS task
 */
gulp.task('css', Object.keys(subtasks).map(cssResolver));