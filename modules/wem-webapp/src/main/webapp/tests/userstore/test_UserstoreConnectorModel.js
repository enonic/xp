function testUserstoreConnectorModel(t, model) {
    t.diag('Test UserstoreConnectorModel');
    t.ok(model.get('name'), 'Name field is present');
    t.ok(model.get('pluginType'), 'PluginType field is present');
    t.ok(model.get('canCreateUser'), 'CanCreateUser field is present');
    t.ok(model.get('canUpdateUser'), 'CanUpdateUser field is present');
    t.ok(model.get('canUpdateUserPassword'), 'CanUpdateUserPassword field is present');
    t.ok(model.get('canDeleteUser'), 'CanDeleteUser field is present');
    t.ok(model.get('canCreateGroup'), 'CanCreateGroup field is present');
    t.ok(model.get('canUpdateGroup'), 'CanUpdateGroup field is present');
    t.ok(model.get('canReadGroup'), 'CanReadGroup field is present');
    t.ok(model.get('canDeleteGroup'), 'CaDeleteGroup field is present');
    t.ok(model.get('groupsLocal'), 'GroupsLocal field is present');
}
StartTest(function (t) {
    t.requireOk('Admin.model.userstore.UserstoreConnectorModel', function () {
        var userstoreConnectorModel = t.createUserstoreConnectorModel();
        testUserstoreConnectorModel(t, userstoreConnectorModel);
    });
});