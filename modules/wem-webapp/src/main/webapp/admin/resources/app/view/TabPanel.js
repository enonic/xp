/**
 * Main tab panel for admin apps. Extends Ext.tab.Panel with admin tabs functionality
 */
Ext.define('Admin.view.TabPanel', {
    extend: 'Ext.tab.Panel',
    // TODO: Refactor "cmsTabPanel" -> "adminTabPanel"
    alias: 'widget.cmsTabPanel',

    requires: [
        'Admin.plugin.TabCloseMenu',
        'Admin.view.TopBar'
    ],
    plugins: ['tabCloseMenu'],

    border: false,
    defaults: { closable: true },

    initComponent: function () {
        var me = this;

        this.callParent(arguments);

        // update default tab bar with our own
        this.removeDocked(this.tabBar, true);

        this.tabBar = Ext.create('Admin.view.TopBar', {
            appName: me.appName,
            appIconCls: me.appIconCls,
            tabPanel: me
        });
        this.addDocked(this.tabBar);
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
            cfg = item.tabConfig || {},
            defaultConfig = {
                tabBar: me.tabBar,
                card: item,
                disabled: item.disabled,
                closable: item.closable,
                hidden: item.hidden && !item.hiddenByLayout, // only hide if it wasn't hidden by the layout itself
                iconCls: item.iconCls || 'icon-data-blue',
                editing: item.editing || false,
                text1: item.title || 'first line',
                text2: item.type || 'second line'
            };


        cfg = Ext.applyIf(cfg, defaultConfig);

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

        // Destroying, or removing the last item, nothing to activate
        if (me.destroying || me.items.getCount() === 1) {
            me.activeTab = null;
        } else if (me.activeTab === item) {
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
