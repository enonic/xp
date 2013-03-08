Ext.define('Admin.view.contentManager.ToolbarMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.contentManagerToolbarMenu',

    cls: 'admin-context-menu',
    border: false,
    items: [
        {
            text: undefined,
            icon: undefined,
            action: 'moveDetailPanel'
        },
        {
            text: 'Toogle View',
            icon: undefined,
            action: 'toggleDetailPanel'
        }
    ]
});

