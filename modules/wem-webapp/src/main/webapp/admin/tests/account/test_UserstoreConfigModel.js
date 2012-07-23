function testUserStoreConfigModel(t, model) {
    t.diag('Test basic fields for existence');
    console.log(model.getData(false));
    t.ok(model.get('key'), 'Key field is present');
    t.ok(model.get('name'), 'Name field is present');
    t.ok(model.get('defaultStore'), 'defaultStore field is present');
    t.ok(model.get('connectorName'), 'connectorName field is present');
    t.ok(model.get('configXML'), 'configXML field is present');
    t.ok(model.get('lastModified'), 'lastModified field is present');
}

function testUserStoreConfigModelUserFieldAssociation(t, model) {
    t.diag('Test user field association');
    t.ok(model.userFields, 'userFields function is present: associations was properly set');
    var userFields = model.userFields().getRange();
    t.is(userFields.length, 2, 'There should be 2 fields');
}

StartTest(function (t) {
    t.requireOk(
        [
            'Test.account.store.UserstoreConfigStore',
            'Admin.model.account.UserstoreConfigModel',
            'Admin.model.account.UserFieldModel'
        ],
        function () {
            var store = Ext.create('Test.account.store.UserstoreConfigStore', {});
            t.loadStoresAndThen(store, function () {
                t.ok(store.getRange(), 'There are userstores in store');
                var defStore = store.findRecord('name', 'default');
                t.ok(defStore, 'Default userstore is present');
                testUserStoreConfigModel(t, defStore);
                testUserStoreConfigModelUserFieldAssociation(t, defStore);
            });
        }
    );
});