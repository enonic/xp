var baseDir = 'src/main/resources/web/admin';
var assetsDir = 'src/main/resources/assets';

add('common', 'common');
add('live_edit', 'live-edit');
addHome();
addLauncher();

function add(name, path) {

    module.exports[name] = {
        src: baseDir + '/' + path + '/styles/_module.less',
        dest: baseDir + '/' + path + '/styles/_all.css',
        options: {
            sourceMap: true
        }
    };
}

function addHome() {

    module.exports['home'] = {
        src: baseDir + '/common/styles/apps/home/home.less',
        dest: baseDir + '/common/styles/_home.css',
        options: {
            sourceMap: false
        }
    };
}

function addLauncher() {

    module.exports['launcher'] = {
        src: baseDir + '/common/styles/apps/launcher/launcher.less',
        dest: assetsDir + '/styles/_launcher.css',
        options: {
            sourceMap: false
        }
    };
}
