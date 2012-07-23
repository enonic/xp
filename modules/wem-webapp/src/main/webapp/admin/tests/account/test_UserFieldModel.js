function testUserFieldModel(t, model) {
    t.diag('Test UserFieldModel');
    t.ok(model.get('id'), 'ID field is present');
    t.ok(model.get('userstore_id'), 'Userstore ID field is present');
    t.ok(model.get('fieldname'), 'Fieldname field is present');
    t.ok(model.get('fieldlabel'), 'Fieldlabel field is present');
    t.ok(model.get('fieldtype'), 'Fieldtype field is present');
    t.ok(model.get('readonly'), 'Readonly field is present');
    t.ok(model.get('required'), 'Required field is present');
    t.ok(model.get('remote'), 'Remote field is present');
}

StartTest(function (t) {
    t.requireOk('Admin.model.account.UserFieldModel', function () {
        var userFieldModel = t.createUserFieldModel();
        testUserFieldModel(t, userFieldModel);
    });
});