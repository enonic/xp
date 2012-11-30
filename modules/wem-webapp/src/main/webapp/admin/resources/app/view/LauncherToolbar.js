Ext.define('Admin.view.LauncherToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.launcherToolbar',

    requires: [
        'Admin.view.TopBarMenu',
        'Admin.view.StartMenu'
    ],

    buttonAlign: 'center',
    border: false,
    cls: 'admin-topbar-panel',

    initComponent: function () {
        var me = this;

        this.tabMenu = Ext.create('Admin.view.TopBarMenu');

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
                    iconCls: 'icon-content-manager-24',
                    appUrl: 'app-content-manager.jsp'
                },
                {
                    id: 'app-02',
                    title: 'Dashboard',
                    cls: 'span2 dashboard',
                    iconCls: 'icon-dashboard-24',
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
                    iconCls: "icon-cluster-24",
                    cls: 'cluster'
                },
                {
                    id: 'app-06',
                    title: 'Userstores',
                    appUrl: 'app-userstore.jsp',
                    iconCls: "icon-userstore-alt-24",
                    cls: 'userstores start-row'
                },
                {
                    id: 'app-07',
                    title: 'Accounts',
                    appUrl: 'app-account.jsp',
                    iconCls: "icon-woman-24",
                    cls: 'accounts'
                },
                {
                    id: 'app-08',
                    title: 'Site Administrator',
                    appUrl: 'blank.html',
                    cls: 'span2 administrators'
                },
                {
                    id: 'app-09',
                    title: 'Content Studio',
                    appUrl: 'app-content-studio.jsp',
                    iconCls: 'icon-content-studio-24',
                    cls: 'content-studio'
                },
                {
                    id: 'app-10',
                    title: 'Live Trace',
                    appUrl: 'blank.html',
                    iconCls: "icon-live-trace-24",
                    cls: 'span2 live-trace title-top title-mask'
                },
                {
                    id: 'app-11',
                    title: 'Schedule',
                    appUrl: 'blank.html',
                    iconCls: "icon-gears-run-24",
                    cls: 'schedule'
                }
            ]
        });

        this.startButton = Ext.create('Ext.button.Button', {
            xtype: 'button',
            itemId: 'app-launcher-button',
            cls: 'start-button',
            iconCls: 'icon-data-white',
            text: 'Content Manager',
            handler: function (btn, evt) {
                me.startMenu.slideToggle();
            }
        });

        this.items = [
            me.startButton,
            {
                text: '0',
                cls: 'white',
                menuAlign: 't-b?',
                menu: me.tabMenu
            },
            { xtype: 'tbspacer', flex: 5 },
            'sitename/path/to/press-releases',
            { xtype: 'tbspacer', flex: 5 },
            {
                text: 'Log in',
                cls: 'red',
                margins: '0 6 0 0'
            },
            {
                text: 'Settings',
                cls: 'blue'
            }
        ];

        this.callParent(arguments);
    },

    getStartButton: function () {
        return this.startButton;
    },

    getStartMenu: function () {
        return this.startMenu;
    },

    getTabMenu: function () {
        return this.tabMenu;
    }
});
