Ext.define('Admin.controller.homescreen.AppSelector', {
    extend: 'Admin.controller.homescreen.Controller',

    stores: [
        'Admin.store.homescreen.Apps'
    ],

    models: [
        'Admin.model.homescreen.Apps'
    ],

    views: [
        'Admin.view.homescreen.AppSelector'
    ],

    appSelectorKeyMap: undefined,
    currentTileIndex: -1,

    appTabCount: [],

    init: function () {
        var me = this;

        Admin.MessageBus.on('topBar.onUpdateAppTabCount', function (config) {
            /* For 18/4 */
            // TODO: Check if config exists
            me.appTabCount.push(config);

            me.updateTabCountInTile(config);
        }, me);


        me.application.on({
            displayAppSelector: this.display,
            scope: this
        });

        me.application.on({
            loadApplication: function () {
                me.currentTileIndex = -1;
                me.getAppSelectorView().clearHighlight();
                me.focusFilterTextField();
            },
            scope: this
        });

        me.control({
            '#appSelectorListView': {
                afterrender: function (view) {
                    me.registerAppSelectorKeyBindings();
                    view.getSearchTextField().on('change', me.onFilterTextFieldChange, me);
                },
                itemclick: function (view, record) {
                    me.openApp(record);
                },
                itemmouseenter: function (view, record) {
                    me.currentTileIndex = record.index;
                    me.updateAppInfoText(record);
                },
                itemmouseleave: function () {
                    me.currentTileIndex = -1;
                    me.updateAppInfoText(null);
                },
                refresh: function (view) {
                    me.refreshTabCountInTiles();
                }
            }
        });
    },


    display: function () {
        var me = this,
            loginElements = Ext.DomQuery.select('div[data-screen="login"]'),
            appSelectorElements = Ext.DomQuery.select('div[data-screen="app-selector"]'),
            appSelectorContainer = Ext.get('admin-home-app-selector');

        Ext.Array.forEach(loginElements, function (el) {
            Ext.fly(el).setStyle('display', 'none');
        });

        Ext.Array.forEach(appSelectorElements, function (el) {
            Ext.fly(el).setStyle('display', 'block');
        });

        me.focusFilterTextField();

        if (me.appSelectorKeyMap) {
            me.appSelectorKeyMap.enable();
        }

        appSelectorContainer.addCls('fade-in');
    },


    registerAppSelectorKeyBindings: function () {
        var me = this;
        me.appSelectorKeyMap = new Ext.util.KeyMap({
            // Make it global
            target: window,
            binding: [
                {
                    key: Ext.EventObject.TAB,
                    shift: false,
                    fn: function () {
                        me.highlightNextAppTile();
                    },
                    defaultEventAction: 'preventDefault'
                },
                {
                    key: Ext.EventObject.TAB,
                    shift: true,
                    fn: function () {
                        me.highlightPreviousApp();
                    },
                    defaultEventAction: 'preventDefault'
                },
                {
                    key: Ext.EventObject.ENTER,
                    fn: function () {
                        var appModel = me.getAppsStore().getAt(me.currentTileIndex);
                        me.openApp(appModel);
                    },
                    defaultEventAction: 'preventDefault'
                }
            ],
            scope: me
        });

        if (me.isUserLoggedIn()) {
            me.appSelectorKeyMap.enable();
        } else {
            me.appSelectorKeyMap.disable();
        }

    },


    highlightNextAppTile: function () {
        var me = this;
        if (me.currentTileIndex === me.getAppsStore().getCount() - 1) {
            me.currentTileIndex = -1;
        }
        me.currentTileIndex++;

        me.highlightAppTile(me.currentTileIndex);
    },


    highlightPreviousApp: function () {
        var me = this;
        var appsStore = me.getAppsStore();
        if (me.currentTileIndex === 0) {
            me.currentTileIndex = appsStore.getCount();
        }
        me.currentTileIndex--;

        me.highlightAppTile(me.currentTileIndex);
    },


    highlightAppTile: function (index) {
        var me = this;
        var appSelector = me.getAppSelectorView();
        var item = appSelector.getNode(index);

        appSelector.highlightItem(item);

        me.updateAppInfoText(me.getAppsStore().getAt(index));
    },


    onFilterTextFieldChange: function (textfield, newValue, oldValue) {
        var me = this;
        me.filterTiles(newValue);
        me.currentTileIndex = -1;
    },


    filterTiles: function (value) {
        var me = this,
            appsStore = me.getAppSelectorView().getStore(),
            valueLowerCased = value.toLowerCase();
        appsStore.clearFilter();
        appsStore.filterBy(function (item) {
            return item.get('name').toLowerCase().indexOf(valueLowerCased) > -1;
        });
    },


    focusFilterTextField: function () {
        Ext.getCmp('admin-home-app-selector-search').focus();
    },


    updateAppInfoText: function (appModel) {
        var name, description;
        if (appModel === null) {
            name = '';
            description = '';
        } else {
            var data = appModel.data;
            name = data.name;
            description = data.description;
        }

        Ext.fly('admin-home-app-info-name').setHTML(name);
        Ext.fly('admin-home-app-info-description').setHTML(description);
    },


    refreshTabCountInTiles: function () {
        var me = this;
        me.getAppsStore().each(function (record) {
            Ext.Array.each(me.appTabCount, function (app, i) {
                if (app.appId === record.raw.id) {
                    me.updateTabCountInTile(app);
                }
            });
        });
    },


    updateTabCountInTile: function (config) {
        var dom = Ext.dom.Query,
            tileNode = dom.selectNode('[data-tile-id=' + config.appId + ']');

        if (tileNode) {
            var countNode = dom.selectNode('.tab-count-container', tileNode),
                countElement = Ext.fly(countNode);

            countElement.setVisibilityMode(Ext.Element.DISPLAY);

            if (config.tabCount > 0) {
                countElement.setHTML(config.tabCount).show();
                countElement.set({title: config.tabCount + ' tab(s) open'});
            } else {
                countElement.setHTML(0).hide();
            }
        }
    }

});