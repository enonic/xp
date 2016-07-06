/*
 Concat commons lib file.
 */

var CONFIG = require("../config");
var gulp = require("gulp");
var include = require("gulp-include");
var rename = require("gulp-rename");
var logger = require("../util/compileLogger");

gulp.task('directives', function (cb) {
    var src = CONFIG.root.src + CONFIG.tasks.directives.src;
    // Place under the same root src
    var dest = CONFIG.root.src;

    return gulp.src(src)
        .pipe(include())
        .on('error', logger.pipeError.bind(null, cb))
        .pipe(rename(CONFIG.tasks.directives.dest))
        .pipe(gulp.dest(dest));
});
