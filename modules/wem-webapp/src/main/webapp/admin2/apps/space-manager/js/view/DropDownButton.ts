module admin.ui {

    export class DropDownButton {

        ext;

        constructor() {

            var menu = new Ext.menu.Menu();
            menu.cls = 'admin-context-menu';
            menu.border = false;
            menu.shadow = false;
            //menu.width = 120;

            var menuItemOpen = new Ext.menu.Item();
            menuItemOpen.text = 'Open';
            menuItemOpen.action = 'viewSpace';
            menu.add(menuItemOpen);

            var menuItemEdit = new Ext.menu.Item();
            menuItemEdit.text = 'Edit';
            menuItemEdit.action = 'editSpace';
            menu.add(menuItemEdit);

            this.ext = new Ext.button.Button({
                menu: menu,
                cls: 'admin-dropdown-button',
                width: 120,
                padding: 5,
                text: 'Actions',
                height: 30,
                itemId: 'dropdown',
                tdAttr: {
                    width: 120,
                    valign: 'top',
                    style: {
                        padding: '0 20px 0 0'
                    }}
            });
        }
    }
}
