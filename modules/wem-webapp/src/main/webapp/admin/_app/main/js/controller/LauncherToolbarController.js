Ext.define( 'App.controller.LauncherToolbarController', {
    extend: 'Ext.app.Controller',

    views: ['LauncherToolbar'],

    requires: [
        'App.LauncherToolbarHelper'
    ],

    init: function()
    {
        this.control(
                {
                    'viewport': {
                        afterrender: {
                            fn: this.loadDefaultApplication,
                            delay: 10
                        }
                    },
                    'launcherToolbar *[id=app-launcher-logo]': {
                        render: this.onLogoRendered
                    },
                    'launcherToolbar *[itemId=app-launcher-button] menu menuitem': {
                        click: this.loadApplication
                    }
                }
        );
    },

    loadDefaultApplication: function()
    {
        var defaultItem = this.getStartMenuButton().menu.down( 'menuitem[default=true]' );
        if ( defaultItem ) {
            this.loadApplication( defaultItem, null, null );
        }
    },

    loadApplication: function( selectedMenuItem, e, options )
    {
        var dom = Ext.DomQuery;
        var iframes = dom.select( 'iframe' );
        var iframeExist = false;
        Ext.each( iframes, function( iframe, index, allIFrames )
        {
            if ( iframe.id === 'iframe-' + selectedMenuItem.id ) {
                iframeExist = true;
                iframe.style.display = 'block';
            }
            else {
                iframe.style.display = 'none';
            }
        } );

        if ( !iframeExist ) {
            this.appendIframe( selectedMenuItem );
            this.showLoadMask();
        }

        this.setDocumentTitle( selectedMenuItem.text );
        this.updateStartButton( selectedMenuItem );
    },

    appendIframe: function( selectedMenuItem )
    {
        Ext.core.DomHelper.append( 'app-frames',
                {
                    tag: 'iframe',
                    src: selectedMenuItem.cms.appUrl,
                    id: 'iframe-' + selectedMenuItem.initialConfig.id,
                    style: 'width: 100%; height: 100%; border: 0'
                }
        );
    },

    updateStartButton: function( selectedMenuItem )
    {
        var startMenuButton = this.getStartMenuButton();
        startMenuButton.setText( selectedMenuItem.text );
        startMenuButton.setIconCls( selectedMenuItem.iconCls );
    },

    setDocumentTitle: function( title )
    {
        window.document.title = 'Enonic CMS Admin - ' + title;
    },

    showLoadMask: function()
    {
        if ( !window.appLoadMask ) {
            window.appLoadMask = new Ext.LoadMask( Ext.getDom( 'main-viewport-center' ), {
                floating: {
                    shadow: false
                }
            } );
        }

        window.appLoadMask.show();
    },


    onLogoRendered: function( component, options )
    {
        component.el.on( 'click', this.showAboutWindow );
    },

    getStartMenuButton: function()
    {
        return Ext.ComponentQuery.query( 'launcherToolbar button[itemId=app-launcher-button]' )[0];
    },

    showAboutWindow: function()
    {
        var aboutWindow = Ext.ComponentQuery.query( '#admin-about-window' )[0];
        if ( aboutWindow ) {
            aboutWindow.show();
            return;
        }

        Ext.create( 'Ext.window.Window', {
            itemId: 'admin-about-window',
            modal: true,
            resizable: false,
            title: 'About',
            width: 550,
            height: 300
        } ).show();
    }

} );
