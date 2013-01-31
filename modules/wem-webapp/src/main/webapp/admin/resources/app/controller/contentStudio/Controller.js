Ext.define('Admin.controller.contentStudio.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],

    views: [
        'Admin.view.contentStudio.wizard.ContentTypeWizardPanel',
        'Admin.view.contentStudio.wizard.MixinWizardPanel',
        'Admin.view.contentStudio.wizard.RelationshipTypeWizardPanel',
        'Admin.view.contentStudio.DeleteContentTypeWindow'
    ],

    init: function () {
        this.application.on({
            showNewContentTypePanel: {
                fn: this.showNewContentTypePanel,
                scope: this
            },
            showNewMixinPanel: {
                fn: this.showNewContentTypePanel,
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

    showNewContentTypePanel: function () {
        this.createEditContentTypePanel(null, true);
    },

    showNewMixinPanel: function () {
        this.createEditMixinPanel(null, true);
    },

    showNewRelationshipTypePanel: function () {
        this.createEditRelationshipTypePanel(null, true);
    },


    showEditBaseTypePanel: function (contentType, callback) {
        if (!contentType) {
            contentType = this.getTreeGridPanel().getSelection();
        } else {
            contentType = [].concat(contentType);
        }
        var i;
        for (i = 0; i < contentType.length; i += 1) {
            this.createEditBaseTypePanel(contentType[i]);
        }
    },


    showPreviewContentTypePanel: function (contentType, callback) {
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

    createEditBaseTypePanel: function (baseType, forceNew) {
        switch (baseType.data.type) {
        case 'ContentType':
            this.createEditContentTypePanel(baseType, forceNew);
            break;
        case 'Mixin':
            this.createEditMixinPanel(baseType, forceNew);
            break;
        case 'RelationshipType':
            this.createEditRelationshipTypePanel(baseType, forceNew);
            break;
        default:
            break;
        }
    },

    createEditContentTypePanel: function (contentType, forceNew) {

        var me = this;
        var tabPanel = this.getCmsTabPanel();

        if (contentType && !forceNew) {
            tabPanel.el.mask();
            Admin.lib.RemoteService.contentType_get({
                "format": "XML",
                "contentType": [contentType.get('qualifiedName')]
            }, function (r) {
                console.log(r);
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

        } else {
            tabPanel.addTab({
                id: 'tab-new-content-type',
                editing: true,
                xtype: 'contentStudioContentTypeWizardPanel',
                title: 'New Content Type'
            });
        }
    },

    createEditMixinPanel: function (mixin, forceNew) {

        var me = this;
        var tabPanel = this.getCmsTabPanel();

        if (mixin && !forceNew) {
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

        } else {
            tabPanel.addTab({
                id: 'tab-new-mixin',
                editing: true,
                xtype: 'contentStudioMixinWizardPanel',
                title: 'New Mixin'
            });
        }
    },

    createEditRelationshipTypePanel: function (relationshipType, forceNew) {

        var me = this;
        var tabPanel = this.getCmsTabPanel();

        if (relationshipType && !forceNew) {
            tabPanel.el.mask();
            Admin.lib.RemoteService.relationshipType_get({
                "format": "XML",
                "qualifiedRelationshipTypeName": [relationshipType.get('qualifiedName')]
            }, function (r) {
                tabPanel.el.unmask();
                if (r) {
                    relationshipType.raw.configXML = r.contentTypeXml;

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

        } else {
            tabPanel.addTab({
                id: 'tab-new-relationship-type',
                editing: true,
                xtype: 'contentStudioRelationshipTypeWizardPanel',
                title: 'New Relationship Type'
            });
        }
    },

    createPreviewContentPanel: function (contentType) {

        var me = this;
        var tabs = this.getCmsTabPanel();

        if (contentType) {

            var tabItem = {
                id: me.generateTabId(contentType, false),
                xtype: 'contentTypeDetailPanel',
                data: contentType.raw,
                title: contentType.raw.name
            };

            //check if edit tab is open and close it
            var index = tabs.items.indexOfKey(me.generateTabId(contentType, true));
            if (index >= 0) {
                tabs.remove(index);
            }
            tabs.addTab(tabItem, index >= 0 ? index : undefined, undefined);
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
    }

});