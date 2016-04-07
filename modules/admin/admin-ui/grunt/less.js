var baseDir = 'src/main/resources/web/admin';
var assetsDir = 'src/main/resources/assets';

add('common', 'common');
add('live_edit', 'live-edit');
addHome();
addLauncher();
addHtmlEditor();

function add(name, path) {

    module.exports[name] = {
        src: baseDir + '/' + path + '/styles/_module.less',
        dest: baseDir + '/' + path + '/styles/_all.css',
        options: {
            sourceMap: true,
            relativeUrls: true
        }
    };
}

function addHome() {

    module.exports['home'] = {
        src: baseDir + '/common/styles/apps/home/home.less',
        dest: baseDir + '/common/styles/_home.css',
        options: {
            sourceMap: false,
            relativeUrls: true
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

function addHtmlEditor() {

    module.exports['htmleditor'] = {
        src: baseDir + '/common/styles/api/util/htmlarea/html-editor.module.less',
        dest: baseDir + '/common/styles/api/util/htmlarea/html-editor.css',
        options: {
            sourceMap: false
        }
    };
}
