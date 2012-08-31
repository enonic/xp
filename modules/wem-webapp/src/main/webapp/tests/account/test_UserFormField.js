function createUserFormField(fieldName, type, label, value, config) {
    var initialConfig = {
        fieldname: fieldName,
        fieldValue: value,
        fieldLabel: label,
        type: type,
        renderTo: Ext.getBody()
    };
    if (config) {
        initialConfig = Ext.apply(initialConfig, config);
    }
    return Ext.create('Admin.view.account.UserFormField', initialConfig);
}

function testTextField(t, callback) {
    t.diag('Test plain text field');
    var uff = createUserFormField('firstName', 'text', 'First Name', 'John');
    t.ok(uff.down('textfield'), 'Plain text field is present');
    t.is(uff.getValue(), 'John', 'Value initialization was correct');
    t.is(uff.getFieldLabel(), 'First Name', 'Field label set correct');
    Ext.destroy(uff);
    callback();
}

function testDateField(t, callback) {
    t.diag('Test date field');
    var uff = createUserFormField('birthday', 'date', 'Birthday', '2012-07-17');
    t.ok(uff.down('datefield'), 'Plain date field is present');
    t.is(new Date(uff.getValue()).toDateString(), new Date('2012-07-17').toDateString(), 'Value initialization was correct');
    t.is(uff.getFieldLabel(), 'Birthday', 'Field label set correct');
    Ext.destroy(uff);
    callback();
}

function testFileField(t, callback) {
    t.diag('Test file field');
    var uff = createUserFormField('photo', 'file', 'Photo', '/path/to/file');
    t.ok(uff.down('filefield'), 'File field is rpesent');
    t.is(uff.getFieldLabel(), 'Photo', 'Field label set correct');
    Ext.destroy(uff);
    callback();
}

function testComboBoxField(t, callback) {
    var userstoreStore = Ext.create('Test.account.store.UserstoreConfigStore');
    t.loadStoresAndThen(userstoreStore, function () {
        t.diag('Test combobox field');
        var uff = createUserFormField('userstores', 'combo', 'Userstores', '2', {
            fieldStore: userstoreStore,
            valueField: 'key',
            displayField: 'name'
        });
        t.ok(uff.down('combobox'), 'Combobox field is present');
        t.is(uff.getFieldLabel(), 'Userstores', 'Field label set correct');
        t.is(uff.getValue(), '2', 'Initial value was set correct');
        uff.down('combobox').expand();
        t.click(Ext.getBody().query('.x-boundlist-item')[0]);
        t.is(uff.getValue(), '1', 'Userstore with key = 1 was selected');
        Ext.destroy(uff);
        callback();
    });
}

function testAutocompleteField(t, callback) {
    var userstoreStore = Ext.create('Test.account.store.UserstoreConfigStore');
    t.loadStoresAndThen(userstoreStore, function () {
        t.diag('Test autocomplete field');
        var uff = createUserFormField('userstores', 'autocomplete', 'Userstores', undefined, {
            fieldStore: userstoreStore,
            valueField: 'key',
            displayField: 'name'
        });
        var combo = uff.down('combobox');
        t.ok(combo, 'Combobox field is present');
        t.is(uff.getFieldLabel(), 'Userstores', 'Field label set correct');
        t.chain(
            {
                action: 'type',
                target: combo,
                text: 'test'
            },
            function (next) {
                var picker = combo.getPicker();
                t.click(picker, next);
            },
            function (next) {
                t.is(combo.getValue(), 2, 'Autocomplete was correct');
                Ext.destroy(combo, uff);
                callback();
            }
        );
    });
}

function testBooleanField(t, callback) {
    t.diag('Test boolean field');
    var uff = createUserFormField('local', 'boolean', 'Local', true);
    t.ok(uff.down('checkbox'), 'Checkbox field is present');
    t.is(uff.getFieldLabel(), 'Local', 'Field label set correct');
    t.is(uff.getValue(), true, 'Initial value was set correct');
    t.click(uff.down('checkbox'), function () {
        t.is(uff.getValue(), false, 'Field was unchecked, value is correct');
        Ext.destroy(uff);
        callback();
    });
}

function testUserFormFieldValidation(t, callback) {
    t.diag('Test simple validation');
    var uff = createUserFormField('name', 'text', 'Name', undefined, {
        required: true
    });
    t.is(uff.isValid(), false, 'Field is not valid. Validation is correct');
    t.type(uff.down('textfield').getActionEl(), 'John', function () {
        t.is(uff.isValid(), true, 'Field is valid. Validation is correct');
        Ext.destroy(uff);
        callback();
    });
    //TODO: remote validation tests should be written too
}

StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.account.UserFormField',
            'Test.account.store.UserstoreConfigStore'
        ],
        function () {
            t.chain(
                function (next) {
                    testTextField(t, next);
                },
                function (next) {
                    testDateField(t, next);
                },
                function (next) {
                    testFileField(t, next);
                },
                function (next) {
                    testComboBoxField(t, next);
                },
                function (next) {
                    testAutocompleteField(t, next);
                },
                function (next) {
                    testBooleanField(t, next);
                },
                function (next) {
                    testUserFormFieldValidation(t, next);
                }
            );
        }
    );

});
