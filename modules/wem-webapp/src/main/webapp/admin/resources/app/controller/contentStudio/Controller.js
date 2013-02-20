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
            createNewBaseType: {
                fn: this.createNewBaseType,
                scope: this
            },
            showEditBaseTypePanel: {
                fn: this.showEditBaseTypePanel,
                scope: this
            },
            showPreviewContentTypePanel: {
                fn: this.showPreviewContentTypePanel,
                scope: this
            },
            showDeleteBaseTypeWindow: {
                fn: this.showDeleteBaseTypeWindow,
                scope: this
            }
        });
    },


    generateTabId: function (contentType, isEdit) {
        return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + contentType.get('type') + '-' + contentType.get('qualifiedName');
    },

    createNewBaseType: function (record) {
        console.log('Event caught');
        this.createNewBaseTypePanel(record.data.name);
    },

    showEditBaseTypePanel: function (baseType) {
        if (!baseType) {
            baseType = this.getTreeGridPanel().getSelection();
        } else {
            baseType = [].concat(baseType);
        }
        var i;
        for (i = 0; i < baseType.length; i += 1) {
            this.createEditBaseTypePanel(baseType[i]);
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


    showDeleteBaseTypeWindow: function (baseType) {
        if (!baseType) {
            baseType = this.getTreeGridPanel().getSelection();
        } else {
            baseType = [].concat(baseType);
        }
        if (baseType.length === 1) {
            this.getDeleteContentTypeWindow().doShow(baseType);
        }
    },

    showSelectBaseTypeWindow: function () {
        this.getSelectBaseTypeWindow().show();
    },

    createNewBaseTypePanel: function (baseTypeModel) {
        switch (baseTypeModel.data.name) {
        case 'ContentType':
            this.createNewContentTypePanel(baseTypeModel);
            break;
        case 'Mixin':
            this.createNewMixinPanel(baseTypeModel);
            break;
        case 'RelationshipType':
            this.createNewRelationshipTypePanel(baseTypeModel);
            break;
        }
    },

    createEditBaseTypePanel: function (baseTypeModel) {
        var baseType = baseTypeModel && baseTypeModel.data.type;
        switch (baseType) {
        case 'ContentType':
            this.createEditContentTypePanel(baseTypeModel);
            break;
        case 'Mixin':
            this.createEditMixinPanel(baseTypeModel);
            break;
        case 'RelationshipType':
            this.createEditRelationshipTypePanel(baseTypeModel);
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

    createNewContentTypePanel: function (baseTypeModel) {
        var tabPanel = this.getCmsTabPanel();
        tabPanel.addTab({
            id: 'tab-new-content-type',
            editing: true,
            modelData: baseTypeModel.data,
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

    createNewMixinPanel: function (baseTypeModel) {
        var tabPanel = this.getCmsTabPanel();
        tabPanel.addTab({
            id: 'tab-new-mixin',
            editing: true,
            modelData: baseTypeModel.data,
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

    createNewRelationshipTypePanel: function (baseTypeModel) {
        var tabPanel = this.getCmsTabPanel();
        tabPanel.addTab({
            id: 'tab-new-relationship-type',
            editing: true,
            modelData: baseTypeModel.data,
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

    getSelectBaseTypeWindow: function () {
        var win = Ext.ComponentQuery.query('selectSchemaWindow')[0];
        if (!win) {
            win = Ext.create('widget.selectSchemaWindow');
        }
        return win;
    }

});