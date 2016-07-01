/*
 Main tasks
 */

import gulp from "gulp";
import gulpSequence from "gulp-sequence";

gulp.task('all: no ts', ['css', 'directives']);
gulp.task('all: no css', gulpSequence('directives', 'js'));
gulp.task('all', gulpSequence(['css', 'all: no css']));
gulp.task('default', ['all']);
