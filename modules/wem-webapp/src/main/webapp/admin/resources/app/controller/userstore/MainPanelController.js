Ext.define( 'Admin.controller.userstore.MainPanelController', {
    extend:'Admin.controller.userstore.Controller',

    stores:[
        'Admin.store.userstore.UserstoreConfigStore',
        'Admin.store.userstore.UserstoreConnectorStore'
    ],
    models:[
        'Admin.model.userstore.UserstoreConfigModel',
        'Admin.model.userstore.UserstoreConnectorModel'
    ],

    views:[
        'Admin.view.TabPanel',
        'Admin.view.userstore.MainPanel',
        'Admin.view.userstore.BrowseToolbar',
        'Admin.view.userstore.wizard.UserstoreWizardPanel'
    ],

    init:function ()
    {
        this.control( {
            'browseToolbar *[action=newUserstore]':{
                'click':this.createUserstoreTab
            }
        } );
        // Add events to application scope.
        this.application.on( {
            newUserstore:{
                fn:this.createUserstoreTab,
                scope:this

            },
            editUserstore:{
                fn:this.createUserstoreTab,
                scope:this
            },
            viewUserstore:{
                fn:this.viewUserstore,
                scope:this
            },
            closeUserstoreTab:{
                fn:this.closeUserstoreTab,
                scope:this
            }
        } );

    },

    createUserstoreTab:function ( userstore, forceNew )
    {
        var tabs = this.getTabs();
        if ( tabs ) {
            if ( !forceNew && userstore ) {

                var showPanel = this.getMainPanel();

                showPanel.el.mask( "Loading..." );

                Ext.Ajax.request( {
                    url:'data/userstore/config',
                    method:'GET',
                    params:{
                        name:userstore.name
                    },
                    success:function ( response )
                    {
                        var obj = Ext.decode( response.responseText, true );
                        // add missing fields for now
                        Ext.apply( obj, {
                            userCount:231,
                            userPolicy:'User Policy',
                            groupCount:12,
                            groupPolicy:'Group Policy',
                            lastModified:'2001-07-04 12:08:56',
                            plugin:'Plugin Name'
                        } );
                        showPanel.el.unmask();
                        tabs.addTab( {
                            xtype:'userstoreWizardPanel',
                            id:'tab-userstore-' + userstore.key,
                            title:userstore.name,
                            modelData:obj
                        } );
                    }
                } );
            } else {
                tabs.addTab( {
                    xtype:'userstoreWizardPanel',
                    title:'New Userstore'
                } );
            }
        }
    },

    closeUserstoreTab:function ( button, e, eOpts )
    {
        var tabs = this.getTabs();
        if ( tabs ) {
            var tab = button.up( 'userstoreFormPanel' );
            tabs.remove( tab, true );
        }
    },

    viewUserstore: function( userstore )
    {
        var tabs = this.getTabs();
        if ( tabs ) {
            var previewTab = tabs.addTab( {
                xtype: 'userstorePreviewPanel',
                tbar: {
                    xtype: 'userstorePreviewToolbar'
                },
                title: userstore.name
            } );
            previewTab.setData( userstore );
        }
    },


    getTabs:function ()
    {
        // returns tabs if executed in the system scope
        var tabs = this.getCmsTabPanel();
        // returns tabs if executed inside the iframe of the system app
        if ( tabs == null && window.parent ) {
            tabs = window.parent.Ext.getCmp( 'systemTabPanelID' );
        }
        return tabs;
    }

} );
