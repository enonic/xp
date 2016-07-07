/*
 Copy resources from /node_modules into the resources.
 */

var CONFIG = require("../config");
var gulp = require("gulp");
var path = require("path");
var pathResolver = require("../util/pathResolver");

function format(name, ext) {
    return path.format({name: name, ext: ext});
}

// The result is an array
function resolvePaths(entry) {
    return entry.ext.map(function (ext) {
        return path.join("node_modules", entry.dir, format(entry.name, ext));
    });
}

var src = pathResolver.flattenPaths(CONFIG.tasks.resources.entries.map(resolvePaths));
var dest = path.join(CONFIG.root.src, CONFIG.tasks.resources.dest);

gulp.task('resources', function (cb) {
    return gulp.src(src, {base: "node_modules"})
        .pipe(gulp.dest(dest));
});
