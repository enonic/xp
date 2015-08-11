var bean1 = __.newBean('com.enonic.xp.portal.impl.script.bean.MyTestBeanOne');
assert.assertEquals('MyTestBeanOne', bean1.status);

var bean2 = __.newBean('com.enonic.xp.portal.impl.script.bean.MyTestBeanTwo');
assert.assertEquals('MyTestBeanTwo, myapplication:/bean/simple-test.js', bean2.status);
