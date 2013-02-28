Ext.define('Admin.view.contentManager.wizard.form.FormItemSetContainer', {
    extend: 'Admin.view.contentManager.wizard.form.FormItemContainer',
    alias: 'widget.formItemSetContainer',

    updateControlsState: function () {
        var formItemsPanel = this.down('#formItemsPanel');
        var addButton = this.down('#addButton');
        var collapseButton = this.down('#collapseButton');
        if (formItemsPanel) {
            var last = formItemsPanel.items.last();
            if (addButton) {
                addButton.setDisabled(last && last.copyNo === this.maxFields);
            }
            if (collapseButton) {
                collapseButton.setVisible(formItemsPanel.items.getCount() > 0);
            }
        }
    },

    createControls: function () {
        var me = this;
        var addButton = {
            xtype: 'button',
            itemId: 'addButton',
            style: {
                float: 'left'
            },
            text: 'Add ' + this.field.formItemSetConfig.label,
            handler: function () {
                var formItemsPanel = me.down('#formItemsPanel');
                var last = formItemsPanel.items.last();
                if (last) {
                    last.addCopy();
                } else {
                    formItemsPanel.add(me.field.cloneConfig());
                }
                me.updateControlsState();
            }
        };
        var collapseButton = {
            xtype: 'component',
            itemId: 'collapseButton',
            renderSelectors: {
                linkEl: 'span'
            },
            style: {
                float: 'right'
            },
            listeners: {
                click: {
                    element: 'linkEl',
                    fn: function () {
                        var formItemsPanel = me.down('#formItemsPanel');
                        formItemsPanel.items.each(function (item) {
                            item.setCollapsed(me.isCollapsed);
                        });
                        me.isCollapsed = !me.isCollapsed;
                        this.setHTML(me.isCollapsed ? 'Expand All' : 'Collapse All');

                    }
                }
            },
            html: '<span class="admin-text-button admin-collapse-all-button" href="javascript:;">Collapse All</span>'
        };
        if ((this.maxFields > 1 && this.minFields !== this.maxFields) || (this.maxFields === 0)) {
            return [addButton, collapseButton];
        } else {
            return [collapseButton];
        }
    }

});
