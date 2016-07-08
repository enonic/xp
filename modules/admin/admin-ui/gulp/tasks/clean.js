/*
 Clean the resulted files from src.
 */

var CONFIG = require("../config");
var gulp = require("gulp");
var del = require("del");
var path = require("path");
var logger = require("../util/compileLogger");

function resolveCleanPaths() {
    return [path.join(CONFIG.root.src, CONFIG.tasks.clean.pattern)];
}

gulp.task('clean', function (cb) {
    var cleanPaths = resolveCleanPaths();
    var cleanDot = CONFIG.tasks.clean.cleanDot;

    return del(cleanPaths, {dot: cleanDot})
        .catch(function (e) {
            logger.pipeError(cb, e);
        })
        .then(function (files) {
            logger.log("Cleaned " + (files && files.length || 0) + " file(s).");
        });
});
