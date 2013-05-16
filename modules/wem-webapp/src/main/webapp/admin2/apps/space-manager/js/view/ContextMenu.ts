module admin.ui {

    export class ContextMenu {
        ext;

        constructor() {
            var menu = new Ext.menu.Menu();
            menu.addCls('admin-context-menu');
            menu.border = false;
            menu.shadow = false;
            menu.itemId = 'spaceContextMenu';

            var menuItemEdit = new Ext.menu.Item();
            menuItemEdit.text = 'Edit';
            menuItemEdit.iconCls = 'icon-edit';
            menuItemEdit.action = 'editSpace';
            menu.add(menuItemEdit);

            var menuItemOpen = new Ext.menu.Item();
            menuItemOpen.text = 'Open';
            menuItemOpen.iconCls = 'icon-view';
            menuItemOpen.action = 'viewSpace';
            menu.add(menuItemOpen);

            var menuItemDelete = new Ext.menu.Item();
            menuItemDelete.text = 'Delete';
            menuItemDelete.iconCls = 'icon-delete';
            menuItemDelete.action = 'deleteSpace';
            menu.add(menuItemDelete);

            this.ext = menu;

            APP.event.ShowContextMenuEvent.on((event) => {
                this.showAt(event.getX(), event.getY());
            });
        }

        showAt(x:number, y:number) {
            this.ext.showAt(x, y);
        }
    }
}
