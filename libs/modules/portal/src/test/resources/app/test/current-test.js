var assert = Java.type('org.junit.Assert');
var portal = require('/lib/xp/portal.js');

exports.getContentTest = function () {
    var result = portal.getContent();

    assert.assertEquals('_TODO_', result);
    return true;
};

exports.getComponentTest = function () {
    var result = portal.getComponent();

    assert.assertEquals('_TODO_', result);
    return true;
};

exports.getSiteTest = function () {
    var result = portal.getSite();

    assert.assertEquals('_TODO_', result);
    return true;
};
