var t = require('/lib/xp/testing');
var i18nLib = require('/lib/xp/i18n');

// BEGIN
// Localizes a simple message.
var message1 = i18nLib.localize({
    key: 'mymessage'
});
// END

// BEGIN
// Localizes a message with placeholders.
var message2 = i18nLib.localize({
    key: 'mymessage_with_placeholder',
    locale: "no",
    values: ["John", "London"]
});
// END

t.assertEquals('[mymessage]', message1);
t.assertEquals('[mymessage_with_placeholder, John, London]', message2);
