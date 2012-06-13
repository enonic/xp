/**
 * Base controller for content manager
 */
Ext.define( 'Admin.controller.contentManager.Controller', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for the content manager module      */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.DeleteContentWindow'
    ],


    init: function()
    {

        this.control( {} );

        this.application.on( {} );

    },

    viewContent: function( content, callback )
    {
        if ( !content ) {
            var showPanel = this.getContentShowPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat( content );
        }

        var tabs = this.getCmsTabPanel();
        if ( tabs ) {
            for ( var i = 0; i < content.length; i++ ) {
                tabs.addTab( {
                    xtype: 'contentDetail',
                    data: content[ i ],
                    title: 'View Content'
                } );
            }
        }
    },

    editContent: function( content, callback )
    {
        if ( !content ) {
            var showPanel = this.getContentShowPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat( content );
        }

        var tabs = this.getCmsTabPanel();
        if ( tabs ) {
            var tab;
            for ( var i = 0; i < content.length; i++ ) {
                var data = content[ i ];
                switch ( data.get( 'type' ) ) {
                    case 'contentType':
                        tab = {
                            xtype: 'contentWizardPanel',
                            title: 'New Content',
                            data: data
                        };
                        break;
                    case 'site':
                        tab = {
                            xtype: 'panel',
                            title: 'New Site',
                            html: 'Site wizard will be here',
                            data: data
                        };
                        break;
                }
                tabs.addTab( tab );
            }
        }
    },

    createContent: function( type )
    {
        var tabs = this.getCmsTabPanel();
        if ( tabs ) {
            var tab;
            switch ( type ) {
                case 'contentType':
                    tab = {
                        xtype: 'contentWizardPanel',
                        title: 'New Content'
                    };
                    break;
                case 'site':
                    tab = {
                        xtype: 'panel',
                        html: 'New site wizard here',
                        title: 'New Site'
                    };
                    break;
            }
            tabs.addTab( tab );
        }
    },

    deleteContent: function( content )
    {
        if ( !content ) {
            var showPanel = this.getContentShowPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat( content );
        }

        if ( content && content.length > 0 ) {
            this.getDeleteContentWindow().doShow( content );
        }
    },

    duplicateContent: function( content )
    {
        if ( !content ) {
            var showPanel = this.getContentShowPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat( content );
        }

        var selection = content[0];
        var parentApp = parent.mainApp;
        if ( parentApp && selection ) {
            parentApp.fireEvent( 'notifier.show', selection.get( 'name' ) + ' duplicated into /path/to/content-copy',
                    'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                    true );
        }
    },

    /*      Getters     */

    getContentFilter: function()
    {
        return Ext.ComponentQuery.query( 'contentFilter' )[0];
    },

    getContentShowPanel: function()
    {
        return Ext.ComponentQuery.query( 'contentShow' ) [0];
    },

    getContentGridPanel: function()
    {
        return this.getContentShowPanel().down( 'grid' );
    },

    getContentTreePanel: function()
    {
        return this.getContentShowPanel().down( 'tree' );
    },

    getContentDetailPanel: function()
    {
        return Ext.ComponentQuery.query( 'contentDetail' )[0];
    },

    getPersistentGridSelectionPlugin: function()
    {
        return this.getContentGridPanel().getPlugin( 'persistentGridSelection' );
    },

    getDeleteContentWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'deleteContentWindow' )[0];
        if ( !win ) {
            win = Ext.create( 'widget.deleteContentWindow' );
        }
        return win;
    }

} );
