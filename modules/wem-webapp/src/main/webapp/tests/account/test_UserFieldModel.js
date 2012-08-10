function testUserFieldModel(t, model) {
    t.diag('Test UserFieldModel');
    t.ok(model.get('type'), 'Type field is present');
    t.ok(model.get('readOnly'), 'Readonly field is present');
    t.ok(model.get('required'), 'Required field is present');
    t.ok(model.get('remote'), 'Remote field is present');
    t.ok(model.get('iso'), 'Iso field is present');
}

StartTest(function (t) {
    t.requireOk('Admin.model.account.UserFieldModel', function () {
        var userFieldModel = t.createUserFieldModel();
        testUserFieldModel(t, userFieldModel);
    });
});