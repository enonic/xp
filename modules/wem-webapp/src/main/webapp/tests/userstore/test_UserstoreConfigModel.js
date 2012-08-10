function testUserstoreConfigModel(t, model) {
    t.diag('Test UserstoreConfigModel');
    t.ok(model.get('key'), 'Key field is present');
    t.ok(model.get('name'), 'Name field is present');
    t.ok(model.get('defaultStore'), 'DefaultStore field is present');
    t.ok(model.get('connectorName'), 'ConnectorName field is present');
    t.ok(model.get('configXML'), 'ConfigXML field is present');
    t.ok(model.get('lastModified'), 'LastModified field is present');
}
StartTest(function (t) {
    t.requireOk('Admin.model.userstore.UserstoreConfigModel', function () {
        var userstoreConfigModel = t.createUserstoreConfigModel();
        testUserstoreConfigModel(t, userstoreConfigModel);
    });
});