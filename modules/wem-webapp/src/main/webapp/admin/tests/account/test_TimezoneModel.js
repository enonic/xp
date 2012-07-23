function testTimezoneModel(t, model) {
    t.diag('Test TimezoneModel');
    t.ok(model.get('id'), 'Id field is present');
    t.ok(model.get('humanizedId'), 'HumanizedId field is present');
    t.ok(model.get('shortName'), 'ShortName field is present');
    t.ok(model.get('name'), 'Name field is present');
    t.ok(model.get('offset'), 'Offset field is present');
    t.ok(model.get('humanizedIdAndOffset'), 'HumanizedIdAndOffset dynamic field is present');
}

StartTest(function (t) {
    t.requireOk('Admin.model.account.TimezoneModel', function () {
        var timezoneModel = t.createTimezoneModel();
        testTimezoneModel(t, timezoneModel);
    });
})