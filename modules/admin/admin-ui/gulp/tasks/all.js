/*
 Main tasks
 */

const gulp = require("gulp");
const gulpSequence = require("gulp-sequence");

gulp.task('all', gulpSequence(['css', 'directives'], 'js'));
gulp.task('all+clean', gulpSequence('clean', 'all'));
gulp.task('all+lint', ['lint', 'all']);
gulp.task('all+clean+lint', gulpSequence('clean', ['lint', 'all']));
gulp.task('default', ['all']);
