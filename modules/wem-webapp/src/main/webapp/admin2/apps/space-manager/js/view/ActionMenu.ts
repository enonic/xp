module app_ui {

    export class ActionMenu extends BaseActionMenu {

        constructor() {

            var openMenuItem = new Ext.menu.Item({
                text: 'Open',
                action: 'viewSpace'
            });

            var editMenuItem = new Ext.menu.Item({
                text: 'Edit',
                action: 'editSpace'
            });

            super([ openMenuItem, editMenuItem]);
        }
    }
}
