/**
 * Main tab panel for admin apps. Extends Ext.tab.Panel with admin tabs functionality
 */

module app_ui {
    export class TabPanel {
        private ext;

        public getExtEl():any {
            return this.ext;
        }

        constructor(config:{
            appName:string;
            appIconCls:string;
        }) {
            this.ext = new Ext.tab.Panel({
                appName: config.appName,
                appIconCls: config.appIconCls,

                border: false,
                defaults: {
                    closable: true
                },
                /**
                 * Adds a tab to this panel
                 * @param {Object} item Item to be added in the tab panel's body
                 * @param {Number} index (optional)
                 * @param {Object} requestConfig (optional)
                 * @return {Ext.tab.Tab}
                 */
                addTab: function (item, index, requestConfig) {
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
                    var me:any = this,
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
                    var me:any = this;

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

                    // call method 'doRemove' from Ext.tab.Panel superclass
                    // casting to <any> to provide access to 'superclass' property without TypeScript warning
                    (<any> Ext.tab.Panel).superclass.doRemove.apply(this, arguments);
                },
                onRemove: function (item, destroying) {
                    var me:any = this;

                    item.un({
                        scope: me,
                        enable: me.onItemEnable,
                        disable: me.onItemDisable,
                        beforeshow: me.onItemBeforeShow
                    });

                    if (!me.destroying) {
                        me.tabBar.remove(item.tab);
                    }
                },

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

                    // Custom tabBar is why we needed to override this
                    this.tabBar = new app_ui.TopBar(me.appName, me);

                    dockedItems.push(this.tabBar.ext);
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

                }
            });


            app_event.EditSpaceEvent.on((event) => {
                var spaces = event.getModel();
                for (var i in spaces) {
                    var space = spaces[i];
                    console.log(space);
                    Admin.lib.RemoteService.space_get({
                        "spaceName": [space.get('name')]
                    }, (r) => {
                        this.ext.el.unmask();
                        if (r) {
                            var id = this.generateTabId(space, true);
                            var editing = true;
                            var title = <string> space.get('displayName');
                            var data = space;
                            var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel(id, title, editing, data);

                            //check if preview tab is open and close it
                            var index = this.ext.items.indexOfKey(this.generateTabId(space, false));
                            if (index >= 0) {
                                this.ext.remove(index);
                            }
                            this.addTab(spaceWizardPanel.ext, index >= 0 ? index : undefined, undefined);
                        } else {
                            console.error("Error", r ? r.error : "Unable to retrieve space.");
                        }
                    });
                }
            });

        }

        private generateTabId(space, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + space.get('name');
        }


        getTabCount() {
            return this.ext.getTabCount();
        }

        removeAllOpenTabs() {
            this.ext.removeAllOpenTabs();
        }

        addTab(item, index, requestConfig) {
            return this.ext.addTab(item, index, requestConfig);
        }

    }
}
