Ext.define('Admin.view.contentManager.wizard.form.input.Relationship', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Relationship',
    requires: [
        'Admin.store.contentManager.ContentStore'
    ],

    defaultOccurrencesHandling: false,

    initComponent: function () {
        var me = this;

        me.selectedContentStore = me.createSelectedContentStore();

        me.items = [
            me.createHiddenInput(),
            me.createComboBox(),
            me.createViewForSelectedContent()
        ];

        if (me.inputConfig && me.inputConfig.type && me.inputConfig.type.config) {
            var getRelationshipTypeCommand = {
                qualifiedRelationshipTypeName: me.inputConfig.type.config.relationshipType,
                format: 'JSON'
            };
            Admin.lib.RemoteService.relationshipType_get(getRelationshipTypeCommand, function (response) {
                if (response && response.success) {
                    var iconUrl = response.relationshipType.iconUrl;
                    if (me.rendered) {
                        me.el.down('.admin-image-icon').set({'src': iconUrl});
                    } else {
                        me.relationshipTypeIconUrl = iconUrl;
                    }
                }
            });
        }

        me.callParent(arguments);

        this.setValue(this.value);
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

        var fieldTpl = [
            '<div class="{hiddenDataCls}" role="presentation"></div>',
            '<input id="{id}" type="{type}" {inputAttrTpl} class="{fieldCls} {typeCls} {editableCls}" autocomplete="off"',
            '<tpl if="value"> value="{[Ext.util.Format.htmlEncode(values.value)]}"</tpl>',
            '<tpl if="name"> name="{name}"</tpl>',
            '<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
            '<tpl if="size"> size="{size}"</tpl>',
            '<tpl if="maxLength !== undefined"> maxlength="{maxLength}"</tpl>',
            '<tpl if="readOnly"> readonly="readonly"</tpl>',
            '<tpl if="disabled"> disabled="disabled"</tpl>',
            '<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
            '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
            '/>',
            '<img src="{relationshipTypeIconUrl}" class="admin-image-icon"/>',
            '<a href="#" class="admin-library-button">Open Library</a>',
            {compiled: true, disableFormats: true}
        ];

        var listItemTpl = [
            '<tpl for=".">',
            '   <div role="option" class="x-boundlist-item">',
            '       <img src="{iconUrl}?size=48" alt="{displayName}" width="32" height="32"/>',
            '       <div class="info">',
            '           <h6>{displayName}</h6>',
            '           <div style="color: #666">{path}</div>',
            '       </div>',
            '       <div class="x-clear"></div>',
            '   </div>',
            '</tpl>'
        ];

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

            emptyText: 'Start typing',
            fieldSubTpl: fieldTpl,
            tpl: listItemTpl,
            cls: 'admin-embedded-image-combo',
            listConfig: {
                cls: 'admin-embedded-image-list',
                emptyText: 'No matching items'
            },

            displayTpl: Ext.create('Ext.XTemplate',
                '<tpl for=".">',
                '{displayName}',
                '</tpl>'
            ),

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
        if (value && Ext.isString(value)) {
            value = value.split(',');
        } else {
            return [];
        }

        var valueList = [];
        var i;
        for (i = 0; i < value.length; i++) {
            var currentItemValue = {
                'path': this.name.concat('[', i, ']'),
                'value': value[i]
            };
            valueList.push(currentItemValue);
        }
        return valueList;
    },

    setValue: function (values) {
        var me = this;
        var getContentCommand = {
            contentIds: Ext.Array.pluck(values, 'value')
        };
        // retrieve image contents by contentId
        Admin.lib.RemoteService.content_get(getContentCommand, function (getContentResponse) {
            if (getContentResponse && getContentResponse.success) {
                Ext.each(getContentResponse.content, function (contentData) {
                    var contentModel = new Admin.model.contentManager.ContentModel(contentData);
                    me.selectedContentStore.add(contentModel);
                });
            }
        });
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