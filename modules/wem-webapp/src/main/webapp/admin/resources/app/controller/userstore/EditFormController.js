Ext.define('Admin.controller.userstore.EditFormController', {
    extend: 'Admin.controller.userstore.UserstoreController',

    stores: [
        'Admin.store.userstore.UserstoreConfigStore',
        'Admin.store.userstore.UserstoreConnectorStore'
    ],
    models: [
        'Admin.model.userstore.UserstoreConfigModel',
        'Admin.model.userstore.UserstoreConnectorModel'
    ],
    views: [
        'Admin.view.userstore.UserstoreFormPanel'
    ],

    init: function () {
        this.control({
            'userstoreForm button[action=saveUserstore]': {
                click: this.saveUserstore
            },
            'userstoreForm button[action=cancelUserstore]': {
                click: function (button, e, eOpts) {
                    this.application.fireEvent('closeUserstoreTab', button, e, eOpts);
                }
            },
            'userstoreForm #defaultCheckbox': {
                change: this.handleDefaultChange
            },
            'userstoreForm textfield[name=name]': {
                keyup: this.handleUserstoreChange
            },
            'userstoreForm combobox[name=connectorName]': {
                change: this.handleConnectorChange
            }
        });
    },

    handleDefaultChange: function (field, newValue, oldValue, options) {
        if (newValue) {
            Ext.Msg.confirm("Important", "Do you really want to set this userstore default ?", function (button) {
                if ("no" === button) {
                    field.setValue(oldValue);
                }
            });
        }
    },

    handleConnectorChange: function (field, newValue, oldValue, options) {
        var form = field.up('userstoreForm');
        var newVals = form.userstore || { data: {} };
        var record = field.store.findRecord(field.valueField, newValue);
        if (record) {
            newVals.data[field.name] = record.data[field.displayField];
            form.updateUserstoreHeader(newVals);
        }
    },

    handleUserstoreChange: function (field, evt, opts) {
        var form = field.up('userstoreForm');
        var newVals = form.userstore || { data: {} };
        newVals.data[field.name] = field.getValue();
        form.updateUserstoreHeader(newVals);
        var tab = field.up('userstoreFormPanel');
        tab.setTitle(newVals.data.name);
    },

    saveUserstore: function () {
        var form = this.getUserstoreForm().getForm();
        if (form.isValid()) {
            this.remoteCreateOrUpdateUserstore(form.getValues());
        }
    },

    getUserstoreForm: function () {
        return Ext.ComponentQuery.query('userstoreForm')[0];
    }

});
