StartTest(function (t) {

    t.requireOk('Admin.model.account.AccountModel', function () {

        var model = t.createUserModel();

        t.is(model.get('displayName'), 'J\u00F6rgen Sivesind', 'Found display name, utf chars ok');
        t.is(model.get('email'), 'jsi@enonic.com', 'Could read email');
    });
});
