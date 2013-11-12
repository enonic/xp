'use strict';

var sourceDir = "src/main/webapp";
var targetDir = "target/main/webapp";

function compileLess(grunt, args) {

    var baseDir = sourceDir + '/' + args.module + '/styles';
    var sourceFile = baseDir + '/_module.less';
    var targetFile = baseDir + '/_all.css';

    if (!grunt.file.exists(sourceFile)) {
        return;
    }

    var config = {
        files: {
        }
    };

    config.files[targetFile] = sourceFile;
    grunt.config.set('less.' + args.target, config);

    grunt.log.writeln('Adding less:' + args.target + ' task');
    grunt.task.run('less:' + args.target);

}

function concatLibJs(grunt, args) {

    var baseDir = sourceDir + '/' + args.module + '/lib';
    var sourceFile = baseDir + '/_module.js';
    var targetFile = baseDir + '/_all.js';

    if (!grunt.file.exists(sourceFile)) {
        return;
    }

    var config = {
        src: sourceFile,
        dest: targetFile
    };

    grunt.config.set('directives.' + args.target, config);

    grunt.log.writeln('Adding directives:' + args.target + ' task');
    grunt.task.run('directives:' + args.target);

}

function compileTs(grunt, args) {

    var baseDir = sourceDir + '/' + args.module + '/js';
    var sourceFile = baseDir + '/_module.ts';
    var targetFile = baseDir + '/_all.js';

    if (!grunt.file.exists(sourceFile)) {
        return;
    }

    var config = {
        src: sourceFile,
        out: targetFile,
        options: {
            sourcemap: args.options.sourcemap,
            declaration: args.options.declaration
        }
    };

    grunt.config.set('ts.' + args.target, config);
    grunt.log.writeln('Adding ts:' + args.target + ' task');
    grunt.task.run('ts:' + args.target);

}

function renameFiles(grunt, args) {

    var config = {
        files: [
            {
                expand: true,
                cwd: (sourceDir + '/' + args.module),
                src: '*/_all.*',
                dest: (targetDir + '/' + args.module + '/')
            }
        ],
        options: {
            ignore: true
        }
    };

    grunt.config.set('rename.' + args.target, config);
    grunt.log.writeln('Adding rename:' + args.target + ' task');
    grunt.task.run('rename:' + args.target);

}

function executeTask(grunt, args) {

    compileLess(grunt, args);
    concatLibJs(grunt, args);
    compileTs(grunt, args);
    // renameFiles(grunt, args);

}

module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-directives');
    grunt.loadNpmTasks('grunt-rename');
    grunt.loadNpmTasks('grunt-ts');

    grunt.registerMultiTask('module', 'Build an admin module', function () {

        var options = this.options({
            sourcemap: false,
            declaration: false
        });

        var args = {
            target: this.target,
            module: this.data.module,
            options: options
        };

        executeTask(grunt, args);
    });

};
