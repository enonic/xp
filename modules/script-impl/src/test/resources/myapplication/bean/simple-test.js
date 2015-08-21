var assert = Java.type('org.junit.Assert');

var bean1 = __.newBean('com.enonic.xp.script.impl.bean.MyTestBeanOne');
assert.assertEquals('MyTestBeanOne', bean1.status);

var bean2 = __.newBean('com.enonic.xp.script.impl.bean.MyTestBeanTwo');
assert.assertEquals('MyTestBeanTwo, myapplication:/bean/simple-test.js', bean2.status);
