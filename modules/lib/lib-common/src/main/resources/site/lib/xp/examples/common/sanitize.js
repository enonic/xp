var common = require('/lib/xp/common');
var assert = require('/lib/xp/assert');

// BEGIN
// Sanitize string
var sanitizedText = common.sanitize("Piña CØLADÆ <script>alert('hi!');</script>");
print(sanitizedText);

var result = sanitizedText === 'pina-coladae-script-alerthi-script';

// END
assert.assertEquals('pina-coladae-script-alerthi-script', sanitizedText);

