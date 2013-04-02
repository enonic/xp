Ext.define('Admin.view.contentManager.wizard.form.input.EmbeddedImage', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.EmbeddedImage',

    requires: [
        'Admin.store.contentManager.ContentStore',
        'Admin.view.FileUploadWindow'
    ],

    initComponent: function () {

        this.selectedContentStore = this.createSelectedContentStore();

        this.items = [
            this.createHiddenInput(),
            this.createComboBox(),
            this.createUploadButton(),
            this.createViewForSelectedContent()
        ];

        if (this.inputConfig && this.inputConfig.type) {
            //TODO: use qualified name
            var getRelationshipTypeCommand = {
                qualifiedRelationshipTypeName: 'System:' + this.inputConfig.type.name
            };
            Admin.lib.RemoteService.relationshipType_get(getRelationshipTypeCommand, this.onRelationshipTypeReceived);
        }

        this.callParent(arguments);
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

    setValue: function (value) {
        var me = this;
        var getContentCommand = {
            contentIds: value
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
    onRelationshipTypeReceived: function (response) {
        if (response && response.success) {
            if (this.rendered) {
                this.el.down('.admin-image-type').attr('src', response.iconUrl);
            } else {
                this.relationshipTypeIconUrl = response.iconUrl;
            }
        }
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
            '   <div class="x-boundlist-item">',
            '       <div>',
            '           <img src="{iconUrl}?size=48" alt="{displayName}"/>',
            '           <div class="info">',
            '               <h6>{displayName}</h6>',
            '               <div style="color: #666">{path}</div>',
            '           </div>',
            '       </div>',
            '   </div>',
            '</tpl>'
        ];

        var contentStore = new Admin.store.contentManager.ContentStore();
        contentStore.proxy.extraParams = {
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

            displayField: 'displayName',
            valueField: 'id',
            tpl: listItemTpl,
            fieldSubTpl: fieldTpl,
            cls: 'admin-embedded-image-combo',
            listConfig: {
                cls: 'admin-embedded-image-list',
                emptyText: 'No matching items'
            },

            store: contentStore,
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
            '   <div class="admin-embedded-image" style="background-image: url({iconUrl})">',
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
            itemSelector: 'div.admin-embedded-image',
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
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var contentModel = Ext.create('Admin.model.contentManager.ContentModel', {
                displayName: file.response.name,
                iconUrl: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/upload/' + file.response.id)
            });
            this.selectedContentStore.add(contentModel);
        }
        win.close();
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