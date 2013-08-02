Ext.define('Admin.controller.schemaManager.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],

    views: [
        'Admin.view.schemaManager.wizard.ContentTypeWizardPanel',
        'Admin.view.schemaManager.wizard.MixinWizardPanel',
        'Admin.view.schemaManager.wizard.RelationshipTypeWizardPanel',
        'Admin.view.schemaManager.DeleteSchemaWindow',
        'Admin.view.schemaManager.SelectSchemaWindow'
    ],

    init: function () {
        this.application.on({
            createNewSchema: {
                fn: this.createNewSchema,
                scope: this
            },
            showEditSchemaPanel: {
                fn: this.showEditSchemaPanel,
                scope: this
            },
            showPreviewContentTypePanel: {
                fn: this.showPreviewContentTypePanel,
                scope: this
            },
            showDeleteSchemaWindow: {
                fn: this.showDeleteSchemaWindow,
                scope: this
            }
        });
    },


    generateTabId: function (contentType, isEdit) {
        return 'tab-' + (isEdit ? 'edit-' : 'preview-') + contentType.get('type') + '-' + contentType.get('qualifiedName');
    },

    createNewSchema: function (record) {
        console.log('Event caught');
        this.createNewSchemaPanel(record.data.name);
    },

    showEditSchemaPanel: function (schema) {
        if (!schema) {
            schema = this.getTreeGridPanel().getSelection();
        } else {
            schema = [].concat(schema);
        }
        var i;
        for (i = 0; i < schema.length; i += 1) {
            this.createEditSchemaPanel(schema[i]);
        }
    },


    showPreviewContentTypePanel: function (contentType) {
        if (!contentType) {
            contentType = this.getTreeGridPanel().getSelection();
        } else {
            contentType = [].concat(contentType);
        }
        var i;
        for (i = 0; i < contentType.length; i += 1) {
            this.createPreviewContentPanel(contentType[i]);
        }
    },


    showDeleteSchemaWindow: function (schema) {
        if (!schema) {
            schema = this.getTreeGridPanel().getSelection();
        } else {
            schema = [].concat(schema);
        }
        if (schema.length === 1) {
            this.getSchemaWindow().doShow(schema);
        }
    },

    showSelectSchemaWindow: function () {
        this.getSelectSchemaWindow().show();
    },

    createNewSchemaPanel: function (schemaModel) {
        switch (schemaModel.get('name')) {
        case 'ContentType':
            this.createNewContentTypePanel(schemaModel);
            break;
        case 'Mixin':
            this.createNewMixinPanel(schemaModel);
            break;
        case 'RelationshipType':
            this.createNewRelationshipTypePanel(schemaModel);
            break;
        }
    },

    createEditSchemaPanel: function (schemaModel) {
        switch (schemaModel.get('type')) {
        case 'ContentType':
            this.createEditContentTypePanel(schemaModel);
            break;
        case 'Mixin':
            this.createEditMixinPanel(schemaModel);
            break;
        case 'RelationshipType':
            this.createEditRelationshipTypePanel(schemaModel);
            break;
        }
    },

    createEditContentTypePanel: function (contentType) {
        if (!contentType) {
            return;
        }

        var me = this;
        var tabPanel = this.getCmsTabPanel();

        tabPanel.el.mask();
        Admin.lib.RemoteService.contentType_get({
            "format": "XML",
            "qualifiedNames": [contentType.get('qualifiedName')]
        }, function (r) {
            tabPanel.el.unmask();
            if (r) {
                contentType.set('configXML', r.contentTypeXmls[0]);

                var tabItem = {
                    xtype: 'schemaManagerContentTypeWizardPanel',
                    id: me.generateTabId(contentType, true),
                    editing: true,
                    title: contentType.get('name'),
                    iconCls: 'icon-schema-manager-16',
                    data: contentType
                };

                //check if preview tab is open and close it
                var index = tabPanel.items.indexOfKey(me.generateTabId(contentType, false));
                if (index >= 0) {
                    tabPanel.remove(index);
                }
                tabPanel.addTab(tabItem, index >= 0 ? index : undefined, undefined);

            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to retrieve content type.");
            }
        });
    },

    createNewContentTypePanel: function (schemaModel) {
        var tabPanel = this.getCmsTabPanel();
        tabPanel.addTab({
            id: 'tab-new-content-type',
            editing: true,
            isNew: true,
            data: schemaModel,
            xtype: 'schemaManagerContentTypeWizardPanel',
            title: '[New Content Type]'
        });
    },

    createEditMixinPanel: function (mixin) {
        if (!mixin) {
            return;
        }

        var me = this;
        var tabPanel = this.getCmsTabPanel();

        tabPanel.el.mask();
        Admin.lib.RemoteService.mixin_get({
            "format": "XML",
            "mixin": [mixin.get('qualifiedName')]
        }, function (r) {
            tabPanel.el.unmask();
            if (r) {
                mixin.set('configXML', r.mixinXml);

                var tabItem = {
                    xtype: 'schemaManagerMixinWizardPanel',
                    id: me.generateTabId(mixin, true),
                    editing: true,
                    title: mixin.get('name'),
                    iconCls: 'icon-schema-manager-16',
                    data: mixin   /* needed for tab panel to show path */
                };

                //check if preview tab is open and close it
                var index = tabPanel.items.indexOfKey(me.generateTabId(mixin, false));
                if (index >= 0) {
                    tabPanel.remove(index);
                }
                tabPanel.addTab(tabItem, index >= 0 ? index : undefined, undefined);

            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to retrieve mixin.");
            }
        });
    },

    createNewMixinPanel: function (schemaModel) {
        var tabPanel = this.getCmsTabPanel();
        tabPanel.addTab({
            id: 'tab-new-mixin',
            editing: true,
            isNew: true,
            data: schemaModel,
            xtype: 'schemaManagerMixinWizardPanel',
            title: '[New Mixin]'
        });
    },

    createEditRelationshipTypePanel: function (relationshipType) {
        if (!relationshipType) {
            return;
        }

        var me = this;
        var tabPanel = this.getCmsTabPanel();

        tabPanel.el.mask();
        Admin.lib.RemoteService.relationshipType_get({
            "format": "XML",
            "qualifiedRelationshipTypeName": [relationshipType.get('qualifiedName')]
        }, function (r) {
            tabPanel.el.unmask();
            if (r) {
                relationshipType.set('configXML', r.relationshipTypeXml);

                var tabItem = {
                    xtype: 'schemaManagerRelationshipTypeWizardPanel',
                    itemId: me.generateTabId(relationshipType, true),
                    editing: true,
                    title: relationshipType.get('name'),
                    iconCls: 'icon-schema-manager-16',
                    data: relationshipType
                };

                //check if preview tab is open and close it
                var index = tabPanel.items.indexOfKey(me.generateTabId(relationshipType, false));
                if (index >= 0) {
                    tabPanel.remove(index);
                }
                tabPanel.addTab(tabItem, index >= 0 ? index : undefined, undefined);

            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to retrieve relationship type.");
            }
        });
    },

    createNewRelationshipTypePanel: function (schemaModel) {
        var tabPanel = this.getCmsTabPanel();
        tabPanel.addTab({
            id: 'tab-new-relationship-type',
            editing: true,
            isNew: true,
            data: schemaModel,
            xtype: 'schemaManagerRelationshipTypeWizardPanel',
            title: '[New Relationship Type]'
        });
    },

    createPreviewContentPanel: function (contentType) {

        var me = this;
        var tabs = this.getCmsTabPanel();

        if (contentType) {

            var activeTab = tabs.setActiveTab(me.generateTabId(contentType, true));

            if (!activeTab) {
                var tabItem = {
                    id: me.generateTabId(contentType, false),
                    xtype: 'contentTypeDetailPanel',
                    data: contentType,
                    title: contentType.get('name'),
                    isFullPage: true
                };
                tabs.addTab(tabItem);
            }
        }
    },


    getTreeGridPanel: function () {
        return Ext.ComponentQuery.query('contentTypeTreeGridPanel')[0];
    },


    getFilterPanel: function () {
        return Ext.ComponentQuery.query('filterPanel')[0];
    },


    getDetailPanel: function () {
        return Ext.ComponentQuery.query('contentTypeDetailPanel')[0];
    },

    getSchemaWindow: function () {
        var win = Ext.ComponentQuery.query('deleteSchemaWindow')[0];
        if (!win) {
            win = Ext.create('widget.deleteSchemaWindow');
        }
        return win;
    },

    getSelectSchemaWindow: function () {
        var win = Ext.ComponentQuery.query('selectSchemaWindow')[0];
        if (!win) {
            win = Ext.create('widget.selectSchemaWindow');
        }
        return win;
    }

});