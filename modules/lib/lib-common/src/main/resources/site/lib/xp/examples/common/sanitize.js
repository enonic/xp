var common = require('/lib/xp/common');
var t = require('/lib/xp/testing');

// BEGIN
// Sanitize string
var sanitizedText = common.sanitize("Piña CØLADÆ <script>alert('hi!');</script>");
print(sanitizedText);

var result = sanitizedText === 'pina-coladae-script-alerthi-script';

// END
t.assertEquals('pina-coladae-script-alerthi-script', sanitizedText);

