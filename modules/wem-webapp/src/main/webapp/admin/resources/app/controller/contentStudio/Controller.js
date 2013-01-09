Ext.define('Admin.controller.contentStudio.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],

    views: [
        'Admin.view.contentStudio.wizard.WizardPanel',
        'Admin.view.contentStudio.DeleteContentTypeWindow'
    ],

    init: function () {
        this.application.on({
            showNewContentTypePanel: {
                fn: this.showNewContentTypePanel,
                scope: this
            },
            showEditContentTypePanel: {
                fn: this.showEditContentTypePanel,
                scope: this
            },
            showPreviewContentTypePanel: {
                fn: this.showPreviewContentTypePanel,
                scope: this
            },
            showDeleteContentTypeWindow: {
                fn: this.showDeleteContentTypeWindow,
                scope: this
            }
        });
    },


    showNewContentTypePanel: function () {
        this.createEditContentPanel(null, true);
    },

    showEditContentTypePanel: function (contentType, callback) {
        if (!contentType) {
            contentType = this.getTreeGridPanel().getSelection();
        } else {
            contentType = [].concat(contentType);
        }
        var i;
        for (i = 0; i < contentType.length; i += 1) {
            this.createEditContentPanel(contentType[i]);
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


    showDeleteContentTypeWindow: function (contentType) {
        if (!contentType) {
            contentType = this.getTreeGridPanel().getSelection();
        } else {
            contentType = [].concat(contentType);
        }
        if (contentType.length === 1) {
            this.getDeleteContentTypeWindow().doShow(contentType);
        }
    },


    createEditContentPanel: function (contentType, forceNew) {
        var tabPanel = this.getCmsTabPanel();

        if (contentType && !forceNew) {
            tabPanel.el.mask();
            Admin.lib.RemoteService.contentType_getConfig({"qualifiedContentTypeName": [contentType.get('qualifiedName')]}, function (r) {
                tabPanel.el.unmask();
                if (r) {
                    contentType.raw.configXML = r.contentTypeXml;

                    tabPanel.addTab({
                        xtype: 'contentStudioWizardPanel',
                        itemId: 'tab-edit-content-type-' + contentType.raw.key,
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
                xtype: 'contentStudioWizardPanel',
                title: 'New Content Type'
            });
        }
    },

    createPreviewContentPanel: function (contentType) {
        var tabs = this.getCmsTabPanel();

        if (contentType) {
            tabs.addTab({
                xtype: 'contentTypeDetailPanel',
                itemId: 'tab-preview-content-type-' + contentType.raw.key,
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