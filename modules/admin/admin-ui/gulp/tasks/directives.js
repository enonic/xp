/*
 Concat commons lib file.
 Files will be searched under src, 
 /node_modules and /bower_components roots.
 */

var CONFIG = require("../config");
var gulp = require("gulp");
var include = require("gulp-include");
var rename = require("gulp-rename");
var path = require("path");
var logger = require("../util/compileLogger");

function resolvePath(dir) {
    return path.resolve(__dirname, "../../" + dir);
}

gulp.task('directives', function (cb) {
    var src = CONFIG.root.src + CONFIG.tasks.directives.src;
    // Place under the same root src
    var dest = CONFIG.root.src;

    // Base paths to search in 
    var includeConfig = {
        hardFail: true,
        includePaths: [
            path.dirname(src),
            "node_modules",
            "bower_components"
        ].map(dir => resolvePath(dir))
    };

    return gulp.src(src)
        .pipe(include(includeConfig))
        .on('error', logger.pipeError.bind(null, cb))
        .pipe(rename(CONFIG.tasks.directives.dest))
        .pipe(gulp.dest(dest));
});
