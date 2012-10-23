Ext.define('Admin.view.TopBarPanel', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.topBarPanel',

    requires: ['Admin.view.TopBarMenu'],

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

        this.items = [
            {
                xtype: 'image',
                cls: 'icon-data-white'
            },
            'Content Manager',
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
