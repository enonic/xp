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
var expected = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAYCAYAAACbU/80AAAAGUlEQVR42u3BAQEAAACCIP+vbkhAAQAA7wYMGAAB93LuRQAAAABJRU5ErkJggg==';
// END

assert.assertEquals(expected, url);
