Ext.define( 'Admin.view.datadesigner.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentTypeDetailPanel',
    layout: 'card',

    cls: 'admin-preview-panel',
    overflowX: 'hidden',
    overflowY: 'auto',

    listeners: {
        afterrender: function( component ) {
            component.down( '#noneSelectedComponent' ).update( {} );
        }
    },

    initComponent: function()
    {
        var noneSelectedCmp = this.createNoneSelectedComponent();
        var previewCt = this.createPreviewContainer();

        this.items = [
            noneSelectedCmp,
            previewCt
        ];

        this.callParent( arguments );
    },

    createPreviewContainer: function()
    {
        return  {
            xtype: 'container',
            itemId: 'previewContainer',
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
                    itemId: 'previewIcon',
                    tpl: Templates.datadesigner.previewIcon,
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
                            tpl: Templates.datadesigner.previewHeader,
                            data: this.data
                        },
                        {
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
                    tpl: Templates.datadesigner.previewCommonInfo,
                    data: this.data
                }
            ]
        }
    },

    createNoneSelectedComponent: function()
    {
        var tpl = new Ext.XTemplate( Templates.datadesigner.noContentTypeSelected );

        return {
            xtype: 'component',
            itemId: 'noneSelectedComponent',
            styleHtmlContent: true,
            border: true,
            padding: 5,
            tpl: tpl
        };
    },

    setData: function( data )
    {
        if ( data ) {
            this.data = data;

            var previewHeader = this.down( '#previewHeader' );
            previewHeader.update( data );

            var previewPhoto = this.down( '#previewIcon' );
            previewPhoto.update( data );

            var previewInfo = this.down( '#previewInfo' );
            previewInfo.update( data );

            var configurationArea = this.down( '#configurationArea' );
            configurationArea.setValue( data['configXml'] );
        }
    },

    getData: function()
    {
        return this.data;
    }

});
