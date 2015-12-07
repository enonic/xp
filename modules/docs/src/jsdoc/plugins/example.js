'use strict';

var fs = require('fs');
var path = require('path');

function exampleRefTag(doclet, tag) {
    var match = /\{(.+)}(.*)/.exec(tag.value);
    if (!match) {
        return;
    }

    var relPath = match[1].trim();
    var caption = match[2].trim();

    var fullPath = path.join(doclet.meta.path, relPath);
    var content = fs.readFileSync(fullPath, {encoding: 'utf8'}).trim();

    if (caption) {
        content = '<caption>' + caption + '</caption>\n' + content;
    }

    doclet.examples = doclet.examples || [];
    doclet.examples.push(content);
}

exports.defineTags = function (dictionary) {
    dictionary.defineTag('example-ref', {
        onTagged: exampleRefTag
    });
};
