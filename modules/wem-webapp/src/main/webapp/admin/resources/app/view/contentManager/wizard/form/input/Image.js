Ext.define('Admin.view.contentManager.wizard.form.input.Image', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Image',

    requires: [
        'Admin.store.contentManager.ContentStore',
        'Admin.view.FileUploadWindow'
    ],

    defaultOccurrencesHandling: false,

    initComponent: function () {
        var me = this;
        this.selectedContentStore = this.createSelectedContentStore();

        this.items = [
            this.createHiddenInput(),
            this.createComboBox(),
            this.createUploadButton(),
            this.createViewForSelectedContent()
        ];

        if (this.inputConfig && this.inputConfig.type && this.inputConfig.type.config) {
            var getRelationshipTypeCommand = {
                qualifiedRelationshipTypeName: this.inputConfig.type.config.relationshipType,
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

        this.callParent(arguments);
        this.setValue(this.value);
    },


    getValue: function () {
        var value = this.items.items[0].getValue();
        if (value && Ext.isString(value)) {
            value = value.split(',');
        } else {
            return [];
        }

        var valueList = [];
        for (var i = 0; i < value.length; i++) {
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
                    if (me.contentStore) {
                        me.contentStore.clearFilter(true);
                        me.contentStore.filter(
                            {
                                filterFn: function (content) {
                                    return !me.selectedContentStore.findRecord('id', content.get('id'));
                                }
                            });
                    }
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

        // default template with icon and library link added
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
            '       <img src="{iconUrl}?size=48" alt="{displayName}" width="48" height="48"/>',
            '       <div class="info">',
            '           <h6>{displayName}</h6>',
            '           <div style="color: #666">{path}</div>',
            '       </div>',
            '       <div class="x-clear"></div>',
            '   </div>',
            '</tpl>'
        ];

        this.contentStore = new Admin.store.contentManager.ContentStore();
        this.contentStore.proxy.extraParams = {
            contentTypes: ['System:image']
        };

        var combo = {
            xtype: 'combo',
            emptyText: 'Start typing',
            submitValue: false,
            hideTrigger: true,
            forceSelection: true,
            minChars: 1,
            queryMode: 'remote',
            queryParam: 'fulltext',
            autoSelect: false,

            fieldCls: 'admin-relationship-input',
            displayField: 'displayName',
            valueField: 'id',
            tpl: listItemTpl,
            fieldSubTpl: fieldTpl,
            cls: 'admin-relationship-combo',
            listConfig: {
                cls: 'admin-relationship-list',
                emptyText: 'No matching items'
            },

            store: this.contentStore,
            listeners: {
                select: function (combo, records) {
                    combo.setValue('');
                    me.onContentSelected(records);
                },
                afterrender: function (cmp) {
                    cmp.el.on('click', me.onLibraryButtonClicked, me, {
                        delegate: 'a.admin-library-button'
                    });
                }
            }
        };

        return combo;
    },

    /**
     * @private
     */
    createUploadButton: function () {
        var me = this;
        return {
            xtype: 'button',
            itemId: 'uploadButton',
            text: 'Upload image',
            maxWidth: 140,
            margin: '5 0',
            handler: function () {
                me.getFileUploadWindow().show();
            }
        };
    },

    /**
     * @private
     */
    createViewForSelectedContent: function () {
        var me = this;

        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-relationship" style="background-image: url({iconUrl})">',
            '       <div class="top-bar"><a href="javascript:;" class="admin-remove-button">Remove</a></div>',
            '       <div class="bottom-bar">',
            '           <h6>{displayName}</h6>',
            '       </div>',
            '   </div>',
            '</tpl>'
        );

        return Ext.create('Ext.view.View', {
            store: me.selectedContentStore,
            tpl: template,
            itemSelector: 'div.admin-relationship',
            emptyText: 'No items selected',
            trackOver: true,
            overItemCls: 'over',
            deferEmptyText: false,
            listeners: {
                itemclick: function (view, contentModel, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    if (clickedElement.hasCls('admin-remove-button')) {
                        me.selectedContentStore.remove(contentModel);
                    }
                }
            }
        });
    },


    /**
     * @private
     */
    alertContentIsAdded: function (contentModel) {
        console.log('Temporary alert! Can not have duplicates in Image input\n"' + contentModel.get('path') + '" has already been added');
        this.down('combobox').focus('');
    },

    /**
     * @private
     */
    updateHiddenValue: function () {
        var me = this;
        var ids = [];
        if (this.items) {
            Ext.Array.each(me.selectedContentStore.data.items, function (item) {
                ids.push(item.data.id);
            });
            this.getComponent(this.name).setValue(ids);
        }
    },


    /**
     * @private
     */
    onContentSelected: function (contentModels) {
        var contentModel = contentModels[0];
        var isAlreadyAdded = this.selectedContentStore.findRecord('id', contentModel.getId());
        if (isAlreadyAdded) {
            this.alertContentIsAdded(contentModel);
            return;
        }
        this.selectedContentStore.add(contentModel);
    },

    /**
     * @private
     */
    onLibraryButtonClicked: function (event, target) {
        alert('Open library now');
    },

    /**
     * @private
     */
    onFilesUploaded: function (win, files) {
        var me = this;
        Ext.each(files, function (file) {
            me.createTemporaryImageContent(file.response, function (contentId) {
                var contentModel = Ext.create('Admin.model.contentManager.ContentModel', {
                    displayName: file.response.name,
                    iconUrl: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/upload/' + file.response.id),
                    id: contentId
                });
                me.selectedContentStore.add(contentModel);
            });
        });
        win.close();
    },

    /**
     * @private
     */
    createTemporaryImageContent: function (file, callback) {
        var me = this;
        this.remoteCreateBinary(file.id, function (binaryId) {
            me.remoteCreateImageContent(file.name, file.mimeType, binaryId, function (contentId) {
                callback(contentId);
            });
        });
    },

    /**
     * @private
     */
    remoteCreateBinary: function (fileUploadId, callback) {
        var createBinaryCommand = {'uploadFileId': fileUploadId};
        Admin.lib.RemoteService.binary_create(createBinaryCommand, function (response) {
            if (response && response.success) {
                callback(response.binaryId);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to create binary content.");
            }
        });
    },

    /**
     * @private
     */
    remoteCreateImageContent: function (displayName, mimeType, binaryId, callback) {
        var createContentCommand = {
            "contentData": {
                "mimeType": mimeType,
                "binary": binaryId
            },
            "qualifiedContentTypeName": 'System:image',
            "displayName": displayName,
            "temporary": true
        };

        Admin.lib.RemoteService.content_createOrUpdate(createContentCommand, function (response) {
            if (response && response.success) {
                callback(response.contentId);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to create image content.");
            }
        });
    },

    /**
     * @private
     */
    getFileUploadWindow: function () {
        var win = Ext.ComponentQuery.query('fileUploadWindow')[0];
        if (!win) {
            win = Ext.create('widget.fileUploadWindow');
            win.on('uploadcomplete', this.onFilesUploaded, this);
        }
        return win;
    }

});