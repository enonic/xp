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

function testTextField(t) {
    t.diag('Test plain text field');
    var uff = createUserFormField('firstName', 'text', 'First Name', 'John');
    t.ok(uff.down('textfield'), 'Plain text field is present');
    t.is(uff.getValue(), 'John', 'Value initialization was correct');
    t.is(uff.getFieldLabel(), 'First Name', 'Field label set correct');
    Ext.destroy(uff);
}

function testDateField(t) {
    t.diag('Test date field');
    var uff = createUserFormField('birthday', 'date', 'Birthday', '2012-07-17');
    t.ok(uff.down('datefield'), 'Plain date field is present');
    t.is(new Date(uff.getValue()).toDateString(), new Date('2012-07-17').toDateString(), 'Value initialization was correct');
    t.is(uff.getFieldLabel(), 'Birthday', 'Field label set correct');
    Ext.destroy(uff);
}

function testFileField(t) {
    t.diag('Test file field');
    var uff = createUserFormField('photo', 'file', 'Photo', '/path/to/file');
    t.ok(uff.down('filefield'), 'File field is rpesent');
    t.is(uff.getFieldLabel(), 'Photo', 'Field label set correct');
    Ext.destroy(uff);
}

function testComboBoxField(t) {
    var userstoreStore = Ext.create('Test.account.store.UserstoreConfigStore', {});
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
    });
}

function testAutocompleteField(t) {
    var userstoreStore = Ext.create('Test.account.store.UserstoreConfigStore', {});
    t.loadStoresAndThen(userstoreStore, function () {
        t.diag('Test autocomplete field');
        var uff = createUserFormField('userstores', 'autocomplete', 'Userstores', undefined, {
            fieldStore: userstoreStore,
            valueField: 'key',
            displayField: 'name'
        });
        t.ok(uff.down('combobox'), 'Combobox field is present');
        t.is(uff.getFieldLabel(), 'Userstores', 'Field label set correct');
        t.chain(
            {
                action: 'type',
                target: uff.down('combobox'),
                text: 'test'
            },
            {
                action: 'type',
                target: uff.down('combobox'),
                text: '[ENTER]'
            },
            function () {
                t.is(uff.getValue(), 2, 'Autocomplete was correct');
                Ext.destroy(uff);
            }
        );
    });
}

function testBooleanField(t) {
    t.diag('Test boolean field');
    var uff = createUserFormField('local', 'boolean', 'Local', true);
    t.ok(uff.down('checkbox'), 'Checkbox field is present');
    t.is(uff.getFieldLabel(), 'Local', 'Field label set correct');
    t.is(uff.getValue(), true, 'Initial value was set correct');
    t.click(uff.down('checkbox'), function () {
        t.is(uff.getValue(), false, 'Field was unchecked, value is correct');
        Ext.destroy(uff);
    });
}

function testUserFormFieldValidation(t) {
    t.diag('Test simple validation');
    var uff = createUserFormField('name', 'text', 'Name', undefined, {
        required: true
    });
    t.is(uff.isValid(), false, 'Field is not valid. Validation is correct');
    t.type(uff.down('textfield'), 'John', function () {
        t.is(uff.isValid(), true, 'Field is valid. Validation is correct');
        Ext.destroy(uff);
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
            testTextField(t);
            testDateField(t);
            testFileField(t);
            testComboBoxField(t);
            testAutocompleteField(t);
            testBooleanField(t);
            testUserFormFieldValidation(t);
        }
    );

});
