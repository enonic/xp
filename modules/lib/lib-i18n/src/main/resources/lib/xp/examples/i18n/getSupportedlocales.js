var t = require('/lib/xp/testing');
var i18nLib = require('/lib/xp/i18n');

// BEGIN
var locales = i18nLib.getSupportedLocales();
// END

t.assertJsonEquals(['ca', 'en', 'es'], locales);
