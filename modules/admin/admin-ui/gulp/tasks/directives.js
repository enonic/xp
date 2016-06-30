/*
 Concat commons lib file.
 */

import CONFIG from "../config";
import gulp from "gulp";
import include from "gulp-include";
import rename from "gulp-rename";
import {pipeError as error} from "../util/compileLogger";

gulp.task('directives', (cb) => {
    const src = `${CONFIG.root.src}${CONFIG.tasks.directives.src}`;
    // Place under the same root src
    const dest = CONFIG.root.src;

    return gulp.src(src)
        .pipe(include())
        .on('error', error.bind(null, cb))
        .pipe(rename(CONFIG.tasks.directives.dest))
        .pipe(gulp.dest(dest));
});