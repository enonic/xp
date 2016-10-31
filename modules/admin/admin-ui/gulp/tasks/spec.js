var gulp = require("gulp");
var gulpSequence = require("gulp-sequence");
var CONFIG = require("../config");
var Server = require('karma').Server;

gulp.task('karma', function (done) {
    setTimeout(function(){
        new Server(CONFIG.spec, done).start();
    }, 500);
});

gulp.task('spec', gulpSequence('ts:spec', ['karma']));
