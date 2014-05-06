module api.ui.menu {

    export class ContextMenu extends api.dom.UlEl {
        private menuItems: MenuItem[] = [];

        constructor(...actions: api.ui.Action[]) {
            super("context-menu");

            this.hide();

            api.dom.Body.get().prependChild(this);

            for (var i = 0; i < actions.length; i++) {
                this.addAction(actions[i]);
            }

            api.dom.Body.get().onClicked((event: MouseEvent) => this.hideMenuOnOutsideClick(event));
        }

        addAction(action: api.ui.Action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        }

        private createMenuItem(action: api.ui.Action): MenuItem {
            var menuItem = new MenuItem(action);
            menuItem.onClicked((event: MouseEvent) => this.hide());
            this.menuItems.push(menuItem);
            return menuItem;
        }

        showAt(x: number, y: number) {
            this.getEl().
                setPosition('absolute').
                setZindex(20000).
                setLeft(x + 'px').
                setTop(y + 'px');
            this.show();
        }

        private hideMenuOnOutsideClick(evt: Event): void {
            var id = this.getId();
            var target: any = evt.target;
            for (var element = target; element; element = element.parentNode) {
                if (element.id === id) {
                    return; // menu clicked
                }
            }

            // click outside menu
            this.hide();
        }
    }

}
