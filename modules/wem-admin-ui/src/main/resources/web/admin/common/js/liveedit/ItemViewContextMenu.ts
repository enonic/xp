module api.liveedit {

    import PageComponent = api.content.page.PageComponent;

    export class ItemViewContextMenu extends api.dom.DivEl {

        private itemView: ItemView;

        private names: api.app.NamesAndIconView;
        private menu: api.ui.menu.ContextMenu;

        constructor(itemView: ItemView, actions: api.ui.Action[]) {
            super('item-view-context-menu bottom');

            this.itemView = itemView;

            this.names = new api.app.NamesAndIconViewBuilder().setAddTitleAttribute(false).build();
            this.names.setMainName(this.getMainName());
            this.names.setIconClass(itemView.getType().getConfig().getIconCls());
            this.appendChild(this.names);

            var lastPosition: {
                x: number;
                y: number;
            };

            var dragListener = (e: MouseEvent) => {
                e.preventDefault();
                e.stopPropagation();
                var x = e.pageX,
                    y = e.pageY;

                this.moveBy(x - lastPosition.x, y - lastPosition.y);
                lastPosition = {
                    x: x,
                    y: y
                };
            };

            var upListener = (e: MouseEvent) => {
                e.preventDefault();
                e.stopPropagation();

                this.stopDrag(dragListener, upListener);
            };

            this.names.onMouseDown((e: MouseEvent) => {
                e.preventDefault();
                e.stopPropagation();
                lastPosition = {
                    x: e.pageX,
                    y: e.pageY
                };

                this.startDrag(dragListener, upListener);
            });

            this.menu = new api.ui.menu.ContextMenu(actions, false).setHideOnItemClick(false);
            this.appendChild(this.menu);

            this.onClicked((e: MouseEvent) => {
                // menu itself was clicked so do nothing
                e.preventDefault();
                e.stopPropagation();
            });

            this.onHidden((e: api.dom.ElementHiddenEvent) => {
                // stop drag if the element was hidden while dragging
                this.stopDrag(dragListener, upListener);
            });

            api.dom.Body.get().appendChild(this);
        }

        showAt(x: number, y: number) {
            this.menu.showAt.call(this, x - this.getEl().getWidth() / 2, y);
        }

        moveBy(dx: number, dy: number) {
            this.menu.moveBy.call(this, dx, dy);
        }

        private getMainName(): string {
            return this.itemView.getName();
        }

        private startDrag(dragListener: (e: MouseEvent) => void, upListener: (e: MouseEvent) => void) {
            api.dom.Body.get().onMouseMove(dragListener);
            api.dom.Body.get().onMouseUp(upListener);
        }

        private stopDrag(dragListener: (e: MouseEvent) => void, upListener: (e: MouseEvent) => void) {
            api.dom.Body.get().unMouseMove(dragListener);
            api.dom.Body.get().unMouseUp(upListener);
        }
    }

}
