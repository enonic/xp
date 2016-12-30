module api.ui.menu {

    export class ContextMenu extends Menu {

        constructor(actions?: api.ui.Action[], appendToBody: boolean = true) {
            super(actions);

            this.addClass("context-menu");

            if (appendToBody) {
                api.dom.Body.get().appendChild(this);
                api.dom.Body.get().onClicked((event: MouseEvent) => this.hideMenuOnOutsideClick(event));
            }
        }

        showAt(x: number, y: number) {
            // referencing through prototype to be able to call this function with context other than this
            // i.e this.showAt.call(other, x, y)
            ContextMenu.prototype.doMoveTo(this, x, y);
            this.show();
        }

        moveBy(dx: number, dy: number) {
            let offset = this.getEl().getOffsetToParent();
            // referencing through prototype to be able to call this function with context other than this
            // i.e this.moveBy.call(other, x, y)
            ContextMenu.prototype.doMoveTo(this, offset.left + dx, offset.top + dy);
        }

        doMoveTo(menu: ContextMenu, x: number, y: number) {
            menu.getEl().setLeftPx(x).setTopPx(y);
        }

        private hideMenuOnOutsideClick(evt: Event): void {
            if (!this.getEl().contains(<HTMLElement> evt.target)) {
                // click outside menu
                this.hide();
            }
        }
    }

}
