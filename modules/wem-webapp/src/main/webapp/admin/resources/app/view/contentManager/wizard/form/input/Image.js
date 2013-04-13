Ext.define('Admin.view.contentManager.wizard.form.input.Image', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Image',

    requires: [
        'Admin.store.contentManager.ContentStore',
        'Admin.view.FileUploadWindow',
        'Admin.view.contentManager.wizard.form.ImagePopupDialog'
    ],

    defaultOccurrencesHandling: false,

    initComponent: function () {
        var me = this;
        this.selectedContentStore = this.createSelectedContentStore();
        this.selectedDataView = this.createViewForSelectedContent();

        this.items = [
            this.createHiddenInput(),
            this.createComboBox(),
            this.createOpenLibraryButton(),
            this.createUploadButton(),
            this.selectedDataView
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
                        var relationshipTypeIcon = me.el.down('.admin-image-icon');
                        relationshipTypeIcon.set({'src': iconUrl});
                        relationshipTypeIcon.setOpacity(0.5);
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
                                    var existing = me.selectedContentStore.findRecord('id', content.get('id'));
                                    if (existing) {
                                        content.set('grayedOutComboItem', 'admin-inputimage-combo-grayed-out-item');
                                    } else {
                                        content.set('grayedOutComboItem', '');
                                    }
                                    return true;
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

        this.contentStore = new Admin.store.contentManager.ContentStore();
        this.contentStore.proxy.extraParams = {
            contentTypes: ['system:image']
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

            width: 435,
            fieldCls: 'admin-inputimage-input',
            displayField: 'displayName',
            valueField: 'id',
            tpl: listItemTpl,
            fieldSubTpl: fieldTpl,
            cls: 'admin-inputimage-combo',
            listConfig: {
                cls: 'admin-inputimage-list',
                emptyText: 'No matching items'
            },

            store: this.contentStore,
            listeners: {
                select: function (combo, records) {
                    combo.setValue('');
                    me.onContentSelected(records);
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
    createUploadButton: function () {
        var me = this;
        return {
            xtype: 'button',
            itemId: 'uploadButton',
            tooltip: 'Upload image',
            iconCls: 'admin-inputimage-upload-icon',
            cls: 'nobg icon-button',
            scale: 'medium',
            width: '24',
            margin: '5 5',
            handler: function () {
                me.getFileUploadWindow().show();
            }
        };
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
            iconCls: 'admin-inputimage-library-icon',
            cls: 'nobg icon-button',
            scale: 'medium',
            width: '24',
            margin: '5 5',
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

        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-inputimage">',
            '       <img class="image" src="{iconUrl}?size=140&thumbnail=false"/>',
            '       <div class="loader"></div>',
//            '       <div class="top-bar"><a href="javascript:;" class="admin-remove-button">Remove</a></div>',
            '       <div class="bottom-bar">',
            '           <h6>{displayName}</h6>',
            '       </div>',
            '       <div class="admin-zoom" style="background-image: url({iconUrl}?size=140&thumbnail=false);"></div>',
            '   </div>',
            '</tpl>'
        );

        return Ext.create('Ext.view.View', {
            store: me.selectedContentStore,
            tpl: template,
            itemSelector: 'div.admin-inputimage',
            selectedItemCls: 'admin-inputimage-selected',
            itemId: 'selectionView',
            emptyText: 'No items selected',
            trackOver: true,
            overItemCls: 'over',
            deferEmptyText: false,
            width: 520,
            listeners: {
                itemclick: function (view, contentModel, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    var viewEl = view.getEl();
                    if (clickedElement.hasCls('admin-zoom')) {
                        view.getSelectionModel().deselectAll();
                        return false;
                    } else {


                        var offset = (index + 1) % 3 > 0 ? 3 - (index + 1) % 3 : 0
                        var insertPoint = viewEl.query('.admin-inputimage')[index + offset];
                        var picker = me.createImageDialog(view, contentModel);
                        if (insertPoint) {
                            picker.getEl().insertAfter(insertPoint);
                        } else {
                            picker.getEl().insertAfter(viewEl.last());
                        }

                    }
                },
                itemadd: function () {
                    this.getSelectionModel().deselectAll();
                },
                deselect: function () {
                    if (me.getImageDialog()) {
                        me.getImageDialog().hide();
                        var parent = me.up();
                        if (Ext.isFunction(parent.doComponentLayout)) {
                            parent.doComponentLayout();
                        }
                    }
                },
                select: function () {
                    if (me.getImageDialog()) {
                        me.getImageDialog().show();
                        var parent = me.up();
                        if (Ext.isFunction(parent.doComponentLayout)) {
                            parent.doComponentLayout();
                        }
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
        var isAlreadyAdded = this.selectedContentStore.findRecord('id', contentModel.get('id'));
        if (isAlreadyAdded) {
            this.alertContentIsAdded(contentModel);
            return;
        }
        this.selectedContentStore.add(contentModel);
        this.hideLoaderOnImageLoad(contentModel);
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
    onFilesUploaded: function (win, files) {
        var me = this;
        Ext.each(files, function (file) {
            me.createTemporaryImageContent(file.response, function (contentModel) {
                me.selectedContentStore.add(contentModel);
                me.hideLoaderOnImageLoad(contentModel);
            });
        });
        win.close();
    },


    /**
     * @private
     */
    hideLoaderOnImageLoad: function (contentModel) {
        if (this.selectedDataView) {
            // need to do it here instead of 'itemadd' event of dataview
            // because it is not being thrown for the first added item
            var node = this.selectedDataView.getNode(contentModel);
            if (node) {
                Ext.fly(node).down('img').on('load', function (event, target, opts) {
                    Ext.fly(target).next('.loader').destroy();
                })
            }
        }
    },

    /**
     * @private
     */
    createTemporaryImageContent: function (file, callback) {
        var me = this;
        this.remoteCreateBinary(file.id, function (binaryId) {
            me.remoteCreateImageContent(file.name, file.mimeType, binaryId, function (contentId) {
                var getContentCommand = {
                    contentIds: [contentId]
                };
                Admin.lib.RemoteService.content_get(getContentCommand, function (getContentResponse) {
                    if (getContentResponse && getContentResponse.success) {
                        var contentData = getContentResponse.content[0];
                        var contentModel = new Admin.model.contentManager.ContentModel(contentData);
                        callback(contentModel);
                    }
                });
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
            "qualifiedContentTypeName": 'system:image',
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
    },

    createImageDialog: function (view, model) {
        var me = this;
        if (this.dialog) {
            this.dialog.updateTpl(model.data);
            return this.dialog;
        } else {
            this.dialog = Ext.create('widget.imagePopupDialog', {
                renderTo: view.getEl(),
                data: model.data,
                removeHandler: function () {
                    var selectionModel = view.getSelectionModel();
                    var selection = selectionModel.getSelection();
                    if (selection.length > 0) {
                        selectionModel.deselectAll();
                        me.selectedContentStore.remove(selection[0]);
                    }

                },
                editHandler: function () {
                    alert('TODO: Implement Edit functionality');
                }
            });
            return this.dialog;
        }
    },

    getImageDialog: function () {
        return this.dialog;
    }

});
