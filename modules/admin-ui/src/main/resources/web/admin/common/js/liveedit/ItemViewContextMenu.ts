module api.liveedit {

    export class ItemViewContextMenu extends api.dom.DivEl {

        private title: ItemViewContextMenuTitle;
        private menu: api.ui.menu.TreeContextMenu;
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

            this.menu = new api.ui.menu.TreeContextMenu(actions, false);
            this.menu.onItemClicked(() => {
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

        showAt(x: number, y: number, notClicked: boolean = false) {
            this.menu.showAt.call(this, this.getXPosition(x), this.getYPosition(y, notClicked));
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

        private getXPosition(x: number): number {
            var pageView = wemjq(this.getHTMLElement()).closest(".page-view");
            var minDistFromFrameBorder = (pageView.outerWidth(true) - pageView.innerWidth()) / 2;

            if (this.overflowsLeftFrameBorder(x)) {
                this.arrow.shiftXPositionLeft(x, pageView);
                return minDistFromFrameBorder;
            }
            else if (this.overflowsRightFrameBorder(x)) {
                this.arrow.shiftXPositionRight(x, pageView);
                return pageView.outerWidth(true) - this.getEl().getWidth() - minDistFromFrameBorder;
            }
            else {
                this.arrow.resetXPosition();
                return x - this.getEl().getWidth() / 2;
            }
        }

        private getYPosition(y:number, notClicked?: boolean): number {
            if (this.overflowsBottom(y, notClicked)) {
                this.arrow.toggleVerticalPosition(false);
                return y - this.getEl().getHeight() - this.arrow.getHeight();
            } else {
                this.arrow.toggleVerticalPosition(true);
                return y + this.arrow.getHeight();
            }

        }

        private overflowsLeftFrameBorder(x:number): boolean {
            return (x - this.getEl().getWidth() / 2) < 0;
        }

        private overflowsRightFrameBorder(x:number): boolean {
            return (x + this.getEl().getWidth() / 2) > window.innerWidth;
        }

        private overflowsBottom(y:number, notClicked?: boolean): boolean {
            var yPos = y + this.getEl().getHeight() + this.arrow.getHeight() + 1;

            return yPos > (notClicked ? Math.max(document.body.scrollHeight, document.documentElement.scrollHeight) : (wemjq(window).scrollTop() + window.innerHeight));
        }
    }

    export class ItemViewContextMenuArrow extends api.dom.DivEl {

        private height: number = 7; //height of pseudo element

        private width: number = 7; //width of pseudo element

        private contextMenu: ItemViewContextMenu;

        constructor(contextMenu: ItemViewContextMenu) {
            super("item-view-context-menu-arrow bottom");

            this.contextMenu = contextMenu;
        }

        toggleVerticalPosition(bottom: boolean) {
            this.toggleClass("bottom", bottom);
            this.toggleClass("top", !bottom);
        }

        updateArrowXPosition(x:number) {
            this.getEl().setLeftPx(x);
        }

        resetXPosition() {
            this.getEl().setLeft("");
        }

        shiftXPositionLeft(x: number, pageView: JQuery) {
            var minDistFromFrameBorder = (pageView.outerWidth(true) - pageView.innerWidth()) / 2;

            if (this.overflowsLeftFrameBorder(x, minDistFromFrameBorder)) {
                this.updateArrowXPosition(this.width);
            }
            else {
                this.updateArrowXPosition(x - minDistFromFrameBorder);
            }
        }

        shiftXPositionRight(x: number, pageView: JQuery) {
            var minDistFromFrameBorder = (pageView.outerWidth(true) - pageView.innerWidth()) / 2;
            var arrowPos = this.contextMenu.getEl().getWidth() - (pageView.outerWidth(true) - x) + minDistFromFrameBorder;

            if (this.overflowsRightFrameBorder(arrowPos)) {
                this.updateArrowXPosition(this.contextMenu.getEl().getWidth() - this.width);
            }
            else {
                this.updateArrowXPosition(arrowPos);
            }
        }

        getHeight(): number {
            return this.height;
        }

        private overflowsLeftFrameBorder(x: number, minimalDistFromFrameBorder: number): boolean {
            return (minimalDistFromFrameBorder + this.width) > x;
        }

        private overflowsRightFrameBorder(arrowPos: number): boolean {
            return (this.contextMenu.getEl().getWidth() - this.width) < arrowPos;
        }
    }

}
