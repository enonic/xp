/**
 * Main tab panel for admin apps. Extends Ext.tab.Panel with admin tabs functionality
 */
Ext.define('Admin.view.TabPanel', {
    extend: 'Ext.tab.Panel',
    // TODO: Refactor "cmsTabPanel" -> "adminTabPanel"
    alias: 'widget.cmsTabPanel',

    requires: [
        'Admin.view.TopBar'
    ],

    border: false,
    defaults: { closable: true },

    initComponent: function () {

        var me = this,
            dockedItems = [].concat(me.dockedItems || []),
            activeTab = me.activeTab || (me.activeTab = 0);

        // Configure the layout with our deferredRender, and with our activeTeb
        me.layout = new Ext.layout.container.Card(Ext.apply({
            owner: me,
            deferredRender: me.deferredRender,
            itemCls: me.itemCls,
            activeItem: me.activeTab
        }, me.layout));

        // New TopBar implementation. Uncomment when the time comes
        // this.appbar = new app.ContentAppBar();
        // dockedItems.push(me.appbar.ext);

        // Custom tabBar is why we needed to override this
        this.tabBar = <any> Ext.create('Admin.view.TopBar', Ext.apply({
            appName: me.appName,
            appIconCls: me.appIconCls,
            tabPanel: me
        }, me.tabBar));

        dockedItems.push(me.tabBar);
        me.dockedItems = dockedItems;

        me.addEvents('beforetabchange', 'tabchange');

        // first super is TabPanel, which we want to bypass
        me.superclass.superclass.initComponent.apply(me, arguments);

        // We have to convert the numeric index/string ID config into its component reference
        me.activeTab = me.getComponent(activeTab);

        // Ensure that the active child's tab is rendered in the active UI state
        if (me.activeTab) {
            me.activeTab.tab.activate(true);

            me.tabBar.setActiveTab(me.activeTab.tab);
        }


    },

    /**
     * Adds a tab to this panel
     * @param {Object} item Item to be added in the tab panel's body
     * @param {Number} index (optional)
     * @param {Object} requestConfig (optional)
     * @return {Ext.tab.Tab}
     */

    addTab: function (item, index, requestConfig) {
        var me = this;
        var tab = this.getTabById(item.id);
        // Create a new tab if it has not been created
        if (!tab) {
            tab = this.insert(index || this.items.length, item);
            if (requestConfig) {
                // activate to make changes to it
                this.setActiveTab(tab);
                var mask = new Ext.LoadMask(tab, {msg: "Please wait..."});
                mask.show();
                var createTabFromResponse = requestConfig.createTabFromResponse;
                var onRequestConfigSuccess = function (response) {
                    var tabContent = createTabFromResponse(response);
                    tab.add(tabContent);
                    mask.hide();
                    // There is a need to call doLayout manually, since it isn't called for background tabs
                    // after content was added
                    tab.on('activate', function () {
                        this.doLayout();
                    }, tab, {single: true});
                };
                requestConfig.doTabRequest(onRequestConfigSuccess);
            }
        }
        // TODO: tab.hideMode: Ext.isIE ? 'offsets' : 'display'
        // is this a real problem?
        this.setActiveTab(tab);
        return tab;
    },

    /**
     * Returns a tab by the given id
     * @param {String} id
     * @return {Ext.tab.Tab}
     */

    getTabById: function (id) {
        return this.getComponent(id);
    },

    /**
     * Removes all open tabs. The first tab will not be removed.
     */

    removeAllOpenTabs: function () {
        var all = this.items.items;
        var last = all[this.getTabCount() - 1];
        while (this.getTabCount() > 1) {
            this.remove(last);
            last = this.items.items[this.getTabCount() - 1];
        }
    },

    /**
     * Returns the number of opened tabs.
     * @return {Number}
     */

    getTabCount: function () {
        return this.items.items.length;
    },


    onAdd: function (item, index) {
        var me = this,
            cfg = item.tabConfig || {};

        cfg = Ext.applyIf(cfg, me.tabBar.createMenuItemFromTab(item));

        // Create the correspondiong tab in the tab bar
        item.tab = me.tabBar.insert(index, cfg);

        item.on({
            scope: me,
            enable: me.onItemEnable,
            disable: me.onItemDisable,
            beforeshow: me.onItemBeforeShow,
            iconchange: me.onItemIconChange,
            iconclschange: me.onItemIconClsChange,
            titlechange: me.onItemTitleChange
        });

        if (item.isPanel) {
            if (me.removePanelHeader) {
                if (item.rendered) {
                    if (item.header) {
                        item.header.hide();
                    }
                } else {
                    item.header = false;
                }
            }
            if (item.isPanel && me.border) {
                item.setBorder(false);
            }
        }
    },

    doRemove: function (item, autoDestroy) {
        var me = this;

        if (me.destroying || me.items.getCount() === 1) {
            // Destroying, or removing the last item, nothing to activate
            me.activeTab = null;
        } else if (me.activeTab === item) {
            // Removing currently active item, find next to activate
            var toActivate = me.tabBar.findNextActivatable(item.tab);
            if (toActivate) {
                me.setActiveTab(toActivate);
            }
        }

        this.callParent(arguments);
    },

    onRemove: function (item, destroying) {
        var me = this;

        item.un({
            scope: me,
            enable: me.onItemEnable,
            disable: me.onItemDisable,
            beforeshow: me.onItemBeforeShow
        });

        if (!me.destroying) {
            me.tabBar.remove(item.tab);
        }
    }

});
