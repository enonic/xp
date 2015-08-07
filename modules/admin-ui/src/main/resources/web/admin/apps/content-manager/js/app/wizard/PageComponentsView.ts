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
        private mouseDown: boolean = false;

        constructor() {
            super('page-components-view');

            var closeButton = new api.ui.button.CloseButton();
            closeButton.onClicked((event: MouseEvent) => this.hide());

            this.header = new api.dom.H3El('header');
            this.header.setHtml('Page Components');

            this.appendChildren(closeButton, this.header);

            this.setModal(false).setFloating(true).setDraggable(true);

            var body = api.dom.Body.get();

            ResponsiveManager.onAvailableSizeChanged(body, (item: ResponsiveItem) => {
                var smallSize = item.isInRangeOrSmaller(ResponsiveRanges._360_540);
                this.setModal(smallSize).setDraggable(!smallSize);
            });

            body.appendChild(this);
        }

        setPageView(pageView: PageView) {
            this.pageView = pageView;
            if (this.content && this.pageView) {
                this.createTree(this.content, this.pageView);
            }
        }

        setContent(content: Content) {
            this.content = content;
            if (this.content && this.pageView) {
                this.createTree(this.content, this.pageView);
            }
        }

        private createTree(content: Content, pageView: PageView) {
            this.tree = new PageComponentsTreeGrid(content, pageView);
            this.tree.getGrid().subscribeOnClick((event, data) => {
                if (this.isModal()) {
                    this.hide();
                }
            });
            this.appendChild(this.tree);
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

                            el.setOffset({
                                top: Math.max(0, Math.min(newOffset.top, window.innerHeight - el.getHeightWithBorder())),
                                left: Math.max(0, Math.min(newOffset.left, window.innerWidth - el.getWidthWithBorder()))
                            });

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
            return this;
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