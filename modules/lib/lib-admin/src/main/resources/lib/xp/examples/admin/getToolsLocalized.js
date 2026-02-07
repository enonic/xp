var adminLib = require('/lib/xp/admin');

// BEGIN
// Get admin tools with Norwegian localization
var result = adminLib.getTools({
    locales: ['no', 'en']
});
log.info('Localized admin tools: ' + JSON.stringify(result, null, 2));
// END

// BEGIN
// Expected result (with Norwegian localization):
var expected = [
    {
        "key": "com.enonic.xp.app.main:home",
        "name": "Hjem",
        "description": "Hjemmesiden",
        "icon": "<svg>...</svg>",
        "systemApp": true
    }
];
// END
