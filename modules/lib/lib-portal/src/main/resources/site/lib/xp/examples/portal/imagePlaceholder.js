var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var url = portalLib.imagePlaceholder({
    width: 32,
    height: 24
});
// END

// BEGIN
// URL returned.
var expected = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAAAwCAYAAAChS3wfAAAAIklEQVR42u3BAQ0AAADCoPdPbQ8HFAAAAAAAAAAAAAAA8GYwMAABiGDrBgAAAABJRU5ErkJggg==';
// END

assert.assertEquals('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAAAwCAYAAAChS3wfAAAAIklEQVR42u3BAQ0AAADCoPdPbQ8HFAAAAAAAAAAAAAAA8GYwMAABiGDrBgAAAABJRU5ErkJggg==', url);
