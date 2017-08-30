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

function webpackJs(name) {
    return gulp
        .src('src/main/resources/assets/js/' + name + '/main.js')
        .pipe(webpack({
            output: {
                filename: 'bundle.js'
            },
            devtool: 'source-map'
        }))
        .pipe(gulp.dest('./build/resources/main/assets/js/' + name));
}

gulp.task('webpack-home', function () {
    return webpackJs('home');
});

gulp.task('webpack-launcher', function () {
    return webpackJs('launcher');
});

gulp.task('webpack', ['webpack-home', 'webpack-launcher']);
gulp.task('all', ['css', 'webpack']);
gulp.task('default', ['all']);
