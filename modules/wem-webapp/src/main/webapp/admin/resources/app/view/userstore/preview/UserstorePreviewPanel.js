Ext.define( 'Admin.view.userstore.preview.UserstorePreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userstorePreviewPanel',

    autoScroll: true,
    layout: 'card',
    cls: 'cms-preview-panel',

    collapsible: true,

    showToolbar: true,

    initComponent: function()
    {
        this.items = [
            this.createNoneSelection(),
            this.createUserstoreSelection()
        ];

        this.callParent( arguments );
    },

    createUserstoreSelection: function()
    {
        return {
            xtype: 'container',
            itemId: 'userstoreDetails',
            layout: {
                type: 'column',
                columns: 3
            },
            defaults: {
                border: 0
            },
            items: [
                {
                    xtype: 'component',
                    width: 100,
                    cls: 'west',
                    itemId: 'previewPhoto',
                    tpl: Templates.userstore.previewPhoto,
                    data: this.data,
                    margin: 5
                },
                {
                    xtype: 'container',
                    columnWidth: 1,
                    margin: '5 0',
                    defaults: {
                        border: 0
                    },
                    items: [
                        {
                            xtype: 'component',
                            cls: 'north',
                            itemId: 'previewHeader',
                            padding: '5 5 15',
                            tpl: Templates.userstore.previewHeader,
                            data: this.data
                        },
                        {
                            xtype: 'component',
                            flex: 1,
                            cls: 'center',
                            xtype: 'tabpanel',
                            items: [
                                {
                                    title: "Configuration",
                                    itemId: 'configurationTab',
                                    layout: 'anchor',
                                    items: [
                                        {
                                            xtype: 'textarea',
                                            cls: 'config-container',
                                            grow: true,
                                            readOnly: true,
                                            anchor: '100%',
                                            itemId: 'configurationArea'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'component',
                    width: 300,
                    margin: 5,
                    itemId: 'previewInfo',
                    cls: 'east',
                    tpl: Templates.userstore.previewCommonInfo,
                    data: this.data
                }
            ]
        };
    },

    createNoneSelection: function()
    {
        var tpl = new Ext.XTemplate( Templates.userstore.noUserstoreSelected );
        var panel = {
            xtype: 'panel',
            itemId: 'noneSelectedPanel',
            styleHtmlContent: true,
            padding: 10,
            border: 0,
            tpl: tpl,
            data: {}
        };

        return panel;
    },

    setData: function( data )
    {
        if ( data ) {

            this.data = data;

            var previewHeader = this.down( '#previewHeader' );
            previewHeader.update( data );

            var previewPhoto = this.down( '#previewPhoto' );
            previewPhoto.update( data );

            var previewInfo = this.down( '#previewInfo' );
            previewInfo.update( data );

            var configurationArea = this.down( '#configurationArea' );
            configurationArea.setValue( data['configXML'] );
            this.getLayout().setActiveItem( 'userstoreDetails' );
        }
        else {
            this.getLayout().setActiveItem( 'noneSelectedPanel' );
        }
    },

    getData: function()
    {
        return this.data;
    }


} );