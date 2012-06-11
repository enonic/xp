Ext.define( 'Admin.view.account.ExportAccountsWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.exportAccountsWindow',

    dialogTitle: 'Export Accounts',
    dialogInfoTpl: false,

    items: [
        {
            xtype: 'form',
            border: false,
            items: [
                {
                    itemId: 'exportType',
                    xtype: 'radiogroup',
                    fieldLabel: 'Export',
                    defaults: {
                        name: 'exportType',
                        anchor: '100%'
                    },
                    layout: 'anchor',
                    items: [
                        {
                            id: 'selectedCount',
                            boxLabel: 'Selection (<span class="count">0</span>)',
                            inputValue: 'selection',
                            checked: true
                        },
                        {
                            id: 'searchCount',
                            boxLabel: 'Search result (<span class="count">0</span>)',
                            inputValue: 'search'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    margin: '10px 0 10px 105px',
                    defaults: {
                        xtype: 'button',
                        scale: 'medium',
                        margin: '0 10 0 0'
                    },
                    items: [
                        {
                            text: 'Cancel',
                            iconCls: 'icon-cancel-24',
                            action: 'close',
                            handler: function()
                            {
                                this.up( 'window' ).close();
                            }
                        },
                        {
                            text: 'Export',
                            formBind: true,
                            iconCls: 'icon-ok-24',
                            handler: function( btn, evt )
                            {
                                var win = btn.up( 'window' );

                                var query = {};
                                var type = win.down( '#exportType' );
                                if ( type.getValue()['exportType'] == 'selection' ) {
                                    // iterate through selected records and pluck keys
                                    query.keys = Ext.Array.pluck( win.modelData.selected, 'internalId' );
                                } else {
                                    // pass last filter params
                                    query = win.modelData.searched.lastQuery;
                                    query = query || {};
                                }

                                win.close();

                                var form = Ext.get( 'accountsExportForm' );
                                if ( form ) {
                                    form.destroy();
                                }

                                // Create a form in order to do a post request
                                var frameData = "<form id='accountsExportForm' action='data/account/export' method='get'>";
                                for ( var param in query ) {
                                    frameData +=
                                    "<input type='hidden' name='" + param + "' value='" + query[param] + "' />";
                                }
                                frameData += "</form>";

                                form = Ext.core.DomHelper.append( Ext.getBody(), frameData );

                                form.submit();

                            }
                        }
                    ]
                }
            ]
        }
    ],

    initComponent: function()
    {

        this.callParent( arguments );
    },

    doShow: function( model )
    {
        this.callParent( arguments );
        if ( this.modelData ) {
            var form = this.down( 'form' ).getForm();
            var selectedField = form.findField( 'selectedCount' );
            var searchField = form.findField( 'searchCount' );

            if ( this.modelData.selected.length > 0 ) {
                selectedField.setDisabled( false );
            } else {
                selectedField.setDisabled( true );
                searchField.setValue( true );
            }

            selectedField.el.down( 'span.count' ).dom.innerHTML = this.modelData.selected.length;
            searchField.el.down( 'span.count' ).dom.innerHTML = this.modelData.searched.count;
        }
    }

} );