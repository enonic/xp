function testLocaleModel(t, model) {
    t.diag('Test LocaleModel');
    t.ok(model.get('id'), 'Id field is present');
    t.ok(model.get('displayName'), 'DisplayName field is present');
}

StartTest(function (t) {
    t.requireOk('Admin.model.account.LocaleModel', function () {
        var localeModel = t.createLocaleModel();
        testLocaleModel(t, localeModel);
    });
});