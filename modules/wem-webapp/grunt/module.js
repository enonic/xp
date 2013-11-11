'use strict';

function compileLess(grunt, target, data) {

    var inputFile = data.dir + '/styles/_module.less';
    if (!grunt.file.exists(inputFile)) {
        return;
    }

    var config = {
        files: {
        }
    };

    config.files[data.dir + '/styles/_all.css'] = inputFile;
    grunt.config.set('less.' + target, config);

    grunt.log.writeln('Adding less:' + target + ' task');
    grunt.task.run('less:' + target);

}

function concatLibJs(grunt, target, data) {

    var inputFile = data.dir + '/lib/_module.js';
    if (!grunt.file.exists(inputFile)) {
        return;
    }

    var config = {
        src: inputFile,
        dest: data.dir + '/lib/_all.js'
    };

    grunt.config.set('directives.' + target, config);

    grunt.log.writeln('Adding directives:' + target + ' task');
    grunt.task.run('directives:' + target);

}

function compileTs(grunt, target, data) {

    var inputFile = data.dir + '/js/_module.ts';
    if (!grunt.file.exists(inputFile)) {
        return;
    }

    var config = {
        src: inputFile,
        out: data.dir + '/ts/_all.js',
    };

    if (data['ts']) {
        config.options = data.ts;
    }

    grunt.config.set('ts.' + target, config);

    grunt.log.writeln('Adding ts:' + target + ' task');
    grunt.task.run('ts:' + target);

}

function executeTask(grunt, target, data) {

    compileLess(grunt, target, data);
    concatLibJs(grunt, target, data);
    compileTs(grunt, target, data);

}

module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-directives');
    grunt.loadNpmTasks('grunt-ts');

    grunt.registerMultiTask('module', 'Build an admin module', function () {
        executeTask(grunt, this.target, this.data);
    });

};
