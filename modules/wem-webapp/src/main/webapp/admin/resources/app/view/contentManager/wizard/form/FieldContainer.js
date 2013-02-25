Ext.define('Admin.view.contentManager.wizard.form.FieldContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.FieldContainer',

    layout: 'hbox',

    label: undefined,

    field: undefined,

    padding: '0 0 10 0',

    listeners: {
        afterrender: function () {
            this.updateAddButtonState();
        }
    },

    initComponent: function () {
        var me = this;
        this.maxFields = this.field.contentTypeItemConfig.occurrences.maximum;
        me.items = [
            this.label,
            {
                xtype: 'panel',
                itemId: 'inputFieldsPanel',
                layout: 'vbox',
                minWidth: 100,
                bodyStyle: {
                    backgroundColor: 'inherit'
                },
                items: [this.field.cloneConfig()],
                dockedItems: [
                    {
                        xtype: 'container',
                        dock: 'bottom',
                        items: [
                            {
                                xtype: 'button',
                                itemId: 'addButton',
                                text: 'Add',
                                listeners: {
                                    click: function () {
                                        var inputFieldsPanel = me.down('#inputFieldsPanel');
                                        var last = inputFieldsPanel.items.last();
                                        if (last) {
                                            last.addCopy();
                                        } else {
                                            inputFieldsPanel.add(me.field.cloneConfig());
                                        }
                                        me.updateAddButtonState();
                                    }
                                }
                            }
                        ]
                    }
                ]
            }
        ];

        me.callParent(arguments);

        var inputFieldsPanel = me.down('#inputFieldsPanel');
        inputFieldsPanel.items.on('add', function () {
            this.updateAddButtonState();
        }, this);
        inputFieldsPanel.items.on('remove', function () {
            this.updateAddButtonState();
        }, this);
    },

    updateAddButtonState: function () {
        var inputFieldsPanel = this.down('#inputFieldsPanel');
        var addButton = this.down('#addButton');
        if (inputFieldsPanel && addButton) {
            var last = inputFieldsPanel.items.last();

            addButton.setDisabled(last && last.copyNo === this.maxFields);
        }
    },

    getValue: function () {
        var value = [];
        var inputFieldsPanel = this.down('#inputFieldsPanel');
        if (inputFieldsPanel) {
            inputFieldsPanel.items.each(function (inputField) {
                value.push(inputField.getValue());
            });
        }
        return value;
    }

});
