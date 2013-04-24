Ext.define('Admin.view.TopBar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.topBar',

    requires: [
        'Admin.view.TopBarMenu',
        'Admin.view.AdminImageButton',
        'Admin.lib.UriHelper'
    ],

    buttonAlign: 'center',
    cls: 'admin-topbar-panel',

    dock: 'top',
    plain: true,
    border: false,

    initComponent: function () {
        var me = this;

        this.startButton = Ext.create('Ext.button.Button', {
            xtype: 'button',
            itemId: 'app-launcher-button',
            margins: '0 8px 0 0',
            cls: 'start-button',
            handler: function (btn, evt) {
                me.toggleHomeScreen();
            }
        });

        this.homeButton = Ext.create('Ext.button.Button', {
            text: me.appName || '&lt; app name &gt;',
            cls: 'home-button',
            handler: function (btn, evt) {
                if (me.tabPanel) {
                    me.tabPanel.setActiveTab(0);
                }
            }
        });

        this.leftContainer = Ext.create('Ext.Container', {
            flex: 5,
            padding: 6,
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            items: [
                me.startButton,
                {
                    xtype: "tbseparator",
                    width: '2px'
                },
                me.homeButton
            ]
        });
        this.rightContainer = Ext.create('Ext.Container', {
            flex: 5,
            layout: {
                type: 'hbox',
                align: 'middle',
                pack: 'end'
            },
            items: [
                {
                    xtype: 'adminImageButton',
                    icon: Admin.lib.UriHelper.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                    popupTpl: Templates.common.userPopUp,
                    popupData: {
                        userName: "Thomas Lund Sigdestad",
                        photoUrl: Admin.lib.UriHelper.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                        qName: 'system/tsi'
                    }
                }
            ]
        });

        this.items = [
            me.leftContainer,
            me.rightContainer
        ];

        if (this.tabPanel) {
            this.tabMenu = Ext.create('Admin.view.TopBarMenu', {
                tabPanel: me.tabPanel
            });
            this.titleButton = Ext.create('Ext.button.Button', {
                cls: 'title-button',
                menuAlign: 't-b?',
                menu: me.tabMenu,
                scale: 'medium',
                styleHtmlContent: true,
                text: '<span class="title">Title</span><span class="count">0</span>',

                setTitle: function (title) {
                    // update title only, without changing count
                    if (this.el) {
                        this.el.down('.title').setHTML(title);
                    }
                },

                setCount: function (count) {
                    // update count number only, without changing title
                    if (this.el) {
                        this.el.down('.count').setHTML(count);
                    }
                }
            });
            Ext.Array.insert(me.items, 1, [me.titleButton]);
        }

        this.callParent(arguments);
        this.syncTabCount();
    },


    toggleHomeScreen: function () {
        var isInsideIframe = window.top !== window.self;
        if (isInsideIframe) {
            window.parent.Ext.getCmp('admin-home-main-container').toggleShowHide();
        } else {
            console.error('Can not toggle home screen. Document must be loaded inside the main window');
        }
    },


    /*  Methods for Admin.view.TabPanel integration */

    insert: function (index, cfg) {
        var added = this.tabMenu.addItems(cfg);
        this.syncTabCount();

        return added.length === 1 ? added[0] : added;
    },

    setActiveTab: function (tab) {
        this.tabMenu.markActiveTab(tab);

        var card = tab.card;
        var buttonText = tab.text1;
        var iconClass;

        if ('tab-browse' === card.id) {
            buttonText = '';
        } else if (card.tab.iconClass) {
            iconClass = card.tab.iconClass;
        } else if (card.tab.editing) {
            iconClass = 'icon-icomoon-pencil-32';
        }

        this.titleButton.setIconCls(iconClass);
        this.setTitleButtonText(buttonText);
    },

    remove: function (tab) {

        var removed = this.tabMenu.removeItems(tab);
        this.syncTabCount();

        return removed;
    },

    findNextActivatable: function (tab) {
        if (this.tabPanel) {
            // set first browse tab active
            return this.tabPanel.items.get(0);
        }
    },

    createMenuItemFromTab: function (item) {
        var me = this;
        var cfg = item.initialConfig || item;
        return {
            tabBar: me,
            card: item,
            disabled: cfg.disabled,
            closable: cfg.closable,
            hidden: cfg.hidden && !item.hiddenByLayout, // only hide if it wasn't hidden by the layout itself
            iconSrc: me.getMenuItemIcon(item),
            iconClass: cfg.iconClass,
            editing: cfg.editing || false,
            text1: Ext.String.ellipsis(me.getMenuItemDisplayName(item), 26),
            text2: Ext.String.ellipsis(me.getMenuItemDescription(item), 38)
        };
    },


    /*  Private */

    syncTabCount: function () {
        if (this.tabMenu && this.titleButton) {
            var tabCount = this.tabMenu.getAllItems(false).length;
            // show dropdown button when any tab is open or text when nothing is open
            this.titleButton.setVisible(tabCount > 0);
            this.titleButton.setCount(tabCount);

            Admin.MessageBus.updateAppTabCount({
                appId: this.getApplicationId(),
                tabCount: tabCount
            });
        }
    },

    /* For 18/4 demo */
    getApplicationId: function () {
        var urlParamsString = document.URL.split('?'),
            urlParams = Ext.urlDecode(urlParamsString[urlParamsString.length - 1]);

        return urlParams.appId ? urlParams.appId.split('#')[0] : null;
    },

    getMenuItemIcon: function (card) {
        var icon;
        if (card.data && card.data instanceof Ext.data.Model) {
            icon = card.data.get('iconUrl') || card.data.get('image_url');
        }
        return icon;
    },

    getMenuItemDescription: function (card) {
        var desc;
        if (!card.isNew && card.data && card.data instanceof Ext.data.Model) {
            desc = card.data.get('path') || card.data.get('qualifiedName') || card.data.get('displayName');
        }
        if (!desc) {
            // fall back to card title
            desc = card.title;
        }
        return desc;
    },

    getMenuItemDisplayName: function (card) {
        var desc;
        if (!card.isNew && card.data && card.data instanceof Ext.data.Model) {
            desc = card.data.get('displayName') || card.data.get('name');
        }
        if (!desc) {
            // fall back to card title
            desc = card.title;
        }
        return desc;
    },


    /*  Public  */

    setTitleButtonText: function (text) {
        this.titleButton.setTitle(text);
        var activeTab = this.titleButton.menu.activeTab;

        if (activeTab) {
            activeTab.text1 = text;
            activeTab.updateTitleContainer();
        }
    },

    getStartButton: function () {
        return this.startButton;
    },

    getLeftContainer: function () {
        return this.leftContainer;
    },

    getRightContainer: function () {
        return this.rightContainer;
    }
});
