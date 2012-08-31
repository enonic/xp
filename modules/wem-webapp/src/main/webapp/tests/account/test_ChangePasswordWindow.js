StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.account.ChangePasswordWindow',
            'Admin.view.account.DoublePasswordField'
        ],
        function () {
            var model = t.createUserModel();

            var win = Ext.create('widget.userChangePasswordWindow');

            t.waitForComponentVisible(win, function () {
                var passwordInput = win.down('#passwordInput');
                var repeatInput = win.down('#repeatInput');
                var passwordStatus = win.down('#passwordStatus');
                var changePasswordButton = win.down('#changePasswordButton');

                t.chain(function (next) {
                    t.ok(changePasswordButton.isDisabled(), 'Fields are empty, confirm button should be disabled');
                    passwordInput.setValue('fisk');
                    next();
                }, function (next) {
                    t.ok(changePasswordButton.isDisabled(), 'Repeat is missing, confirm button should be disabled');
                    repeatInput.setValue('fask');
                    next();
                }, function (next) {
                    t.ok(changePasswordButton.isDisabled(), 'Repeat doesn\'t match, confirm button should be disabled');
                    repeatInput.setValue('fisk');
                    next();
                }, function (next) {
                    t.ok(!changePasswordButton.isDisabled(), 'Passwords match, confirm button should be enabled');
                });
            });

            win.doShow(model);
        }
    );

});
