module app.wizard {

    import Content = api.content.Content;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import PageView = api.liveedit.PageView;

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

    export class PageComponentsView extends api.dom.DivEl {

        private content: Content;
        private pageView: PageView;

        private tree: PageComponentsTreeGrid;
        private header: api.dom.H3El;
        private modal: boolean;
        private floating: boolean;
        private draggable: boolean;

        private mouseDownListener: (event: MouseEvent) => void;
        private mouseUpListener: (event: MouseEvent) => void;
        private mouseMoveListener: (event: MouseEvent) => void;
        private clickListener: (event, data) => void;
        private mouseDown: boolean = false;

        constructor() {
            super('page-components-view');

            var closeButton = new api.ui.button.CloseButton();
            closeButton.onClicked((event: MouseEvent) => this.hide());

            this.header = new api.dom.H3El('header');
            this.header.setHtml('Page Components');

            this.appendChildren(closeButton, this.header);

            this.setModal(false).setFloating(true).setDraggable(true);

            ResponsiveManager.onAvailableSizeChanged(api.dom.Body.get(), (item: ResponsiveItem) => {
                var smallSize = item.isInRangeOrSmaller(ResponsiveRanges._360_540);
                if (!smallSize) {
                    this.constrainToParent();
                }
                this.setModal(smallSize).setDraggable(!smallSize);
            });
        }

        setPageView(pageView: PageView) {
            this.pageView = pageView;
            if (!this.tree && this.content && this.pageView) {
                this.createTree(this.content, this.pageView);
            } else if (this.tree) {
                this.tree.setPageView(pageView);
            }
        }

        setContent(content: Content) {
            this.content = content;
            if (!this.tree && this.content && this.pageView) {
                this.createTree(this.content, this.pageView);
            }
        }

        private createTree(content: Content, pageView: PageView) {
            this.tree = new PageComponentsTreeGrid(content, pageView);
            this.clickListener = (event, data) => {
                if (this.isModal()) {
                    this.hide();
                }
            };
            this.tree.getGrid().subscribeOnClick(this.clickListener);
            this.appendChild(this.tree);

            this.tree.onRemoved((event) => this.tree.getGrid().unsubscribeOnClick(this.clickListener));
        }

        isDraggable(): boolean {
            return this.draggable;
        }

        setDraggable(draggable: boolean): PageComponentsView {
            var body = api.dom.Body.get();
            if (!this.draggable && draggable) {
                var lastPos;
                if (!this.mouseDownListener) {
                    this.mouseDownListener = (event: MouseEvent) => {
                        this.mouseDown = true;
                        lastPos = {
                            x: event.clientX,
                            y: event.clientY
                        };
                    }
                }
                if (!this.mouseUpListener) {
                    this.mouseUpListener = (event: MouseEvent) => {
                        this.mouseDown = false;
                    }
                }
                if (!this.mouseMoveListener) {
                    this.mouseMoveListener = (event: MouseEvent) => {
                        if (this.mouseDown) {
                            var el = this.getEl(),
                                newPos = {
                                    x: event.clientX,
                                    y: event.clientY
                                },
                                offset = el.getOffset(),
                                newOffset = {
                                    top: offset.top + newPos.y - lastPos.y,
                                    left: offset.left + newPos.x - lastPos.x
                                };

                            el.setOffset(this.constrainToParent(newOffset));

                            lastPos = newPos;
                        }
                    }
                }
                this.header.onMouseDown(this.mouseDownListener);
                body.onMouseUp(this.mouseUpListener);
                body.onMouseMove(this.mouseMoveListener);
            } else if (this.draggable && !draggable) {
                this.header.unMouseDown(this.mouseDownListener);
                body.unMouseUp(this.mouseUpListener);
                body.unMouseMove(this.mouseMoveListener);
            }
            this.draggable = draggable;
            this.toggleClass('draggable', draggable);
            return this;
        }

        private constrainToParent(offset?: {top: number; left: number}) {

            var parentEl, parentOffset,
                el = this.getEl(),
                offset = offset || el.getOffset();

            if (this.getParentElement()) {
                parentEl = this.getParentElement().getEl();
                parentOffset = parentEl.getOffset();
            }
            else {
                parentEl = api.dom.WindowDOM.get();
                parentOffset = {
                    top: 0,
                    left: 0
                }
            }

            return {
                top: Math.max(parentOffset.top,
                    Math.min(offset.top, parentOffset.top + parentEl.getHeight() - el.getHeightWithBorder())),
                left: Math.max(parentOffset.left,
                    Math.min(offset.left, parentOffset.left + parentEl.getWidth() - el.getWidthWithBorder()))
            }
        }

        isFloating(): boolean {
            return this.floating;
        }

        setFloating(floating: boolean): PageComponentsView {
            this.toggleClass('floating', floating);
            this.floating = floating;
            return this;
        }

        isModal(): boolean {
            return this.modal;
        }

        setModal(modal: boolean): PageComponentsView {
            this.toggleClass('modal', modal);
            if (this.tree) {
                // tree may not be yet initialized
                this.tree.getGrid().resizeCanvas();
            }
            this.modal = modal;
            return this;
        }
    }

}