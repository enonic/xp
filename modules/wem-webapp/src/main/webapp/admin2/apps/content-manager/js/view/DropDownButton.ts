module admin.ui {

    export class DropDownButton {

        ext;

        constructor(config?:Object, menuItems?:Object[]) {

            var menu;
            if (!Ext.isEmpty(menuItems)) {
                menu = new Admin.view.BaseContextMenu({
                    width: 120,
                    items: menuItems
                })
            }

            this.ext = new Ext.button.Button(Ext.apply({
                cls: 'admin-dropdown-button',
                width: 120,
                padding: 5,
                menu: menu
            }, config));
        }

    }
}