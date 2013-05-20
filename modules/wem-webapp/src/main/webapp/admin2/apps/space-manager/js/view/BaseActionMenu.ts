module admin.ui {

    /**
     * Candidate to be shared between applications.
     */
    export class BaseActionMenu {

        ext:Ext_button_Button;

        constructor(menuItems:any[]) {

            var menu = new Ext.menu.Menu({
                cls: 'admin-context-menu',
                border: false,
                shadow: false,
                width: 120
            });

            for (var i in menuItems) {
                menu.add(menuItems[i]);
            }

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
