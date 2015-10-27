var auth = require('/lib/xp/auth.js');
var assert = require('/lib/xp/assert.js');

exports.generatePassword = function () {

    var result = auth.generatePassword();

    assert.assertNotNull(result);

};