StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.BaseDialogWindow'
        ],
        function () {

            var window, header, info;

            t.chain(
                function (next) {

                    t.diag('Test with no params');

                    window = Ext.create('widget.baseDialogWindow');
                    window.doShow();

                    t.ok(window.isVisible(), 'Dialog window should have been shown');

                    header = window.child('#dialogHeader');
                    t.is(header.getEl().dom.innerHTML, '<h3>Base dialog</h3>', 'Dialog header should have been Base dialog by default');

                    window.setDialogHeader('<h2>Test dialog</h2>');
                    t.is(header.getEl().dom.innerHTML, '<h2>Test dialog</h2>', 'Dialog header should have been changed to Test dialog');

                    info = window.child('#dialogInfo');
                    t.notOk(info.getEl().dom.innerHTML, 'Dialog info should have been blank by default');

                    window.setDialogInfoTpl('<p>{property}: {value}</p>');
                    window.setDialogInfoData({
                        data: {
                            property: 'foo',
                            value: 'bar'
                        }
                    });
                    t.is(info.getEl().dom.innerHTML, '<p>foo: bar</p>', 'Dialog info should have been changed to foo: bar');

                    var closeButton = window.down('#closeButton');
                    t.ok(closeButton, 'Close button present');

                    t.click(closeButton, function () {
                        t.notOk(window.isVisible(), 'Dialog window should have been closed');
                        Ext.destroy(window);
                        next();
                    });
                },
                function (next) {

                    t.diag('Test with custom params');

                    window = Ext.create('widget.baseDialogWindow', {
                        dialogTitle: 'Custom title',
                        dialogInfoTpl: '<p>Custom content by {author}</p>'
                    });
                    window.doShow({
                        data: {
                            author: 'Siesta'
                        }
                    });
                    t.ok(window.isVisible(), 'Dialog window should have been shown');

                    var header = window.child('#dialogHeader');
                    t.is(header.getEl().dom.innerHTML, '<h3>Custom title</h3>', 'Dialog header should have been Custom title');

                    info = window.child('#dialogInfo');
                    t.is(info.getEl().dom.innerHTML, '<p>Custom content by Siesta</p>',
                        'Dialog info should have been Custom content by Siesta');

                    window.close();
                    t.notOk(window.isVisible(), 'Dialog window should have been closed');

                    t.done();
                }
            );
        }
    );
});