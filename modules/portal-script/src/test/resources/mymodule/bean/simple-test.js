var bean = __.getBean('mybean');
assert.assertEquals(0, bean.count);
assert.assertEquals(1, bean.count);

bean.count = 10;
assert.assertEquals(10, bean.count);
assert.assertEquals(11, bean.count);
