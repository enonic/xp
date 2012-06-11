/**
 * Main tab panel for admin apps. Extends Ext.tab.Panel with admin tabs functionality
 */
Ext.define( 'Admin.view.TabPanel', {
    extend: 'Ext.tab.Panel',
    // TODO: Refactor "cmsTabPanel" -> "adminTabPanel"
    alias: 'widget.cmsTabPanel',
    requires: ['Admin.plugin.TabCloseMenu'],
    defaults: { closable: true },
    plugins: ['tabCloseMenu'],

    initComponent: function()
    {
        this.callParent( arguments );
    },

    /**
     * Adds a tab to this panel
     * @param {Object} item Item to be added in the tab panel's body
     * @param {Number} index (optional)
     * @param {Object} requestConfig (optional)
     * @return {Ext.tab.Tab}
     */

    addTab: function( item, index, requestConfig )
    {
        var tabPanel = this;
        var tab = this.getTabById( item.id );
        // Create a new tab if it has not been created
        if ( !tab ) {
            tab = this.insert( index || this.items.length, item );
            if ( requestConfig ) {
                this.setActiveTab( tab );
                var mask = new Ext.LoadMask( tab, {msg: "Please wait..."} );
                mask.show();
                var createTabFromResponse = requestConfig.createTabFromResponse;
                requestConfig.success = function successCallback( response )
                {
                    var tabContent = createTabFromResponse( response );
                    tab.add( tabContent );
                    mask.hide();
                    // There is a need to call doLayout manually, since it isn't called for background tabs
                    // after content was added
                    tab.on( 'activate', function()
                    {
                        this.doLayout();
                    }, tab, {single: true} );
                };
                Ext.Ajax.request( requestConfig );
            }
            if ( tab.closable ) {
                tab.on( {
                    beforeclose: function( tab )
                    {
                        tabPanel.onBeforeCloseTab( tab )
                    }
                } );
            }
        }
        // TODO: tab.hideMode: Ext.isIE ? 'offsets' : 'display'
        // is this a real problem?
        this.setActiveTab( tab );
        return tab;
    },

    /**
     * Returns a tab by the given id
     * @param {String} id
     * @return {Ext.tab.Tab}
     */

    getTabById: function( id )
    {
        return this.getComponent( id );
    },

    /**
     * Removes all open tabs. The first tab will not be removed.
     */

    removeAllOpenTabs: function()
    {
        var all = this.items.items;
        var last = all[this.getTabCount() - 1 ];
        while ( this.getTabCount() > 1 ) {
            this.remove( last );
            last = this.items.items[this.getTabCount() - 1];
        }
    },

    /**
     * Returns the number of opened tabs.
     * @return {Number}
     */

    getTabCount: function()
    {
        return this.items.items.length;
    },

    /**
     * Fired before a tab is closed.
     * Resolves which tab to set active when the given tab is closed.
     * If the tab to be closed is visible the previous tab is activated.
     * @private
     * @param {Ext.tab.Tab} tab The tab to close.
     */

    onBeforeCloseTab: function( tab )
    {
        var tabToActivate = null;

        if ( tab.isVisible() ) {
            var tabIndex = this.items.findIndex( 'id', tab.id );
            tabToActivate = this.items.items[ tabIndex - 1 ];
        }
        else {
            // Keep visible tab activated.
            tabToActivate = this.getActiveTab();
        }

        this.setActiveTab( tabToActivate );
    }

} );
