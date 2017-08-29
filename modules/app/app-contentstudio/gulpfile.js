var gulp = require('gulp');
var less = require('gulp-less');
var sourceMaps = require("gulp-sourcemaps");
var LessAutoPrefix = require('less-plugin-autoprefix');
var webpack = require('webpack-stream');

var autoPrefix = new LessAutoPrefix({
    browsers: ['last 3 versions', 'ie 11']
});

gulp.task('css', function () {
    return gulp
        .src('src/main/resources/assets/styles/*.less')
        .pipe(sourceMaps.init())
        .pipe(less({
            plugins: [autoPrefix],
            relativeUrls: true
        }))
        .pipe(sourceMaps.write('.'))
        .pipe(gulp.dest('./build/resources/main/assets/styles'));
});
