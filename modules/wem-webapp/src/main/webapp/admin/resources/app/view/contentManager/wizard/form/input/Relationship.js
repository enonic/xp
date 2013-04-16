Ext.define('Admin.view.contentManager.wizard.form.input.Relationship', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Relationship',
    requires: [
        'Admin.store.contentManager.ContentStore'
    ],

    defaultOccurrencesHandling: false,
    contentStore: null,

    initComponent: function () {
        var me = this;

        me.selectedContentStore = me.createSelectedContentStore();

        me.items = [
            me.createHiddenInput(),
            me.createComboBox(),
            me.createOpenLibraryButton(),
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
            {compiled: true, disableFormats: true}
        ];

        var listItemTpl = [
            '<tpl for=".">',
            '   <div role="option" class="x-boundlist-item {grayedOutComboItem}">',
            '       <img src="{iconUrl}?size=32" alt="{displayName}" width="32" height="32"/>',
            '       <div class="info">',
            '           <h6>{displayName}</h6>',
            '           <div style="color: #666">{path}</div>',
            '       </div>',
            '       <div class="x-clear"></div>',
            '   </div>',
            '</tpl>'
        ];

        me.contentStore = new Admin.store.contentManager.ContentStore({
            filters: [
                function (content) {
                    return !me.selectedContentStore.findRecord('id', content.get('id'));
                }
            ]
        });

        var relationshipTypeName = me.inputConfig.type.config.relationshipType;
        me.remoteGetRelationshipType(relationshipTypeName, function (relationshipType) {
            var allowedContentTypes = relationshipType.allowedToTypes;
            if (!Ext.isEmpty(allowedContentTypes)) {
                me.contentStore.proxy.extraParams = {
                    'contentTypes': allowedContentTypes
                }
            }
        });

        var combo = {
            xtype: 'combo',
            itemId: 'relationshipCombo',
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

            width: 468,
            fieldCls: 'admin-relationship-input',
            emptyText: 'Start typing',
            fieldSubTpl: fieldTpl,
            tpl: listItemTpl,
            cls: 'admin-relationship-combo',
            listConfig: {
                cls: 'admin-relationship-list',
                emptyText: 'No matching items'
            },

            displayTpl: Ext.create('Ext.XTemplate',
                '<tpl for=".">',
                '{displayName}',
                '</tpl>'
            ),

            store: me.contentStore,
            listeners: {
                select: function (combo, records) {
                    combo.setValue('');
                    me.onSelectContent(records);
                },
                beforeselect: function (combo, record, index) {
                    return record.data['grayedOutComboItem'];
                }
            }
        };

        return combo;
    },


    /**
     * @private
     */
    onSelectContent: function (contentModels) {
        var contentModel = contentModels[0];
        var isAlreadyAdded = this.selectedContentStore.findRecord('id', contentModel.get('id'));
        if (isAlreadyAdded) {
            this.alertContentIsAdded(contentModel);
            return;
        }
        this.selectedContentStore.add(contentModel);
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
        var max = this.inputConfig.occurrences.maximum;
        var min = this.inputConfig.occurrences.minimum;

        return Ext.create('Ext.data.Store', {
            model: 'Admin.model.contentManager.ContentModel',
            data: [],
            listeners: {
                datachanged: function (store) {
                    me.updateHiddenValue();
                    if (me.contentStore) {
                        me.contentStore.clearFilter(true);
                        me.contentStore.filter({
                            filterFn: function (content) {
                                var existing = me.selectedContentStore.findRecord('id', content.get('id'));
                                if (existing) {
                                    content.set('grayedOutComboItem', 'admin-relationship-combo-grayed-out-item');
                                } else {
                                    content.set('grayedOutComboItem', '');
                                }
                                return true;
                            }
                        });
                    }
                    try {
                        if (max > 0) {
                            me.down('#relationshipCombo').setDisabled(store.getCount() === max);
                        }
                    }
                    catch (exception) {
                        //
                    }
                }
            }
        });
    },


    /**
     * @private
     */
    createOpenLibraryButton: function () {
        var me = this;
        return {
            xtype: 'button',
            itemId: 'openLibraryButton',
            tooltip: 'Open Library',
            iconCls: 'admin-relationship-library-icon',
            cls: 'nobg icon-button',
            scale: 'medium',
            width: '24',
            margin: '6',
            handler: function () {
                me.onLibraryButtonClicked();
            }
        };
    },


    /**
     * @private
     */
    createViewForSelectedContent: function () {
        var me = this;
        var min = this.inputConfig.occurrences.minimum;
        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-related-item">',
            '       <img src="{iconUrl}" alt="{displayName}" width="32" height="32"/>',
            '       <span class="center-column">',
            '           {displayName}',
            '           <p style="color: #666">{path}</p>',
            '       </span>',
            '       <span class="right-column"><a href="javascript:;" class="icon-remove icon-2x"></a></span>',
            '   </div>',
            '</tpl>'
        );

        return Ext.create('Ext.view.View', {
            store: me.selectedContentStore,
            itemId: 'relationshipView',
            tpl: template,
            itemSelector: 'div.admin-related-item',
            emptyText: '',
            deferEmptyText: false,
            listeners: {
                itemclick: function (view, contentModel, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    if (clickedElement.hasCls('icon-remove')) {
                        me.selectedContentStore.remove(contentModel);
                    }
                }
            }
        });
    },


    /**
     * @private
     */
    onLibraryButtonClicked: function () {
        alert('Open library now');
    },


    /**
     * @private
     */
    alertContentIsAdded: function (contentModel) {
        console.log('Temporary alert! Can not have duplicates in Relationship input\n"' + contentModel.get('path') +
                    '" has already been added');
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
    },

    /**
     * @private
     */
    remoteGetRelationshipType: function (relationshipTypeName, callback) {
        var getRelationshipTypeCommand = {
            'qualifiedRelationshipTypeName': relationshipTypeName,
            'format': 'JSON'
        };

        Admin.lib.RemoteService.relationshipType_get(getRelationshipTypeCommand, function (response) {
            if (response && response.success) {
                callback(response.relationshipType);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to load relationship type");
            }
        });
    }

})
;
