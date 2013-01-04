Ext.define('Admin.view.TopBar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.topBar',

    requires: [
        'Admin.view.TopBarMenu',
        'Admin.view.StartMenu',
        'Admin.view.AdminImageButton'
    ],

    buttonAlign: 'center',
    cls: 'admin-topbar-panel',

    dock: 'top',
    plain: true,
    border: false,

    initComponent: function () {
        var me = this;

        this.startMenu = Ext.create('Admin.view.StartMenu', {
            xtype: 'startMenu',
            renderTo: Ext.getBody(),
            listeners: {
                login: function (cmp) {
                    cmp.loggedUser = {
                        img: '../html-templates/images/profile-image.png'
                    };
                },
                logout: function (cmp) {
                    delete cmp.loggedUser;
                }
            },
            items: [
                {
                    id: 'app-01',
                    title: 'Content Manager',
                    cls: 'span2 start-row content-manager',
                    iconCls: 'icon-metro-content-manager-24',
                    appUrl: 'app-content-manager.jsp'
                },
                {
                    id: 'app-02',
                    title: 'Dashboard',
                    cls: 'span2 dashboard',
                    iconCls: 'icon-metro-dashboard-24',
                    appUrl: 'app-dashboard.jsp',
                    defaultApp: true
                },
                {
                    id: 'app-03',
                    title: 'Profile',
                    cls: 'profile title-top title-mask',
                    img: '../dev/html-templates/images/profile-image.png',
                    appUrl: 'blank.html',
                    contentTpl: new Ext.XTemplate('<img src="{img}" alt=""/>')
                },
                {
                    id: 'app-04',
                    title: 'Activity Stream',
                    cls: 'span2 activity title-top title-right',
                    appUrl: 'blank.html',
                    posts: [
                        {
                            img: '../dev/html-templates/images/profile-image.png',
                            text: 'Hello there !'
                        },
                        {
                            img: '../dev/html-templates/images/profile-image.png',
                            text: 'What do you think about this new tile based start menu ?'
                        },
                        {
                            img: '../dev/html-templates/images/profile-image.png',
                            text: 'Huh ?'
                        }
                    ],
                    contentTpl: new Ext.XTemplate('<tpl for="posts"><div class="item">' +
                                                  '<img src="{img}"/><p>{text}</p></div></tpl>')
                },
                {
                    id: 'app-05',
                    title: 'Cluster',
                    appUrl: 'blank.html',
                    iconCls: "icon-metro-cluster-24",
                    cls: 'cluster'
                },
                {
                    id: 'app-06',
                    title: 'Userstores',
                    appUrl: 'app-userstore.jsp',
                    iconCls: "icon-metro-userstores-24",
                    cls: 'userstores start-row'
                },
                {
                    id: 'app-07',
                    title: 'Accounts',
                    appUrl: 'app-account.jsp',
                    iconCls: "icon-metro-accounts-24",
                    cls: 'accounts'
                },
                {
                    id: 'app-08',
                    title: 'Site Administrator',
                    appUrl: 'blank.html',
                    iconCls: 'icon-metro-site-admin-24',
                    cls: 'span2 administrators'
                },
                {
                    id: 'app-09',
                    title: 'Content Studio',
                    appUrl: 'app-content-studio.jsp',
                    iconCls: 'icon-metro-content-studio-24',
                    cls: 'content-studio'
                },
                {
                    id: 'app-10',
                    title: 'Live Trace',
                    appUrl: 'blank.html',
                    iconCls: "icon-metro-live-trace-24",
                    cls: 'span2 live-trace title-top title-mask'
                },
                {
                    id: 'app-11',
                    title: 'Schedule',
                    appUrl: 'blank.html',
                    iconCls: "icon-metro-schedule-24",
                    cls: 'schedule'
                }
            ]
        });
        this.startButton = Ext.create('Ext.button.Button', {
            xtype: 'button',
            itemId: 'app-launcher-button',
            split: true,
            cls: 'start-button',
            iconCls: me.appIconCls,
            text: me.appName || '&lt; app name &gt;',
            handler: function (btn, evt) {
                me.startMenu.slideToggle();
            }
        });

        this.leftContainer = Ext.create('Ext.Container', {
            flex: 5,
            margins: '0 8px',
            layout: 'hbox'
        });
        this.titleText = Ext.create('Ext.form.Label');
        this.rightContainer = Ext.create('Ext.Container', {
            flex: 5,
            margins: '0 8px',
            layout: {
                type: 'hbox',
                align: 'middle',
                pack: 'end'
            }
        });

        this.items = [
            me.startButton,
            me.leftContainer,
            me.titleText,
            me.rightContainer,
            {
                xtype: 'adminImageButton',
                icon: "rest/account/image/default/user",
                popupTpl: Templates.common.userPopUp,
                popupData: {
                    userName: "Thomas Lund Sigdestad",
                    photoUrl: "rest/account/image/default/user",
                    qName: 'system/tsi'
                }
            }
        ];

        if (this.tabPanel) {
            this.tabMenu = Ext.create('Admin.view.TopBarMenu', {
                tabPanel: me.tabPanel
            });
            this.titleButton = Ext.create('Ext.button.Button', {
                cls: 'title-button',
                menuAlign: 't-b?',
                menu: me.tabMenu,
                text: 'Title'
            });
            Ext.Array.insert(me.items, 3, [me.titleButton]);
        }

        this.callParent(arguments);
        this.syncTabCount();
    },


    /*  Methods for Admin.view.TabPanel integration */

    insert: function (index, cfg) {
        var added = this.tabMenu.addItems(cfg);
        this.syncTabCount();

        return added.length === 1 ? added[0] : added;
    },

    setActiveTab: function (tab) {
        this.setTitle(this.getMenuItemDescription(tab.card));
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
        var data = item.data || item;
        return {
            tabBar: me,
            card: item,
            disabled: cfg.disabled,
            closable: cfg.closable,
            hidden: cfg.hidden && !item.hiddenByLayout, // only hide if it wasn't hidden by the layout itself
            iconCls: cfg.iconCls,
            iconSrc: data.image_url,
            width: 270,
            editing: cfg.editing || false,
            text1: Ext.String.ellipsis(cfg.title || 'first line', 24),  // The length of 'Userstore Administrators'
            text2: Ext.String.ellipsis(me.getMenuItemDescription(item), 34)
        };
    },


    /*  Private */

    syncTabCount: function () {
        if (this.tabMenu && this.titleButton) {
            var tabCount = this.tabMenu.getAllItems(false).length;
            // show dropdown button when any tab is open or text when nothing is open
            this.titleButton.setVisible(tabCount > 0);
            this.titleText.setVisible(tabCount == 0);
        }
    },

    getMenuItemDescription: function (card) {
        var desc;
        if (card.data) {
            var data = card.data;
            desc = data.path || data.qualifiedName || data.displayName;
            if (!desc && data.content) {
                var content = data.content;
                desc = content.path || content.qualifiedName || content.displayName;
            }
        }
        if (!desc) {
            desc = card.title;
        }
        return desc;
    },


    /*  Public  */

    setTitle: function (text) {
        // highlight the last path fragment
        var title = text;
        var lastSlash = !Ext.isEmpty(text) ? text.lastIndexOf('/') : -1;
        if (lastSlash > -1) {
            title = text.substring(0, lastSlash + 1) + '<strong>' + text.substring(lastSlash + 1) + '</strong>';
        }
        this.titleText.setText(title);
        this.titleButton.setText(title);
    },

    getStartButton: function () {
        return this.startButton;
    },

    getStartMenu: function () {
        return this.startMenu;
    },

    getLeftContainer: function () {
        return this.leftContainer;
    },

    getRightContainer: function () {
        return this.rightContainer;
    }
});
