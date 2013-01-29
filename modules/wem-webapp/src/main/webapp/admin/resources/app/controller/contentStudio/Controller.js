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

                    tabPanel.addTab({
                        xtype: 'contentStudioContentTypeWizardPanel',
                        itemId: 'tab-edit-content-type-' + contentType.raw.qualifiedName,
                        editing: true,
                        title: contentType.raw.name,
                        iconCls: 'icon-content-studio-16',
                        modelData: contentType.raw,
                        data: contentType.raw   /* needed for tab panel to show path */
                    });
                } else {
                    Ext.Msg.alert("Error", r ? r.error : "Unable to retrieve content type.");
                }
            });

        } else {
            tabPanel.addTab({
                xtype: 'contentStudioContentTypeWizardPanel',
                title: 'New Content Type'
            });
        }
    },

    createEditMixinPanel: function (mixin, forceNew) {
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

                    tabPanel.addTab({
                        xtype: 'contentStudioMixinWizardPanel',
                        itemId: 'tab-edit-mixin-' + mixin.raw.qualifiedName,
                        editing: true,
                        title: mixin.raw.name,
                        iconCls: 'icon-content-studio-16',
                        modelData: mixin.raw,
                        data: mixin.raw   /* needed for tab panel to show path */
                    });
                } else {
                    Ext.Msg.alert("Error", r ? r.error : "Unable to retrieve mixin.");
                }
            });

        } else {
            tabPanel.addTab({
                xtype: 'contentStudioMixinWizardPanel',
                title: 'New Mixin'
            });
        }
    },

    createEditRelationshipTypePanel: function (relationshipType, forceNew) {
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

                    tabPanel.addTab({
                        xtype: 'contentStudioRelationshipTypeWizardPanel',
                        itemId: 'tab-edit-relationship-type-' + relationshipType.raw.qualifiedName,
                        editing: true,
                        title: relationshipType.raw.name,
                        iconCls: 'icon-content-studio-16',
                        modelData: relationshipType.raw,
                        data: relationshipType.raw   /* needed for tab panel to show path */
                    });
                } else {
                    Ext.Msg.alert("Error", r ? r.error : "Unable to retrieve relationship type.");
                }
            });

        } else {
            tabPanel.addTab({
                xtype: 'contentStudioRelationshipTypeWizardPanel',
                title: 'New Relationship Type'
            });
        }
    },

    createPreviewContentPanel: function (contentType) {
        var tabs = this.getCmsTabPanel();

        if (contentType) {
            tabs.addTab({
                xtype: 'contentTypeDetailPanel',
                itemId: 'tab-preview-content-type-' + contentType.raw.qualifiedName,
                data: contentType.raw,
                title: contentType.raw.name
            });
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