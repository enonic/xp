module api.ui.menu {

    export class TreeContextMenu extends api.dom.DlEl {
        private itemClickListeners: {(item: TreeMenuItem): void}[] = [];

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

        private addAction(action: api.ui.Action): TreeMenuItem {
            var childActions = action.getChildActions();
            var menuItem = this.createMenuItem(action);
            var subItems = [];
            this.appendChild(menuItem);

            if (childActions.length > 0) {
                for (var i = 0; i < childActions.length; i++) {
                    subItems.push(this.addAction(childActions[i]));
                }
                menuItem.onClicked((event: MouseEvent) => {
                    for (var i = 0; i < subItems.length; i++) {
                        subItems[i].toggleExpand();
                    }
                });
            }
            else {
                menuItem.onClicked((event: MouseEvent) => {
                    this.notifyItemClicked();

                    event.preventDefault();
                    event.stopPropagation();
                });
            }
            return menuItem;
        }

        addActions(actions: api.ui.Action[]): TreeContextMenu {
            actions.forEach((action) => {
                this.addAction(action);
            });
            return this;
        }

        setActions(actions: api.ui.Action[]): TreeContextMenu {
            this.removeChildren();
            this.addActions(actions);
            return this;
        }

        onItemClicked(listener: () => void) {
            this.itemClickListeners.push(listener);
        }

        unItemClicked(listener: () => void) {
            this.itemClickListeners = this.itemClickListeners.filter((currentListener: () => void) => {
                return listener != currentListener;
            });
        }

        private notifyItemClicked() {
            this.itemClickListeners.forEach((listener: ()=>void) => {
                listener();
            });
        }

        showAt(x: number, y: number) {
            // referencing through prototype to be able to call this function with context other than this
            // i.e this.showAt.call(other, x, y)
            TreeContextMenu.prototype.doMoveTo(this, x, y);
            this.show();
        }

        moveBy(dx: number, dy: number) {
            var offset = this.getEl().getOffsetToParent();
            // referencing through prototype to be able to call this function with context other than this
            // i.e this.moveBy.call(other, x, y)
            TreeContextMenu.prototype.doMoveTo(this, offset.left + dx, offset.top + dy);
        }

        private doMoveTo(menu: TreeContextMenu, x: number, y: number) {
            menu.getEl().setLeftPx(x).setTopPx(y);
        }

        private createMenuItem(action: api.ui.Action): TreeMenuItem {
            return new TreeMenuItem(action, action.getIconClass());
        }

        private hideMenuOnOutsideClick(evt: Event): void {
            if (!this.getEl().contains(<HTMLElement> evt.target)) {
                // click outside menu
                this.hide();
            }
        }
    }

}
