var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
// Returns the current id provider.
var idProviderKey = portalLib.getIdProviderKey();

if (idProviderKey) {
    log.info('Id provider key: %s', idProviderKey);
}
// END

// BEGIN
// Id provider key returned.
var expected = "myidprovider" +
               "";
// END

assert.assertJsonEquals(expected, idProviderKey);
