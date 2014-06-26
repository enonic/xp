module api.ui.menu {

    export class ContextMenu extends api.dom.UlEl {
        private menuItems: MenuItem[] = [];
        private hideOnItemClick = true;
        private itemClickListeners: {(item: MenuItem):void}[] = [];

        constructor(actions?: api.ui.Action[], appendToBody = true) {
            super("context-menu");

            if (actions) {
                for (var i = 0; i < actions.length; i++) {
                    this.addAction(actions[i]);
                }
            }
            if (appendToBody) {
                api.dom.Body.get().appendChild(this);
                api.dom.Body.get().onClicked((event: MouseEvent) => this.hideMenuOnOutsideClick(event));
            }
        }

        addAction(action: api.ui.Action): ContextMenu {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
            return this;
        }

        setHideOnItemClick(hide: boolean): ContextMenu {
            this.hideOnItemClick = hide;
            return this;
        }

        showAt(x: number, y: number) {
            this.getEl().
                setLeft(x + 'px').
                setTop(y + 'px');
            this.show();
        }

        onItemClicked(listener: (item: MenuItem) => void) {
            this.itemClickListeners.push(listener);
        }

        unItemClicked(listener: (item: MenuItem) => void) {
            this.itemClickListeners = this.itemClickListeners.filter((currentListener: (item: MenuItem) => void) => {
                return listener != currentListener;
            });
        }

        private notifyItemClicked(item: MenuItem) {
            this.itemClickListeners.forEach((listener: (item: MenuItem)=>void) => {
                listener(item);
            });
        }

        private createMenuItem(action: api.ui.Action): MenuItem {
            var menuItem = new MenuItem(action);
            menuItem.onClicked((event: MouseEvent) => {
                this.notifyItemClicked(menuItem);
                if (this.hideOnItemClick) {
                    this.hide();
                }
                event.preventDefault();
                event.stopPropagation();
            });
            this.menuItems.push(menuItem);
            return menuItem;
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
