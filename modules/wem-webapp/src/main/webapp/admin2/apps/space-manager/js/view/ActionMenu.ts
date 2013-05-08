module admin.ui {

    export class ActionMenu extends BaseActionMenu {

        constructor() {

            var openMenuItem = new Ext.menu.Item();
            openMenuItem.text = 'Open';
            openMenuItem.action = 'viewSpace';

            var editMenuItem = new Ext.menu.Item();
            editMenuItem.text = 'Edit';
            editMenuItem.action = 'editSpace';

            super([ openMenuItem, editMenuItem]);
        }
    }
}
