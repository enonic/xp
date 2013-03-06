Ext.define('Admin.view.WizardHeader', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardHeader',

    //TODO: rename to admin-wizard-header when no old headers left
    cls: 'admin-wizard-header-container',

    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    border: false,

    displayNameProperty: 'displayName',
    displayNameConfig: {
        allowBlank: false,
        emptyText: 'Display Name'
    },

    pathProperty: 'path',
    pathConfig: {
        hidden: false,
        emptyText: 'path/to/'
    },

    nameProperty: 'name',

    nameConfig: {
        hidden: false,
        allowBlank: false,
        emptyText: 'Name'
    },

    statics: {

        appendNameVtype: function () {
            Ext.apply(Ext.form.field.VTypes, {
                //  vtype validation function
                name: function (val, field) {
                    return /^[a-z0-9\-]+$/i.test(val);
                },
                // vtype Text property: The error text to display when the validation function returns false
                nameText: 'Not a valid name. Can contain digits, characters and "-" only.',
                // vtype Mask property: The keystroke filter mask
                nameMask: /^[a-z0-9\-]+$/i
            });
        }()

    },

    initComponent: function () {
        var me = this;

        var headerData = this.prepareHeaderData(this.data);

        me.autogenerateName = Ext.isEmpty(headerData.name);

        var displayNameField = Ext.apply({
            xtype: 'textfield',
            itemId: 'displayName',
            name: this.displayNameProperty,
            value: headerData[this.displayNameProperty],
            enableKeyEvents: true,
            hideLabel: true,
            cls: 'admin-display-name',
            dirtyCls: 'admin-display-name-dirty',
            listeners: {
                change: function (field, newVal, oldVal, opts) {
                    if (me.fireEvent('displaynamechange', newVal, oldVal) !== false) {
                        if (me.autogenerateName) {
                            var processedValue = nameField.processRawValue(newVal);
                            nameField.setValue(processedValue);
                        }
                    }
                },
                afterrender: function (field) {
                    field.getFocusEl().focus(100);
                }
            }
        }, me.displayNameConfig);

        var pathField = Ext.apply({
            xtype: 'displayfield',
            cls: 'admin-path',
            dirtyCls: 'admin-path-dirty',
            value: headerData[this.pathProperty]
        }, me.pathConfig);

        var nameField = Ext.create('Ext.form.field.Text', Ext.apply({
            xtype: 'textfield',
            flex: 1,
            cls: 'admin-name',
            dirtyCls: 'admin-name-dirty',
            name: this.nameProperty,
            enableKeyEvents: true,
            value: headerData[this.nameProperty],
            vtype: 'name',
            stripCharsRe: /[^a-z0-9\-]+/ig,
            listeners: {
                change: function (field, newVal, oldVal, opts) {
                    me.fireEvent('namechange', newVal, oldVal);
                },
                keyup: function (field, event, opts) {
                    me.autogenerateName = Ext.isEmpty(field.getValue());
                }
            }
        }, me.nameConfig));


        this.items = [
            displayNameField
        ];

        if (!pathField.hidden && !nameField.hidden) {
            this.items.push({
                xtype: 'fieldcontainer',
                hideLabel: true,
                layout: 'hbox',
                items: [
                    pathField,
                    nameField
                ]
            });
        } else if (!pathField.hidden) {
            this.items.push(pathField);
        } else if (!nameField.hidden) {
            this.items.push(nameField);
        }

        this.callParent(arguments);
        this.addEvents('displaynamechange', 'namechange');
    },

    prepareHeaderData: function (data) {
        return data && data.data || {};
    },

    setData: function (data) {
        this.data = data;
        this.getForm().setValues(this.resolveHeaderData(data));
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }

});