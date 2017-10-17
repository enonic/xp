var t = require('/lib/xp/testing');
var i18n = require('/lib/xp/i18n');

exports.testgetLocales = function () {
    var result = i18n.getSupportedLocales();

    t.assertJsonEquals(['ca', 'en', 'es'], result);
};

exports.testExamples = function () {
    t.runScript('/site/lib/xp/examples/i18n/getSupportedlocales.js');
};
