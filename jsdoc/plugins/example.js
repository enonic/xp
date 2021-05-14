'use strict';

var fs = require('fs');
var path = require('path');

var beginStr = '// BEGIN';
var endStr = '// END';

function splitLines(str) {
    return str.split(/\r?\n/);
}

function findExamples(examples, lines) {
    var current = undefined;

    for (var i = 0; i < lines.length; i++) {
        var line = lines[i];

        if (line.startsWith(beginStr)) {
            current = '';

            var caption = line.substring(beginStr.length).trim();
            if (caption.length > 0) {
                current = '<caption>' + caption + '</caption>\n';
            }
        } else if (line.startsWith(endStr)) {
            if (current) {
                examples.push(current.trim());
            }

            current = undefined;
        } else {
            if (current !== undefined) {
                current += line + '\n';
            }
        }
    }
}

function exampleRefTag(doclet, tag) {
    var relPath = tag.value.trim();
    var fullPath = path.join(doclet.meta.path, relPath);
    var content = fs.readFileSync(fullPath, {encoding: 'utf8'}).trim();

    doclet.examples = doclet.examples || [];

    var lines = splitLines(content);
    findExamples(doclet.examples, lines);
}

exports.defineTags = function (dictionary) {
    dictionary.defineTag('example-ref', {
        onTagged: exampleRefTag
    });
};
