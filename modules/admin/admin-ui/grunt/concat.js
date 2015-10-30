var fs = require('fs');
var path = require('path');

var srcDir = 'src/main/resources/web/admin';
var destDir = 'src/main/resources/web/admin/defs';

function loadReference(baseFile, file) {

    var dirName = path.dirname(baseFile);
    var includeFile = path.resolve(dirName, file);

    var src = fs.readFileSync(includeFile);
    return replaceReferences(src, includeFile);
}

function replaceReferences(src, filepath) {
    return src.replace(/\/\/\/\s*<reference path="(.+)"\s*\/>/g, function (match, capture) {
        return loadReference(filepath, capture);
    });
}


module.exports = {

    defs: {
        options: {
            process: function (src, filepath) {
                return replaceReferences(src, filepath);
            }
        },
        src: srcDir + '/common/js/_all.d.ts',
        dest: destDir + '/common.d.ts'
    }

};
