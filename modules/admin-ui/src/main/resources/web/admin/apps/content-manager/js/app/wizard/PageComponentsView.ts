module app.wizard {

    import LiveEditPageProxy = app.wizard.page.LiveEditPageProxy;

    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectedEvent = api.liveedit.ItemViewDeselectedEvent;
    import ComponentAddedEvent = api.liveedit.ComponentAddedEvent;
    import ComponentRemovedEvent = api.liveedit.ComponentRemovedEvent;
    import ComponentDuplicatedEvent = api.liveedit.ComponentDuplicatedEvent;
    import ComponentLoadedEvent = api.liveedit.ComponentLoadedEvent;
    import ComponentResetEvent = api.liveedit.ComponentResetEvent;

    import Content = api.content.Content;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import PageView = api.liveedit.PageView;
    import ItemView = api.liveedit.ItemView;

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

    export class PageComponentsView extends api.dom.DivEl {

        private content: Content;
        private pageView: PageView;
        private liveEditPage: LiveEditPageProxy;

        private tree: PageComponentsTreeGrid;
        private header: api.dom.H3El;
        private modal: boolean;
        private floating: boolean;
        private draggable: boolean;

        private mouseDownListener: (event: MouseEvent) => void;
        private mouseUpListener: (event?: MouseEvent) => void;
        private mouseMoveListener: (event: MouseEvent) => void;
        private clickListener: (event, data) => void;
        private mouseDown: boolean = false;
        public static debug: boolean = false;

        constructor(liveEditPage: LiveEditPageProxy) {
            super('page-components-view');

            this.liveEditPage = liveEditPage;

            var closeButton = new api.ui.button.CloseButton();
            closeButton.onClicked((event: MouseEvent) => this.hide());

            this.header = new api.dom.H2El('header');
            this.header.setHtml('Page Components');

            this.appendChildren(closeButton, this.header);

            this.setModal(false).setFloating(true).setDraggable(true);

            this.onShown((event) => {
                this.constrainToParent();
            });

            ResponsiveManager.onAvailableSizeChanged(api.dom.Body.get(), (item: ResponsiveItem) => {
                var smallSize = item.isInRangeOrSmaller(ResponsiveRanges._360_540);
                if (!smallSize && this.isVisible()) {
                    this.constrainToParent();
                }
                if (item.isRangeSizeChanged()) {
                    this.setModal(smallSize).setDraggable(!smallSize);
                }
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

            this.liveEditPage.onItemViewSelected((event: ItemViewSelectedEvent) => {
                if (!event.isNew()) {
                    this.tree.selectNode(this.tree.getDataId(event.getItemView()));
                }
            });

            this.liveEditPage.onItemViewDeselected((event: ItemViewDeselectedEvent) => {
                this.tree.deselectNode(this.tree.getDataId(event.getItemView()));
            });

            this.liveEditPage.onComponentAdded((event: ComponentAddedEvent) => {
                var parentNode = this.tree.getRoot().getCurrentRoot().findNode(this.tree.getDataId(event.getParentRegionView()));
                if (parentNode) {
                    // deselect all otherwise node is going to be added as child to selection (that is weird btw)
                    this.tree.deselectAll();
                    var index = event.getParentRegionView().getComponentViews().indexOf(event.getComponentView());
                    if (index >= 0) {
                        this.tree.insertNode(event.getComponentView(), false, index, parentNode).then(() => {
                            // expand parent node to show added one
                            this.tree.expandNode(parentNode);

                            if (event.getComponentView().isSelected()) {
                                this.tree.selectNode(this.tree.getDataId(event.getComponentView()));
                            }

                            this.constrainToParent();
                        });
                    }
                }
            });

            this.liveEditPage.onComponentRemoved((event: ComponentRemovedEvent) => {
                this.tree.deleteNode(event.getComponentView());
                // update parent node in case it was the only child
                this.tree.updateNode(event.getParentRegionView());
            });

            this.liveEditPage.onComponentLoaded((event: ComponentLoadedEvent) => {
                var oldDataId = this.tree.getDataId(event.getOldComponentView());

                this.tree.updateNode(event.getNewComponentView(), oldDataId).then(() => {
                    var newDataId = this.tree.getDataId(event.getNewComponentView());

                    if (this.tree.hasChildren(event.getNewComponentView())) {
                        // expand new node as it has children
                        var newNode = this.tree.getRoot().getCurrentRoot().findNode(newDataId);
                        this.tree.expandNode(newNode);
                    }

                    if (event.getNewComponentView().isSelected()) {
                        this.tree.selectNode(newDataId);
                    }
                });
            });

            this.liveEditPage.onComponentReset((event: ComponentResetEvent) => {
                var oldDataId = this.tree.getDataId(event.getOldComponentView());

                if (this.tree.hasChildren(event.getOldComponentView())) {
                    var oldNode = this.tree.getRoot().getCurrentRoot().findNode(oldDataId);
                    oldNode.removeChildren();
                    this.tree.refreshNode(oldNode);
                }

                this.tree.updateNode(event.getNewComponentView(), oldDataId).then(() => {

                    if (event.getNewComponentView().isSelected()) {
                        var newDataId = this.tree.getDataId(event.getNewComponentView());
                        this.tree.selectNode(newDataId);
                    }
                });
            });

            this.clickListener = (event, data) => {
                var elem = new api.dom.ElementHelper(event.target);
                if (elem.hasClass('toggle')) {
                    // do nothing if expand toggle is clicked
                    return;
                }

                this.tree.getGrid().selectRow(data.row);

                if (this.isModal()) {
                    this.hide();
                }
            };
            this.tree.getGrid().subscribeOnClick(this.clickListener);
            this.tree.onSelectionChanged((data, nodes) => {
                if (nodes.length > 0) {
                    nodes[0].getData().select(null, api.liveedit.ItemViewContextMenuPosition.TOP);
                    nodes[0].getData().scrollComponentIntoView();

                    if (this.isModal()) {
                        this.hide();
                    }
                }
            });
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
                        if (PageComponentsView.debug) {
                            console.log('mouse down', this.mouseDown, event);
                        }
                        if (!this.mouseDown && event.buttons == 1) {
                            // left button was clicked
                            event.preventDefault();
                            event.stopPropagation();
                            this.mouseDown = true;
                            lastPos = {
                                x: event.clientX,
                                y: event.clientY
                            };
                        }
                    }
                }
                if (!this.mouseUpListener) {
                    this.mouseUpListener = (event?: MouseEvent) => {
                        if (PageComponentsView.debug) {
                            console.log('mouse up', this.mouseDown, event);
                        }
                        if (this.mouseDown) {
                            // left button was released
                            if (event) {
                                event.preventDefault();
                                event.stopPropagation();
                            }

                            this.mouseDown = false;
                        }
                    }
                }
                if (!this.mouseMoveListener) {
                    this.mouseMoveListener = (event: MouseEvent) => {
                        if (this.mouseDown) {
                            if (event.buttons != 1) {
                                // button was probably released outside browser window
                                this.mouseUpListener();
                                return;
                            }
                            event.preventDefault();
                            event.stopPropagation();

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

                            this.constrainToParent(newOffset);

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
            this.toggleClass('draggable', draggable);
            this.draggable = draggable;
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

            el.setMaxHeightPx(parentEl.getHeight());

            el.setOffset({
                top: Math.max(parentOffset.top,
                    Math.min(offset.top, parentOffset.top + parentEl.getHeight() - el.getHeightWithBorder())),
                left: Math.max(parentOffset.left,
                    Math.min(offset.left, parentOffset.left + parentEl.getWidth() - el.getWidthWithBorder()))
            });
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