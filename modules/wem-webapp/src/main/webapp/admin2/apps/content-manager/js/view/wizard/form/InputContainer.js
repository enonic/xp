Ext.define('Admin.view.contentManager.wizard.form.InputContainer', {
    extend: 'Admin.view.contentManager.wizard.form.FormItemContainer',
    alias: 'widget.inputContainer',

    updateControlsState: function () {
        var formItemsPanel = this.down('#formItemsPanel');
        var addButton = this.down('#addButton');
        if (formItemsPanel && addButton) {
            var last = formItemsPanel.items.last();

            addButton.setDisabled(last && last.copyNo === this.maxFields);
        }
    },

    createControls: function () {
        var me = this;
        if ((this.maxFields > 1 && this.minFields !== this.maxFields) || (this.maxFields === 0)) {
            return [
                {
                    xtype: 'button',
                    itemId: 'addButton',
                    ui: 'dark-grey',
                    text: 'Add',
                    listeners: {
                        click: function () {
                            var formItemsPanel = me.down('#formItemsPanel');
                            var last = formItemsPanel.items.last();
                            if (last) {
                                last.addCopy();
                            } else {
                                formItemsPanel.add(me.field.cloneConfig());
                            }
                            me.updateControlsState();
                        }
                    }
                }
            ];
        } else {
            return [];
        }
    }

});
