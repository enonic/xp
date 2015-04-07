var baseDir = 'src/main/resources/web/admin';

module.exports = {

    options: {
        sourcemap: true
    }

};

add('common', 'common', true);
add('app_launcher', 'apps/app-launcher', false);
add('content_manager', 'apps/content-manager', false);
add('module_manager', 'apps/module-manager', false);
add('user_manager', 'apps/user-manager', false);
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
