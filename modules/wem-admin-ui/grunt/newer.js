var baseDir = 'src/main/resources/web/admin';

add('common', 'common', []);
add('app_launcher', 'apps/app-launcher', ['common']);
add('content_manager', 'apps/content-manager', ['common']);
add('module_manager', 'apps/module-manager', ['common']);
add('schema_manager', 'apps/schema-manager', ['common']);
add('template_manager', 'apps/template-manager', ['common']);
add('live_edit', 'live-edit', ['common']);

function add(name, path, dependencies) {

    var srcList = [baseDir + '/' + path + '/js/**'];
    for (var i = 0; i < dependencies.length; i++) {
        srcList.push(baseDir + '/' + dependencies[i] + '/js/**')
    }

    module.exports[name + "_ts"] = {
        src: srcList,
        dest: baseDir + '/' + path + '/js/_all.js',
        options: {
            tasks: [ "ts:" + name ]
        }
    };

}
