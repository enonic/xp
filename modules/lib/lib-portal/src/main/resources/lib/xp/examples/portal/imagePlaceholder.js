var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.imagePlaceholder({
    width: 32,
    height: 24
});
// END

// BEGIN
// URL returned.
var expected = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAYCAYAAACbU/80AAAAGklEQVR4nO3BAQ0AAADCoPdPbQ8HFAAAAPwbDwAAAQUUSzEAAAAASUVORK5CYII=';
// END

assert.assertEquals(expected, url);
