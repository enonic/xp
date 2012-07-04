Ext.define('Admin.controller.datadesigner.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],

    views: ['Admin.view.datadesigner.wizard.WizardPanel'],

    init: function () {
        this.application.on({
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
            Ext.Msg.alert('Delete', 'Name: ' + contentType[0].raw.name + ', key: ' + contentType[0].raw.key);
        }
    },


    createEditContentPanel: function (contentType, forceNew) {
        var tabPanel = this.getCmsTabPanel();

        if (contentType && !forceNew) {

            tabPanel.el.mask("Loading...");

            Ext.Ajax.request({
                url: 'resources/data/mock_dataDesignerContentType.json',
                method: 'GET',
                params: {
                    key: contentType.raw.key
                },
                success: function (response) {
                    var obj = Ext.decode(response.responseText, true);
                    tabPanel.el.unmask();

                    tabPanel.addTab({
                        xtype: 'dataDesignerWizardPanel',
                        id: 'tab-content-type-' + contentType.raw.key,
                        title: contentType.raw.name,
                        iconCls: 'icon-data-designer-16',
                        modelData: obj
                    });
                }
            });
        } else {
            tabPanel.addTab({
                xtype: 'dataDesignerWizardPanel',
                title: 'New Content Type'
            });
        }
    },

    createPreviewContentPanel: function (contentType) {
        var tabs = this.getCmsTabPanel();

        if (contentType) {
            tabs.addTab({
                xtype: 'contentTypeDetailPanel',
                data: contentType,
                title: 'View Content Type'
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
    }

});