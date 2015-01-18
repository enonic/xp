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

            this.onClicked((e: MouseEvent) => {
                // menu itself was clicked so do nothing
                e.preventDefault();
                e.stopPropagation();
            });
        }

        addAction(action: api.ui.Action): ContextMenu {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
            return this;
        }

        addActions(actions: api.ui.Action[]): ContextMenu {
            actions.forEach((action) => {
                this.addAction(action);
            });
            return this;
        }

        setHideOnItemClick(hide: boolean): ContextMenu {
            this.hideOnItemClick = hide;
            return this;
        }

        showAt(x: number, y: number) {
            // referencing through prototype to be able to call this function with context other than this
            // i.e this.showAt.call(other, x, y)
            ContextMenu.prototype.doMoveTo(this, x, y);
            this.show();
        }

        moveBy(dx: number, dy: number) {
            var offset = this.getEl().getOffsetToParent();
            // referencing through prototype to be able to call this function with context other than this
            // i.e this.moveBy.call(other, x, y)
            ContextMenu.prototype.doMoveTo(this, offset.left + dx, offset.top + dy);
        }

        private doMoveTo(menu: ContextMenu, x: number, y: number) {
            menu.getEl().setLeftPx(x).setTopPx(y);
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
            if (!this.getEl().contains(<HTMLElement> evt.target)) {
                // click outside menu
                this.hide();
            }
        }
    }

}
