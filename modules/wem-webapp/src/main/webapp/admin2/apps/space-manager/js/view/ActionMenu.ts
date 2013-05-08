module admin.ui {

    export class ActionMenu extends BaseActionMenu {

        constructor() {

            super([
                this.createMenuItem('Open', 'viewSpace'),
                this.createMenuItem('Edit', 'editSpace')
            ]);
        }

        private createMenuItem(text:string, action:string) {
            var menuItem = new Ext.menu.Item();
            menuItem.text = text;
            menuItem.action = action;
            return menuItem;
        }
    }
}
