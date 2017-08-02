var common = require('/lib/xp/common');
var assert = require('/lib/xp/assert');

// BEGIN
// Prettify string
var prettifiedText = common.prettify('Piña CØLADÆ');

var result = prettifiedText === 'pina-coladae';

// END

assert.assertEquals('pina-coladae', prettifiedText);

