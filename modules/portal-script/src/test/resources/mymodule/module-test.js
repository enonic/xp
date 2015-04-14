assert.assertEquals('mymodule:/module-test.js', module.id);
assert.assertEquals('mymodule', module.name);
assert.assertEquals(true, exports == module.exports);

exports.val = 1;
assert.assertEquals(1, exports.val);
assert.assertEquals(1, module.exports.val);

module.exports.val = 2;
assert.assertEquals(2, exports.val);
assert.assertEquals(2, module.exports.val);
