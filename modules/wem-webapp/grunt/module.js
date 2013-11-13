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
        src: sourceFile,
        dest: targetFile
    };

    grunt.log.writeln('Adding less:' + args.target + ' task');
    grunt.config.set('less.' + args.target, config);
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

    grunt.log.writeln('Adding directives:' + args.target + ' task');
    grunt.config.set('directives.' + args.target, config);
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

    grunt.log.writeln('Adding ts:' + args.target + ' task');
    grunt.config.set('ts.' + args.target, config);
    grunt.task.run('ts:' + args.target);

}

function renameFiles(grunt, args) {

    var config = {
        expand: true,
        cwd: (sourceDir + '/' + args.module),
        src: '**/_all.*',
        dest: (targetDir + '/' + args.module + '/')
    };

    grunt.config.set('rename.' + args.target, config);
    grunt.log.writeln('Adding rename:' + args.target + ' task');
    grunt.task.run('rename:' + args.target);

}

function executeTask(grunt, args) {

    compileLess(grunt, args);
    concatLibJs(grunt, args);
    compileTs(grunt, args);
    renameFiles(grunt, args);

}

function renameFile(grunt, src, dest) {

    var fs = require('fs');
    var path = require('path');

    var dirName = path.dirname(dest);
    grunt.file.mkdir(dirName);

    fs.renameSync(src, dest, function (err) {
        if (err) {
            grunt.log.error('Failed to rename file');
            grunt.verbose.error();
        }
    });

    grunt.log.ok('Renamed ' + src + ' -> ' + dest);

}

function renameFilesTask(grunt, files) {

    files.forEach(function (filePair) {

        filePair.src.forEach(function (src) {

            renameFile(grunt, src, filePair.dest);

        });

    });

}

module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-directives');
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

    grunt.registerMultiTask('rename', 'Rename files', function () {

        renameFilesTask(grunt, this.files);

    });

};
