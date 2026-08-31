/* global app*/
var t = require('/lib/xp/testing');

exports.testAppConfig = function () {
    t.assertEquals('value', app.config['key']);
    t.assertEquals('dotted value', app.config['dotted.key']);
};
