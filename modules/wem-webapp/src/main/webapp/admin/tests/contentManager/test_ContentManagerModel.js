function testContentManagerModel(t, model) {
    t.diag('Test ContentManagerModel');
    t.ok(model.get('key'), 'Key field is present');
    t.ok(model.get('name'), 'Name field is present');
    t.ok(model.get('type'), 'Type field is present');
    t.ok(model.get('owner'), 'Owner field is present');
    t.ok(model.get('url'), 'URL field is present');
    t.ok(model.get('lastModified'), 'LastModified field is present');
}

StartTest(function (t) {
    t.requireOk('Admin.model.contentManager.ContentModel', function () {
        var contentManagerModel = t.createContentManagerModel();
        testContentManagerModel(t, contentManagerModel);
    });
});