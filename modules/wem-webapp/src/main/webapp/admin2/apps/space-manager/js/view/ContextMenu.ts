module app_ui {

    export class ContextMenu {
        ext;

        constructor() {
            var menu = new Ext.menu.Menu({
                cls: 'admin-context-menu',
                border: false,
                shadow: false,
                itemId: 'spaceContextMenu'
            });

            var menuItemEdit = new Ext.menu.Item({
                text: 'Edit',
                iconCls: 'icon-edit',
                action: 'editSpace'
            });

            var menuItemOpen = new Ext.menu.Item({
                text: 'Open',
                iconCls: 'icon-view',
                action: 'viewSpace'
            });

            var menuItemDelete = new Ext.menu.Item({
                text: 'Delete',
                iconCls: 'icon-delete',
                action: 'deleteSpace'
            });
            menu.add(menuItemEdit, menuItemOpen, menuItemDelete);

            this.ext = menu;

            app_event.ShowContextMenuEvent.on((event) => {
                this.showAt(event.getX(), event.getY());
            });
        }

        showAt(x:number, y:number) {
            this.ext.showAt(x, y);
        }
    }
}
