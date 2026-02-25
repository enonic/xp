const t = require('/lib/xp/testing');
const i18n = require('/lib/xp/i18n');

exports.testGetLocales = function () {
    var result = i18n.getSupportedLocales();

    t.assertJsonEquals(['ca', 'en', 'es'], result);
};

exports.testGetLocalesBundle = function () {
    let result = i18n.getSupportedLocales(['bundle']);

    t.assertJsonEquals(['ca', 'en', 'es'], result);
};

exports.testExamples = function () {
    t.runScript('/lib/xp/examples/i18n/getSupportedlocales.js');
};
