StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.account.ChangePasswordWindow'
        ],
        function () {
            var model = t.createUserModel();

            var win = Ext.create('widget.userChangePasswordWindow');
            win.doShow(model);

            var passwordInput = win.down('#passwordInput');
            var repeatInput = win.down('#repeatInput');
            var passwordStatus = win.down('#passwordStatus');
            var changePasswordButton = win.down('#changePasswordButton');

            t.type(passwordInput, 'fisk', function () {
            });
            t.type(repeatInput, 'fask', function () {
                t.ok(changePasswordButton.isDisabled(), 'Confirm button should be disabled');
                repeatInput.setValue('');

                t.type(repeatInput, 'fisk', function () {
                    t.ok(!changePasswordButton.isDisabled(), 'Confirm button should be enabled');
                });
            });
        }
    );

});
