module api.liveedit {

    export class ItemViewContextMenu extends api.dom.DivEl {

        private title: ItemViewContextMenuTitle;
        private menu: api.ui.menu.ContextMenu;
        private arrow: api.dom.DivEl;

        constructor(menuTitle: ItemViewContextMenuTitle, actions: api.ui.Action[]) {
            super('item-view-context-menu bottom');

            this.arrow = new api.dom.DivEl("item-view-context-menu-arrow bottom");
            this.appendChild(this.arrow);

            this.title = menuTitle;
            if (this.title) {
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

                this.title.onMouseDown((e: MouseEvent) => {
                    e.preventDefault();
                    e.stopPropagation();
                    lastPosition = {
                        x: e.pageX,
                        y: e.pageY
                    };

                    this.startDrag(dragListener, upListener);
                });
                this.appendChild(this.title);
            }

            this.menu = new api.ui.menu.ContextMenu(actions, false).setHideOnItemClick(false);
            this.menu.onItemClicked((item: api.ui.menu.MenuItem) => {
                this.hide();
            });
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
            this.menu.showAt.call(this, this.updateXToStayWithinFrame(x), this.updateYToStayWithinFrame(y));
        }

        moveBy(dx: number, dy: number) {
            this.menu.moveBy.call(this, dx, dy);
        }

        setActions(actions: api.ui.Action[]) {
            this.menu.setActions(actions);
        }

        private startDrag(dragListener: (e: MouseEvent) => void, upListener: (e: MouseEvent) => void) {
            api.dom.Body.get().onMouseMove(dragListener);
            api.dom.Body.get().onMouseUp(upListener);
        }

        private stopDrag(dragListener: (e: MouseEvent) => void, upListener: (e: MouseEvent) => void) {
            api.dom.Body.get().unMouseMove(dragListener);
            api.dom.Body.get().unMouseUp(upListener);
        }

        private updateXToStayWithinFrame(x: number): number {
            var minimalDistFromFrameBorder = 1;

            if (this.oveflowsLeftFrameBorder(x)) {
                this.updateArrowXPosition(x - minimalDistFromFrameBorder);
                return minimalDistFromFrameBorder;
            }
            else if (this.oveflowsRightFrameBorder(x)) {
                this.updateArrowXPosition(this.getEl().getWidth() - (window.innerWidth - x) + minimalDistFromFrameBorder);
                return window.innerWidth - this.getEl().getWidth() - minimalDistFromFrameBorder;
            }
            else {
                this.resetArrowXPosition();
                return x - this.getEl().getWidth() / 2;
            }
        }

        private updateYToStayWithinFrame(y:number): number {
            var arrowHeight = 7;
            if(this.oveflowsBottom(y, arrowHeight)) {
                this.putArrowToBottomOfMenu();
                return y - this.getEl().getHeight() - arrowHeight;
            } else {
                this.putArrowToTopOfMenu();
                return y + arrowHeight;
            }

        }

        private oveflowsLeftFrameBorder(x:number): boolean {
            return (x - this.getEl().getWidth() / 2) < 0;
        }

        private oveflowsRightFrameBorder(x:number): boolean {
            return (x + this.getEl().getWidth()) > window.innerWidth;
        }

        private oveflowsBottom(y:number, arrowHeight: number): boolean {
            return (y + this.getEl().getHeight() + arrowHeight + 1) > (wemjq(window).scrollTop() + window.innerHeight);
        }

        private updateArrowXPosition(x:number) {
            wemjq(this.arrow.getHTMLElement()).css("left", x);
        }

        private resetArrowXPosition() {
            wemjq(this.arrow.getHTMLElement()).css("left", "");
        }

        private putArrowToBottomOfMenu() {
            this.arrow.removeClass("bottom");
            this.arrow.addClass("top");
        }

        private putArrowToTopOfMenu() {
            this.arrow.removeClass("top");
            this.arrow.addClass("bottom");
        }
    }

}
