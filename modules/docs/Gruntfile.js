module.exports = function (grunt) {

    grunt.initConfig({
        jsdoc: {
            dist: {
                src: [
                    'src/jsdoc/global.js',
                    '../lib/lib-*/src/main/resources/site/lib/xp/*.js'
                ],
                options: {
                    destination: 'target/jsdoc',
                    readme: 'src/jsdoc/index.md',
                    template: 'src/jsdoc/template',
                    configure: 'src/jsdoc/conf.json'
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-jsdoc');
    grunt.registerTask('all', ['jsdoc']);

};
