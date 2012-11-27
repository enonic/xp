Ext.define('Admin.lib.inputtypes.Relation', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.input.Relation',
    fieldLabel: 'Relation',
    width: 580,
    initComponent: function () {
        var me = this;

        me.listViewStore = me.createListViewStore();

        me.items = [
            me.createHiddenInput(),
            me.createComboBox(),
            me.createItemListView()
        ];

        me.callParent(arguments);
    },

    createHiddenInput: function () {
        var me = this;
        return {
            xtype: 'hiddenfield',
            name: me.name,
            itemId: me.name, // TODO: Is this unique enough?
            value: '' // TODO: Populate
        };
    },

    createComboBox: function () {
        var me = this;

        var combo = {
            xtype: 'combo',
            submitValue: false,
            hideTrigger: true,
            forceSelection: true,
            width: 350,
            minChars: 1,
            queryMode: 'remote',
            autoSelect: false,

            displayField: 'title',
            valueField: 'key',

            // Hardcode the store for now.
            store: Ext.create('Ext.data.Store', {
                fields: ['key', 'title'],
                proxy: {
                    type: 'ajax',
                    url: 'related-content.json',
                    reader: {
                        type: 'json',
                        root: 'content'
                    }
                }
            }),
            listeners: {
                select: function (combo, records) {
                    me.listViewStore.add(records[0].raw);
                    combo.setValue('');
                }
            }
        };

        return combo;
    },

    createListViewStore: function () {
        var me = this;

        return Ext.create('Ext.data.Store', {
            fields: ['key', 'icon', 'title', 'path'],
            data: [],
            listeners: {
                datachanged: function (store) {
                    me.updateValue();
                }
            }
        });
    },

    createItemListView: function () {
        var me = this;

        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-related-item">',
            '       <img src="{icon}" alt="{title}"/>',
            '       <div class="center-column">',
            '           <h6>{title}</h6>',
            '           <div style="color: #666">{path}</div>',
            '       </div>',
            '       <div class="right-column"><a href="javascript:;" class="remove-related-item-button">Remove</a></div>',
            '   </div>',
            '</tpl>'
        );

        return Ext.create('Ext.view.View', {
            store: me.listViewStore,
            tpl: template,
            itemSelector: 'div.admin-related-item',
            emptyText: 'No items selected',
            deferEmptyText: false,
            listeners: {
                itemclick: function (view, record, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    if (clickedElement.hasCls('remove-related-item-button')) {
                        me.listViewStore.remove(record);
                    }
                }
            }
        });
    },

    updateValue: function () {
        var me = this;
        var keys = [];
        if (me.items) {
            Ext.Array.each(me.listViewStore.data.items, function (item) {
                keys.push(item.data.key);
            });
            me.getComponent(me.name).setValue(keys);
        }
    }

});