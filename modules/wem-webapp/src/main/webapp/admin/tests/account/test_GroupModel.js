function testGroupModel(t, model) {
    t.diag('Test GroupModel');
    t.ok(model.get('key'), 'Key field is present');
    t.ok(model.get('name'), 'Name field is present');
    t.ok(model.get('userStore'), 'userStore field is present');
    t.ok(model.get('type'), 'Type field is present');
}

StartTest(function (t) {
    t.requireOk('Admin.model.account.GroupModel', function () {
        var groupModel = t.createGroupModel();
        testGroupModel(t, groupModel);
    });
});