module api.liveedit {

    export class ItemViewContextMenu extends api.dom.DivEl {

        private title: ItemViewContextMenuTitle;
        private menu: api.ui.menu.TreeContextMenu;
        private arrow: ItemViewContextMenuArrow;

        constructor(menuTitle: ItemViewContextMenuTitle, actions: api.ui.Action[]) {
            super('item-view-context-menu');

            this.arrow = new ItemViewContextMenuArrow();
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
            this.menu.showAt.call(this, this.restrainX(x), this.restrainY(y, notClicked));
        }

        moveBy(dx: number, dy: number) {
            this.menu.moveBy.call(this, dx, dy);
        }

        setActions(actions: api.ui.Action[]) {
            this.menu.setActions(actions);
        }

        getMenu(): api.ui.menu.TreeContextMenu {
            return this.menu;
        }

        private startDrag(dragListener: (e: MouseEvent) => void, upListener: (e: MouseEvent) => void) {
            api.dom.Body.get().onMouseMove(dragListener);
            api.dom.Body.get().onMouseUp(upListener);
        }

        private stopDrag(dragListener: (e: MouseEvent) => void, upListener: (e: MouseEvent) => void) {
            api.dom.Body.get().unMouseMove(dragListener);
            api.dom.Body.get().unMouseUp(upListener);
        }

        private restrainX(x: number): number {
            var parentEl = this.getParentElement().getEl();

            var width = this.getEl().getWidth(),
                halfWidth = width / 2,
                arrowHalfWidth = this.arrow.getWidth() / 2,
                desiredX = x - halfWidth,
                deltaX,
                minX = parentEl.getMarginLeft(),
                maxX = parentEl.getWidthWithMargin() - parentEl.getMarginRight() - width;

            if (desiredX < minX) {
                deltaX = minX - desiredX;
                this.arrow.getEl().setLeftPx(Math.max(arrowHalfWidth, halfWidth - deltaX));
                return minX;
            } else if (desiredX > maxX) {
                deltaX = maxX - desiredX;
                this.arrow.getEl().setLeftPx(Math.min(halfWidth - deltaX, width - arrowHalfWidth));
                return maxX;
            } else {
                this.arrow.getEl().setLeft("");
                return desiredX;
            }
        }

        private restrainY(y: number, notClicked?: boolean): number {
            var height = this.getEl().getHeight(),
                arrowHeight = this.arrow.getHeight(),
                bottomY = y + height + arrowHeight,
                maxY;

            if (notClicked) {
                maxY = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
            } else {
                maxY = Math.max(document.body.scrollTop, document.documentElement.scrollTop) + window.innerHeight;
            }

            if (bottomY > maxY) {
                this.arrow.toggleVerticalPosition(false);
                return y - height - arrowHeight;
            } else {
                this.arrow.toggleVerticalPosition(true);
                return y + arrowHeight;
            }

        }

    }

    export class ItemViewContextMenuArrow extends api.dom.DivEl {

        constructor() {
            super("item-view-context-menu-arrow-bottom");
        }

        toggleVerticalPosition(bottom: boolean) {
            this.toggleClassEx("item-view-context-menu-arrow-bottom", bottom);
            this.toggleClassEx("item-view-context-menu-arrow-top", !bottom);
        }

        getWidth(): number {
            if (this.hasClass('top') || this.hasClass('bottom')) {
                return 14;
            } else if (this.hasClass('left') || this.hasClass('right')) {
                return 7;
            }
        }

        getHeight(): number {
            if (this.hasClass('top') || this.hasClass('bottom')) {
                return 7;
            } else if (this.hasClass('left') || this.hasClass('right')) {
                return 14;
            }
        }
    }

}
