Ext.define('Admin.view.contentManager.wizard.form.input.Relation', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Relation',
    fieldLabel: 'Relation',

    initComponent: function () {

        this.selectedContentStore = this.createSelectedContentStore();

        this.items = [
            this.createHiddenInput(),
            this.createComboBox(),
            this.createViewForSelectedContent()
        ];

        this.callParent(arguments);
    },


    getValue: function () {
        return this.getComponent(this.name).getValue();
    },


    /**
     * @private
     */
    createHiddenInput: function () {
        return {
            xtype: 'hiddenfield',
            name: this.name,
            itemId: this.name, // TODO: Is this unique enough?
            value: ''
        };
    },


    /**
     * @private
     */
    createComboBox: function () {
        var me = this;

        var combo = {
            xtype: 'combo',
            name: '_system_relation_combo',
            submitValue: false,
            hideTrigger: true,
            forceSelection: true,
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
                    combo.setValue('');
                    me.onSelectContent(records);
                }
            }
        };

        return combo;
    },


    /**
     * @private
     */
    onSelectContent: function (records) {
        var isAlreadyAdded = this.selectedContentStore.findRecord('key', records[0].raw.key);
        if (isAlreadyAdded) {
            this.alertContentIsAdded(records);
            return;
        }
        this.selectedContentStore.add(records[0].raw);
    },


    /**
     * @private
     */
    createSelectedContentStore: function () {
        var me = this;

        return Ext.create('Ext.data.Store', {
            fields: ['key', 'icon', 'title', 'path'],
            data: [],
            listeners: {
                datachanged: function (store) {
                    me.updateHiddenValue();
                    try {
                        me.down('combobox').setDisabled(me.selectedContentStore.getCount() ===
                                                        me.contentTypeItemConfig.occurrences.maximum);
                    } catch (exception) {
                        /**/
                    }
                }
            }
        });
    },


    /**
     * @private
     */
    createViewForSelectedContent: function () {
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
            store: me.selectedContentStore,
            tpl: template,
            itemSelector: 'div.admin-related-item',
            emptyText: 'No items selected',
            deferEmptyText: false,
            listeners: {
                itemclick: function (view, record, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    if (clickedElement.hasCls('remove-related-item-button')) {
                        me.selectedContentStore.remove(record);
                    }
                }
            }
        });
    },


    /**
     * @private
     */
    alertContentIsAdded: function (records) {
        alert('Temporary alert! Can not have duplicates in Relation field\n"' + records[0].raw.title + '" has already been added');
        this.down('combobox').focus('');
    },


    /**
     * @private
     */
    updateHiddenValue: function () {
        var keys = [];
        if (this.items) {
            Ext.Array.each(me.selectedContentStore.data.items, function (item) {
                keys.push(item.data.key);
            });
            this.getComponent(this.name).setValue(keys);
        }
    }

});