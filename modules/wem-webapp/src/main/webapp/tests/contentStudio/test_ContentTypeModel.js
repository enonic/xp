function testContentTypeModel(t, model) {
    t.diag('Test ContentTypeModel');
    t.ok(model.get('key'), 'Key field is present');
    t.ok(model.get('type'), 'Type field is present');
    t.ok(model.get('extends'), 'Extends field is present');
    t.ok(model.get('created'), 'Created field is present');
    t.ok(model.get('lastModified'), 'LastModified field is present');
    t.ok(model.get('name'), 'Name field is present');
    t.ok(model.get('displayName'), 'DisplayName field is present');
    t.ok(model.get('module'), 'Module field is present');
    t.ok(model.get('configXml'), 'ConfigXML field is present');
    t.ok(model.get('usageCount'), 'UsageCount field is present');
    t.ok(model.get('icon'), 'Icon field is present');
}

StartTest(function (t) {
    t.requireOk('Admin.model.contentStudio.ContentTypeModel', function () {
        var contentTypeModel = t.createContentTypeModel();
        testContentTypeModel(t, contentTypeModel);
    });
});