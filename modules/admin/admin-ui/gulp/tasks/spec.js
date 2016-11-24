var gulp = require("gulp");
var open = require('gulp-open');
var CONFIG = require("../config");
var Server = require('karma').Server;
var os = require('os');
var path = require("path");

gulp.task('spec', ['ts'], function (done) {
    // Run Karma after a little timeout to make sure the spec files are built
    setTimeout(function () {
        new Server(CONFIG.spec, function () {
            var browser =
                os.platform() === 'linux' ? 'google-chrome' : (
                    os.platform() === 'darwin' ? 'google chrome' : (
                        os.platform() === 'win32' ? 'chrome' : 'firefox'));

            var uri = path.join(CONFIG.spec.remapIstanbulReporter.reports.html, "index.html");

            gulp.src('./')
                .pipe(open({
                    uri: uri,
                    app: browser
                }));

        }).start();
    }, 500);
});
