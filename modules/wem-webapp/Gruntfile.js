module.exports = function(grunt) {

    grunt.loadNpmTasks('grunt-typescript');
    grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-contrib-sass');
	
    grunt.initConfig({

		typescript: {
        	api: {
            	src: ['src/main/webapp/admin2/api/js/main.ts'],
            	dest: 'src/main/webapp/admin2/api/js/all.js',
            	options: {
              		target: 'es5',
				  	sourcemap: true,
				  	declaration: true 
            	}
          	},
        	space_manager: {
            	src: ['src/main/webapp/admin2/apps/space-manager/js/main.ts'],
            	dest: 'src/main/webapp/admin2/apps/space-manager/js/all.js',
            	options: {
              		target: 'es5',
				  	sourcemap: true 
            	}
          	}
			
     	},

      	watch: {
        	files: 'src/main/webapp/admin2/**/*.ts',
            tasks: ['typescript']
     	}

    });
	
	grunt.registerTask('default', 'watch');
	
};
