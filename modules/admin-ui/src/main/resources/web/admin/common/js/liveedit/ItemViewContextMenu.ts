module api.liveedit {

    export class ItemViewContextMenu extends api.dom.DivEl {

        private title: ItemViewContextMenuTitle;
        private menu: api.ui.menu.ContextMenu;
        private arrow: ItemViewContextMenuArrow;

        constructor(menuTitle: ItemViewContextMenuTitle, actions: api.ui.Action[]) {
            super('item-view-context-menu bottom');

            this.arrow = new ItemViewContextMenuArrow(this);
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
            var pageView = wemjq(this.getHTMLElement()).closest(".page-view");
            var minDistFromFrameBorder = (pageView.outerWidth(true) - pageView.innerWidth()) / 2;

            if (this.oveflowsLeftFrameBorder(x)) {
                this.arrow.updateArrowXPosition(this.arrow.overflowsLeftFrameBorder(x, minDistFromFrameBorder) ? ItemViewContextMenuArrow.width : x - minDistFromFrameBorder);
                return minDistFromFrameBorder;
            }
            else if (this.oveflowsRightFrameBorder(x)) {
                var arrowPos = this.getEl().getWidth() - (pageView.outerWidth(true) - x) + minDistFromFrameBorder;
                this.arrow.updateArrowXPosition(this.arrow.overflowsRightFrameBorder(arrowPos, minDistFromFrameBorder) ? this.getEl().getWidth() - ItemViewContextMenuArrow.width : arrowPos );
                return pageView.outerWidth(true) - this.getEl().getWidth() - minDistFromFrameBorder;
            }
            else {
                this.arrow.resetXPosition();
                return x - this.getEl().getWidth() / 2;
            }
        }

        private updateYToStayWithinFrame(y:number): number {
            if (this.oveflowsBottom(y)) {
                this.arrow.putArrowToBottomOfMenu();
                return y - this.getEl().getHeight() - ItemViewContextMenuArrow.height;
            } else {
                this.arrow.putArrowToTopOfMenu();
                return y + ItemViewContextMenuArrow.height;
            }

        }

        private oveflowsLeftFrameBorder(x:number): boolean {
            return (x - this.getEl().getWidth() / 2) < 0;
        }

        private oveflowsRightFrameBorder(x:number): boolean {
            return (x + this.getEl().getWidth()) > window.innerWidth;
        }

        private oveflowsBottom(y:number): boolean {
            return (y + this.getEl().getHeight() + ItemViewContextMenuArrow.height + 1) > (wemjq(window).scrollTop() + window.innerHeight);
        }

    }

    export class ItemViewContextMenuArrow extends api.dom.DivEl {

        static height: number = 7; //height of pseudo element

        static width: number = 7; //width of pseudo element

        private contextMenu: ItemViewContextMenu;

        constructor(contextMenu: ItemViewContextMenu) {
            super("item-view-context-menu-arrow bottom");

            this.contextMenu = contextMenu;
        }

        putArrowToBottomOfMenu() {
            this.removeClass("bottom");
            this.addClass("top");
        }

        putArrowToTopOfMenu() {
            this.removeClass("top");
            this.addClass("bottom");
        }

        updateArrowXPosition(x:number) {
            this.getEl().setLeftPx(x);
        }

        resetXPosition() {
            this.getEl().setLeft("");
        }

        overflowsLeftFrameBorder(x:number, minimalDistFromFrameBorder: number): boolean {
            return (minimalDistFromFrameBorder + ItemViewContextMenuArrow.width) > x;
        }

        overflowsRightFrameBorder(arrowPos: number, minimalDistFromFrameBorder: number): boolean {
            return (this.contextMenu.getEl().getWidth() - ItemViewContextMenuArrow.width) < arrowPos;
        }
    }

}
