module admin.ui {

    export class ContextMenu {
        ext;

        constructor() {
            var menu = new Ext.menu.Menu({
                cls: 'admin-context-menu',
                border: false,
                shadow: false,
                itemId: 'contentMangerContextMenu'
            });

            var itemNew = new Ext.menu.Item({
                text: ' New',
                icon: undefined,
                action: 'newContent',
                disableOnMultipleSelection: true
            });

            var itemEdit = new Ext.menu.Item({
                text: 'Edit',
                icon: undefined,
                action: 'editContent',
                disableOnMultipleSelection: false
            });

            var itemOpen = new Ext.menu.Item({
                text: 'Open',
                icon: undefined,
                action: 'viewContent',
                disableOnMultipleSelection: false
            });

            var itemDelete = new Ext.menu.Item({
                text: 'Delete',
                icon: undefined,
                action: 'deleteContent'
            });

            var itemDuplicate = new Ext.menu.Item({
                text: 'Duplicate',
                icon: undefined,
                action: 'duplicateContent'
            });

            var itemMove = new Ext.menu.Item({
                text: 'Move',
                icon: undefined,
                disabled: true,
                action: 'moveContent'
            });

            menu.add(itemNew, itemEdit, itemOpen, itemDelete, itemDuplicate, itemMove);

            this.ext = menu;
        }
    }
}

