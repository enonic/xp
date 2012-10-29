Ext.define('Admin.view.TopBarPanel', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.topBarPanel',

    requires: [
        'Admin.view.TopBarMenu',
        'Admin.view.StartMenu'
    ],

    buttonAlign: 'center',
    border: false,
    cls: 'admin-topbar-panel',

    initComponent: function () {
        var topBarMenu = Ext.create('Admin.view.TopBarMenu', {
            listeners: {
                afterrender: function (menu) {
                    menu.addItems([
                        {
                            id: 'item-1',
                            iconCls: 'icon-data-blue',
                            text1: 'Two',
                            editing: true,
                            text2: 'Just two'
                        },
                        {
                            id: 'item-2',
                            checked: true,
                            iconCls: 'icon-data-blue',
                            text1: 'Three',
                            text2: 'With some very long explanation'
                        },
                        {
                            id: 'item-3',
                            iconCls: 'icon-data-blue',
                            text1: 'Four'
                        },
                        {
                            id: 'item-4',
                            checked: true,
                            iconCls: 'icon-data-blue',
                            editing: true,
                            text1: 'One',
                            text2: 'First item'
                        }
                    ]);
                },
                close: function (items) {
                    alert('Closed ' + items.length + ' item(s)');
                },
                closeAll: function (items) {
                    alert('Closed all ' + items.length + ' item(s)');
                },
                click: function (menu, item, event, opts) {
                    alert('Clicked on ' + item.text1);
                }
            }
        });

        var startMenu = Ext.create('Admin.view.StartMenu', {
            xtype: 'startMenu',
            renderTo: Ext.getBody(),
            listeners: {
                login: function (cmp) {
                    console.log('login');
                    cmp.loggedUser = {
                        img: '../html-templates/images/profile-image.png'
                    };
                },
                logout: function (cmp) {
                    console.log('logout');
                    delete cmp.loggedUser;
                },
                pagechange: function (cmp, num) {
                    console.log('page ' + num);
                }
            },
            items: [
                {
                    title: 'Content Manager',
                    cls: 'span2 start-row content-manager'
                },
                {
                    title: 'Dashboard',
                    cls: 'span2 dashboard'
                },
                {
                    title: 'Profile',
                    cls: 'profile title-top title-mask',
                    img: '../html-templates/images/profile-image.png',
                    contentTpl: new Ext.XTemplate('<img src="{img}" alt=""/>')
                },
                {
                    title: 'Activity Stream',
                    cls: 'span2 activity title-top title-right',
                    posts: [
                        {
                            img: '../html-templates/images/profile-image.png',
                            text: 'Hello there !'
                        },
                        {
                            img: '../html-templates/images/profile-image.png',
                            text: 'What do you think about this new tile based start menu ?'
                        },
                        {
                            img: '../html-templates/images/profile-image.png',
                            text: 'Huh ?'
                        }
                    ],
                    contentTpl: new Ext.XTemplate('<tpl for="posts">' +
                                                  '<div class="item">' +
                                                  '<img src="{img}"/>' +
                                                  '<p>{text}</p>' +
                                                  '</div>' +
                                                  '</tpl>')
                },
                {
                    title: 'Cluster',
                    cls: 'cluster'
                },
                {
                    title: 'Userstores',
                    cls: 'userstores'
                },
                {
                    title: 'Accounts',
                    cls: 'accounts'
                },
                {
                    title: 'Site Administrator',
                    cls: 'span2 administrators'
                },
                {
                    title: 'Content Studio',
                    cls: 'content-studio'
                },
                {
                    title: 'Live Trace',
                    cls: 'span2 live-trace title-top title-mask'
                },
                {
                    title: 'Schedule',
                    cls: 'schedule'
                }
            ]
        });

        this.items = [
            {
                xtype: 'button',
                cls: 'start-button',
                iconCls: 'icon-data-white',
                text: 'Content Manager',
                handler: function (btn, evt) {
                    startMenu.slideToggle();
                }
            },
            {
                text: 4,
                cls: 'white',
                menuAlign: 't-b?',
                menu: topBarMenu
            },
            { xtype: 'tbspacer', flex: 5 },
            'sitename/path/to/press-releases',
            { xtype: 'tbspacer', flex: 5 },
            {
                text: 'Add edit item to menu',
                cls: 'red',
                margins: '0 6 0 0',
                handler: function () {
                    topBarMenu.addItems({
                        checked: true,
                        iconCls: 'icon-data-blue',
                        editing: true,
                        text1: 'New edit item',
                        text2: 'Added dynamically'
                    });
                }
            },
            {
                text: 'Add view item to menu',
                cls: 'blue',
                handler: function () {
                    topBarMenu.addItems({
                        checked: true,
                        iconCls: 'icon-data-blue',
                        text1: 'New view item',
                        text2: 'Added dynamically'
                    });
                }
            }
        ];

        this.callParent(arguments);
    }
});
