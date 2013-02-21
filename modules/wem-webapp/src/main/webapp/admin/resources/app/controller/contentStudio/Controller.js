Ext.define('Admin.controller.contentStudio.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],

    views: [
        'Admin.view.contentStudio.wizard.ContentTypeWizardPanel',
        'Admin.view.contentStudio.wizard.MixinWizardPanel',
        'Admin.view.contentStudio.wizard.RelationshipTypeWizardPanel',
        'Admin.view.contentStudio.DeleteContentTypeWindow',
        'Admin.view.contentStudio.SelectSchemaWindow'
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
            this.getDeleteContentTypeWindow().doShow(schema);
        }
    },

    showSelectSchemaWindow: function () {
        this.getSelectSchemaWindow().show();
    },

    createNewSchemaPanel: function (schemaModel) {
        switch (schemaModel.data.name) {
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
        var schema = schemaModel && schemaModel.data.type;
        switch (schema) {
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
            "contentType": [contentType.get('qualifiedName')]
        }, function (r) {
            tabPanel.el.unmask();
            if (r) {
                contentType.raw.configXML = r.contentTypeXml;

                var tabItem = {
                    xtype: 'contentStudioContentTypeWizardPanel',
                    id: me.generateTabId(contentType, true),
                    editing: true,
                    title: contentType.raw.name,
                    iconCls: 'icon-content-studio-16',
                    modelData: contentType.raw,
                    data: contentType.raw   /* needed for tab panel to show path */
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
            modelData: schemaModel.data,
            xtype: 'contentStudioContentTypeWizardPanel',
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
                mixin.raw.configXML = r.mixinXml;

                var tabItem = {
                    xtype: 'contentStudioMixinWizardPanel',
                    id: me.generateTabId(mixin, true),
                    editing: true,
                    title: mixin.raw.name,
                    iconCls: 'icon-content-studio-16',
                    modelData: mixin.raw,
                    data: mixin.raw   /* needed for tab panel to show path */
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
            modelData: schemaModel.data,
            xtype: 'contentStudioMixinWizardPanel',
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
                relationshipType.raw.configXML = r.relationshipTypeXml;

                var tabItem = {
                    xtype: 'contentStudioRelationshipTypeWizardPanel',
                    itemId: me.generateTabId(relationshipType, true),
                    editing: true,
                    title: relationshipType.raw.name,
                    iconCls: 'icon-content-studio-16',
                    modelData: relationshipType.raw,
                    data: relationshipType.raw   /* needed for tab panel to show path */
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
            modelData: schemaModel.data,
            xtype: 'contentStudioRelationshipTypeWizardPanel',
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
                    data: contentType.raw,
                    title: contentType.raw.name
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

    getDeleteContentTypeWindow: function () {
        var win = Ext.ComponentQuery.query('deleteContentTypeWindow')[0];
        if (!win) {
            win = Ext.create('widget.deleteContentTypeWindow');
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