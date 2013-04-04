Ext.define('Admin.view.contentManager.wizard.form.input.Relationship', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Relationship',
    requires: [
        'Admin.store.contentManager.ContentStore'
    ],

    defaultOccurrencesHandling: false,

    initComponent: function () {

        this.selectedContentStore = this.createSelectedContentStore();
        this.items = [
            this.createHiddenInput(),
            this.createComboBox(),
            this.createViewForSelectedContent()
        ];

        this.callParent(arguments);
    },


    //getValue: function () {
    //return this.getComponent(this.name).getValue();
    //},


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
            queryParam: 'fulltext',
            autoSelect: false,

            displayField: 'displayName',
            valueField: 'id',

            // Hardcode the store for now.
            store: new Admin.store.contentManager.ContentStore(),
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
    onSelectContent: function (contentModels) {
        var isAlreadyAdded = this.selectedContentStore.findRecord('key', contentModels[0].raw.key);
        if (isAlreadyAdded) {
            this.alertContentIsAdded(contentModels);
            return;
        }
        this.selectedContentStore.add(contentModels[0].raw);
    },


    getValue: function () {
        var value = this.items.items[0].getValue();
        if (Ext.isArray(value)) {
            value = value.join(',');
        }
        return {
            path: this.name.concat('[', this.copyNo - 1, ']'),
            value: value
        };
    },
    /**
     * @private
     */
    createSelectedContentStore: function () {
        var me = this;

        return Ext.create('Ext.data.Store', {
            model: 'Admin.model.contentManager.ContentModel',
            data: [],
            listeners: {
                datachanged: function (store) {
                    me.updateHiddenValue();
                    try {
                        me.down('combobox').setDisabled(me.selectedContentStore.getCount() ===
                                                        me.contentTypeItemConfig.occurrences.maximum);
                    }
                    catch (exception) {
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
            '       <img src="{iconUrl}" alt="{displayName}" width="32" height="32"/>',
            '       <span class="center-column">',
            '           {displayName}',
            '           <span style="color: #666">{path}</span>',
            '       </span>',
            '       <span class="right-column"><a href="javascript:;" class="remove-related-item-button"></a></span>',
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
                itemclick: function (view, contentModel, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    if (clickedElement.hasCls('remove-related-item-button')) {
                        me.selectedContentStore.remove(contentModel);
                    }
                }
            }
        });
    },


    /**
     * @private
     */
    alertContentIsAdded: function (records) {
        alert('Temporary alert! Can not have duplicates in Relationship input\n"' + records[0].raw.title + '" has already been added');
        this.down('combobox').focus('');
    },


    /**
     * @private
     */
    updateHiddenValue: function () {
        var me = this;
        var keys = [];
        if (this.items) {
            Ext.Array.each(me.selectedContentStore.getRange(), function (item) {
                keys.push(item.get('id'));
            });
            this.getComponent(this.name).setValue(keys);
        }
    }

});