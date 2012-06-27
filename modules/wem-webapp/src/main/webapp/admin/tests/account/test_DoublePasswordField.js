StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.account.DoublePasswordField'
        ],
        function () {
            var dpf = Ext.create('widget.doublePasswordField', {
                renderTo: Ext.getBody()
            });

            t.ok(dpf.calculatePasswordStrength('abc') === 0, 'Password "abc" should be 0:Too short');

            t.ok(dpf.calculatePasswordStrength('qyz829') === 1, 'Password "qyz829" should be 1:Weak');

            t.ok(dpf.calculatePasswordStrength('PoKe1997') === 2, 'Password "PoKe1997" should be 2:Good');

            t.ok(dpf.calculatePasswordStrength('FishAndCheese!') === 3, 'Password "FishAndCheese!" should be 3:Strong');

            t.ok(dpf.calculatePasswordStrength('Battery!#7') === 4, 'Password "Battery!#7" should be 4:Very Strong');

            t.ok(dpf.calculatePasswordStrength('KrypTonitE15++') === 5, 'Password  "KrypTonitE15++" should be 5:Extremely Strong');
        }
    );

});
