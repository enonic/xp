Ext.define( 'Admin.controller.datadesigner.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],

    views: ['Admin.view.datadesigner.wizard.WizardPanel'],

    init: function()
    {
        this.application.on( {
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


    showEditContentTypePanel: function()
    {
        var selection = this.getGridPanel().getSelectionModel().getSelection();
        if ( selection.length === 1 ) {
            this.createEditContentPanel(selection[0]);
        }
    },


    showPreviewContentTypePanel: function()
    {
        var selection = this.getGridPanel().getSelectionModel().getSelection();
        if ( selection.length === 1 ) {
            Ext.Msg.alert('Preivew', 'Name: ' + selection[0].raw.name + ', key: ' + selection[0].raw.key);
        }
    },


    showDeleteContentTypeWindow: function()
    {
        var selection = this.getGridPanel().getSelectionModel().getSelection();
        if ( selection.length === 1 ) {
            Ext.Msg.alert('Delete', 'Name: ' + selection[0].raw.name + ', key: ' + selection[0].raw.key);
        }
    },


    createEditContentPanel: function( contentType, forceNew )
    {
        var tabPanel = this.getCmsTabPanel();

        if ( contentType && !forceNew ) {

            tabPanel.el.mask( "Loading..." );

            Ext.Ajax.request( {
                url: 'resources/data/mock_dataDesignerContentType.json',
                method: 'GET',
                params: {
                    key: contentType.raw.key
                },
                success: function ( response )
                {
                    var obj = Ext.decode( response.responseText, true );
                    tabPanel.el.unmask();

                    tabPanel.addTab( {
                        xtype: 'dataDesignerWizardPanel',
                        id:'tab-content-type-' + contentType.raw.key,
                        title: contentType.raw.name,
                        iconCls: 'icon-data-designer-16',
                        modelData: obj
                    });
                }
            });
        } else {
            tabPanel.addTab( {
                xtype:'dataDesignerWizardPanel',
                title:'New Content Type'
            });
        }
    },


    getGridPanel: function()
    {
        return Ext.ComponentQuery.query( 'contentTypeGridPanel' )[0];
    },


    getFilterPanel: function()
    {
        return Ext.ComponentQuery.query( 'filterPanel' )[0];
    },


    getDetailPanel: function()
    {
        return Ext.ComponentQuery.query( 'contentTypeDetailPanel' )[0];
    }

});