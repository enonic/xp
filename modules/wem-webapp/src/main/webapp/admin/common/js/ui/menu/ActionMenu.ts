module api.ui.menu {

    export class ActionMenu extends api.dom.DivEl {

        private button:api.ui.Button;
        private list:ActionList;

        constructor(actions:api.ui.Action[]) {
            super("action-menu");

            this.button = new api.ui.Button("Actions");
            this.button.getEl().addEventListener("click", (evt:Event) => {
                this.showMenuOnButtonClick(evt);
            });
            this.appendChild(this.button);

            this.list = new ActionList(this, actions);
            this.list.hide();
            this.appendChild(this.list);

            window.document.addEventListener("click", (evt:Event) => {
                this.hideMenuOnOutsideClick(evt);
            });

        }

        addAction(action:api.ui.Action) {
            this.list.addAction(action);
        }

        show() {
            this.list.show();
        }

        hide() {
            this.list.hide();
        }

        setEnabled(enabled:boolean) {
            this.button.setEnabled(enabled);
        }

        isEnabled() {
            return this.button.isEnabled();
        }

        private showMenuOnButtonClick(evt:Event):void {
            if (!this.button.isEnabled()) {
                return;
            }

            if (!this.list.isVisible()) {
                this.show();
            } else {
                this.hide();
            }
        }

        private hideMenuOnOutsideClick(evt:Event):void {
            var id = this.getId();
            var target:any = evt.target;
            for (var element = target; element; element = element.parentNode) {
                if (element.id === id) {
                    return; // menu clicked
                }
            }

            // click outside menu
            this.hide();
        }

    }

    export class ActionList extends api.dom.UlEl {

        private menu:ActionMenu;
        private menuItems:api.ui.menu.MenuItem[] = [];

        constructor(menu:ActionMenu, actions:api.ui.Action[]) {
            super();
            this.menu = menu;

            for (var i = 0; i < actions.length; i++) {
                this.addAction(actions[i]);
            }
        }

        addAction(action:api.ui.Action) {
            var menuItem = this.createMenuItem(action);
            this.menuItems.push(menuItem);
            this.appendChild(menuItem);
        }

        private createMenuItem(action:api.ui.Action):MenuItem {
            var menuItem = new api.ui.menu.MenuItem(action);
            menuItem.getEl().addEventListener("click", (evt:Event) => {
                this.menu.hide();
            });
            return menuItem;
        }
    }

}
