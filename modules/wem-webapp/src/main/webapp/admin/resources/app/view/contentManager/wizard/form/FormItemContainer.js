Ext.define('Admin.view.contentManager.wizard.form.FormItemContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.formItemContainer',

    layout: 'column',

    label: undefined,

    field: undefined,

    padding: '0 0 5 0',

    listeners: {
        afterrender: function () {
            this.updateControlsState();
        }
    },

    initComponent: function () {
        this.maxFields = this.field.getConfig().occurrences.maximum;
        this.minFields = this.field.getConfig().occurrences.minimum;

        this.items = [
            this.label,
            {
                xtype: 'panel',
                itemId: 'formItemsPanel',
                cls: 'admin-droppable',
                layout: 'anchor',
                minWidth: 100,
                bodyStyle: {
                    backgroundColor: 'inherit'
                },
                items: [this.field.cloneConfig()],
                dockedItems: [
                    {
                        xtype: 'container',
                        padding: '5px 5px 0 0',
                        dock: 'bottom',
                        items: this.createControls()
                    }
                ]
            }
        ];

        this.callParent(arguments);

        var formItemsPanel = this.down('#formItemsPanel');
        formItemsPanel.items.on('add', this.updateControlsState, this);
        formItemsPanel.items.on('remove', this.updateControlsState, this);
    },

    /**
     * Do nothing must be implemented in child classes.
     * @private
     */
    updateControlsState: function () {

    },

    /**
     * Do nothing, must be implemented in child components.
     * @private
     */
    createControls: function () {

    },


    getValue: function () {
        var value = [];
        var formItemsPanel = this.down('#formItemsPanel');
        if (formItemsPanel) {
            formItemsPanel.items.each(function (formItem) {
                var formItemValue = formItem.getValue();
                if (formItemValue instanceof Array) {
                    value = value.concat(formItemValue);
                } else {
                    value.push(formItemValue);
                }
            });
        }
        return value;
    }

});
