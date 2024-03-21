var mailLib = require('/lib/xp/mail.js');
var assert = require('/lib/xp/testing.js');

// BEGIN
// Retrieve the default from mail address.
var defaultFromEmail = mailLib.getDefaultFromEmail();
// END

assert.assertEquals('noreply@domain.com', defaultFromEmail);
