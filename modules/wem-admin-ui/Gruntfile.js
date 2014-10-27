module.exports = function (grunt) {

    // Add grunt plugins
    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-directives');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks("grunt-newer-explicit");
    grunt.loadNpmTasks('grunt-karma');
    grunt.loadNpmTasks('grunt-typedoc');

    // Load grunt task configuration from grunt/*
    require('load-grunt-config')(grunt);

    // Register aliases
    grunt.registerTask('default', 'all');

    grunt.registerTask('all', ['less', 'newer', 'directives']);
    grunt.registerTask('all_no_ts', ['less', 'directives']);

    grunt.registerTask('common', ['all_no_ts', 'ts:common']);
    grunt.registerTask('cm', ['all_no_ts', 'ts:content_manager']);
    grunt.registerTask('le', ['all_no_ts', 'ts:live_edit']);
    grunt.registerTask('md', ['all_no_ts', 'ts:module_manager']);
    grunt.registerTask('al', ['all_no_ts', 'ts:app_launcher']);
    grunt.registerTask('ccl', ['all_no_ts', 'ts:common', 'ts:content_manager', 'ts:live_edit']);

};
