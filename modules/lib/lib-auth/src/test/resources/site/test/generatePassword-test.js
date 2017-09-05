var auth = require('/lib/xp/auth.js');
var t = require('/lib/xp/testing.js');

exports.generatePassword = function () {

    var result = auth.generatePassword();

    t.assertTrue(result);

};