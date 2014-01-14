var baseDir = 'src/main/webapp/admin';

module.exports = {

    options: {
        sourcemap: true
    }

};

add('common', 'common', true);
add('app_launcher', 'apps/app-launcher', false);
add('content_manager', 'apps/content-manager', false);
add('module_manager', 'apps/module-manager', false);
add('schema_manager', 'apps/schema-manager', false);
add('template_manager', 'apps/template-manager', false);
add('live_edit', 'live-edit', false);

function add(name, path, declaration) {

    module.exports[name] = {
        src: baseDir + '/' + path + '/js/_module.ts',
        out: baseDir + '/' + path + '/js/_all.js',
        options: {
            declaration: declaration
        }
    };

}
