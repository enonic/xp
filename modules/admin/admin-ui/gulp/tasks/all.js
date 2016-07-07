/*
 Main tasks
 */

var gulp = require("gulp");
var gulpSequence = require("gulp-sequence");

gulp.task('all:no-ts', ['css', 'directives']);
gulp.task('all:no-css', gulpSequence('directives', 'js'));
gulp.task('all', gulpSequence(['css', 'all:no-css']));
gulp.task('all:clean', gulpSequence('clean', 'all'));
gulp.task('default', ['all']);
