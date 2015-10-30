var fs = require('fs');
var path = require('path');

var srcDir = 'src/main/resources/web/admin';
var destDir = 'src/main/resources/web/admin/d.ts';

var includedFiles = [];

function contains(a, obj) {
    for (var i = 0; i < a.length; i++) {
        if (a[i] === obj) {
            return true;
        }
    }
    return false;
}

function loadReference(baseFile, file) {

    var dirName = path.dirname(baseFile);
    var includeFile = path.resolve(dirName, file);

    if (includedFiles[includeFile]) {
        return "";
    }

    includedFiles[includeFile] = true;
    var src = fs.readFileSync(includeFile, 'utf-8');
    return replaceReferences(src, includeFile);
}

function replaceReferences(src, filepath) {
    return src.replace(/\/\/\/\s*<reference path="(.+)"\s*\/>/g, function (match, capture) {
        return loadReference(filepath, capture);
    });
}


module.exports = {

    commonDef: {
        options: {
            process: function (src, filepath) {
                return replaceReferences(src, filepath);
            }
        },
        src: srcDir + '/common/js/_all.d.ts',
        dest: destDir + '/common.d.ts'
    }

};
