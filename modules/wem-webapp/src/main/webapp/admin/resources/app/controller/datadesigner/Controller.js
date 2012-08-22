Ext.define('Admin.controller.datadesigner.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],

    views: [
        'Admin.view.datadesigner.wizard.WizardPanel',
        'Admin.view.datadesigner.preview.DetailPanel'
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
            this.createEditContentPanel(contentType[i].raw);
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
            var name = contentType[0].raw ? contentType[0].raw.name : contentType[0].name;
            var key = contentType[0].raw ? contentType[0].raw.key : contentType[0].key;
            Ext.Msg.alert('Delete', 'Name: ' + name + ', key: ' + key);
        }
    },


    createEditContentPanel: function (contentType, forceNew) {
        var tabPanel = this.getCmsTabPanel();

        if (contentType && !forceNew) {

            /*tabPanel.el.mask("Loading...");

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
             });*/

            tabPanel.addTab({
                xtype: 'dataDesignerWizardPanel',
                id: 'tab-content-type-' + contentType.key,
                title: contentType.name,
                iconCls: 'icon-data-designer-16',
                modelData: contentType
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

    getCurrentTab: function () {
        return this.getCmsTabPanel().getActiveTab();
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