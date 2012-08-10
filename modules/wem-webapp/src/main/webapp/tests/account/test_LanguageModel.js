function testLanguageModel(t, model) {
    t.diag('Test LanguageModel');
    t.ok(model.get('key'), 'Key field is present');
    t.ok(model.get('languageCode'), 'LanguageCode field is present');
    t.ok(model.get('description'), 'Description field is present');
    t.ok(model.get('lastModified'), 'LastModified field is present');
}

StartTest(function (t) {
    t.requireOk('Admin.model.account.LanguageModel', function () {
        var languageModel = t.createLanguageModel();
        testLanguageModel(t, languageModel);
    });
})