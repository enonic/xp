Ext.define( 'Admin.view.contentManager.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentDetail',

    requires: [
        'Admin.view.contentManager.DetailToolbar',
        'Admin.view.account.MembershipsGraphPanel'
    ],

    autoScroll: true,
    layout: 'card',
    cls: 'cms-preview-panel',

    collapsible: true,
    showToolbar: true,
    grid: undefined,

    initComponent: function()
    {

        if ( Ext.isEmpty( this.data ) ) {

            this.activeItem = 'noSelection';

        } else if ( Ext.isObject( this.data ) || this.data.length == 1 ) {

            this.activeItem = 'singleSelection';

        } else if ( this.data.length > 1 && this.data.length <= 10 ) {

            this.activeItem = 'largeBoxSelection';

        } else {

            this.activeItem = 'smallBoxSelection';

        }

        this.items = [
            this.createNoSelection(),
            this.createSingleSelection( this.data ),
            this.createLargeBoxSelection( this.data ),
            this.createSmallBoxSelection( this.data )
        ];

        if ( this.showToolbar ) {
            this.tbar = Ext.createByAlias( 'widget.contentDetailToolbar' );
        }

        this.callParent( arguments );
    },


    createNoSelection: function()
    {
        return {
            itemId: 'noSelection',
            xtype: 'panel',
            styleHtmlContent: true,
            html: '<h2 class="message">Nothing selected</h2>'
        };
    },

    createSingleSelection: function( data )
    {
        var info;
        if ( Ext.isArray( data ) && data.length > 0 ) {
            info = data[0].data
        } else if ( !Ext.isEmpty( data ) ) {
            info = data.data;
        }
        return {
            xtype: 'container',
            itemId: 'singleSelection',
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
                    tpl: Templates.contentManager.previewPhoto,
                    data: info,
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
                            tpl: Templates.contentManager.previewHeader,
                            data: info
                        },
                        {
                            flex: 1,
                            cls: 'center',
                            xtype: 'tabpanel',
                            items: [
                                {
                                    title: "Content",
                                    itemId: 'contentTab',
                                    html: 'Content'
                                },
                                {
                                    title: "Tree",
                                    itemId: 'treeTab',
                                    html: 'Tree'
                                },
                                {
                                    title: "Page",
                                    itemId: 'pageTab',
                                    html: 'Page'
                                },
                                {
                                    title: "Security",
                                    itemId: 'securityTab',
                                    html: 'Security'
                                },
                                {
                                    title: "Relations",
                                    itemId: 'relationsTab',
                                    items: [
                                        {
                                            tpl: Templates.account.userPreviewMemberships
                                        },
                                        {
                                            xtype: 'membershipsGraphPanel',
                                            extraCls: 'admin-memberships-graph',
                                            listeners: {
                                                afterrender: function( cmp )
                                                {
                                                    var data = this.data ? this.data['graph'] : undefined;
                                                    if ( data ) {
                                                        cmp.setGraphData( data );
                                                    }
                                                }
                                            }
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
                    tpl: Templates.contentManager.previewCommonInfo,
                    data: info
                }
            ]
        };
    },

    createLargeBoxSelection: function( data )
    {
        var tpl = Ext.Template( Templates.contentManager.previewSelectionLarge );

        var panel = {
            xtype: 'panel',
            itemId: 'largeBoxSelection',
            styleHtmlContent: true,
            autoScroll: true,
            listeners: {
                click: {
                    element: 'body',
                    fn: this.deselectItem,
                    scope: this
                }
            },
            padding: 10,
            border: 0,
            tpl: tpl,
            data: data
        };

        return panel;
    },

    createSmallBoxSelection: function( data )
    {
        var tpl = Ext.Template( Templates.contentManager.previewSelectionSmall );

        var panel = {
            xtype: 'panel',
            itemId: 'smallBoxSelection',
            styleHtmlContent: true,
            listeners: {
                click: {
                    element: 'body',
                    fn: this.deselectItem,
                    scope: this
                }
            },
            autoScroll: true,
            padding: 10,
            border: 0,
            tpl: tpl,
            data: data
        };

        return panel;
    },

    deselectItem: function( event, target )
    {
        var className = target.className;
        if ( className && className === 'remove-selection' ) {
            var key = target.attributes.getNamedItem( 'id' ).nodeValue.split( 'remove-from-selection-button-' )[1];

            if ( this.grid ) {
                var record = this.grid.getStore().findRecord( 'key', key );
                if ( record ) {
                    Ext.get( 'selected-item-box-' + key ).remove();
                    this.grid.getSelectionModel().deselect( record );
                }
            }
        }
    },


    setData: function( data )
    {
        if ( !data ) {
            return;
        }
        this.data = data;

        if ( Ext.isEmpty( this.data ) ) {

            this.getLayout().setActiveItem( 'noSelection' );

        } else if ( Ext.isObject( this.data ) || this.data.length == 1 ) {

            var data = this.data[0].data;

            var previewHeader = this.down( '#previewHeader' );
            previewHeader.update( data );

            var previewPhoto = this.down( '#previewPhoto' );
            previewPhoto.update( data );

            var previewInfo = this.down( '#previewInfo' );
            previewInfo.update( data );

            this.getLayout().setActiveItem( 'singleSelection' );

        } else if ( this.data.length > 1 && this.data.length <= 10 ) {

            var largeBox = this.down( '#largeBoxSelection' );
            largeBox.update( this.data );

            this.getLayout().setActiveItem( largeBox );

        } else {

            var smallBox = this.down( '#smallBoxSelection' );
            smallBox.update( this.data );

            this.getLayout().setActiveItem( smallBox );

        }

        this.updateTitle( data );
    },

    getData: function()
    {
        return this.data;
    },

    updateTitle: function( data )
    {

        var count = data.length;
        var header = count + " item(s) selected";
        if ( count > 0 ) {
            header += " (<a href='javascript:;' class='clearSelection'>Clear selection</a>)";
        }
        this.setTitle( header );

        var clearSel = this.header.el.down( 'a.clearSelection' );

        if ( clearSel && this.grid ) {
            var selModel = this.grid.getSelectionModel();
            clearSel.on( "click", function()
            {
                selModel.deselectAll();
            }, this );
        }

    }


} );
