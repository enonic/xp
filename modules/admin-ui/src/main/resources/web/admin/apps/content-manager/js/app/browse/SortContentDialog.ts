module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import Element = api.dom.Element;
    import SaveSortedContentAction = action.SaveSortedContentAction;
    import ContentSummary = api.content.ContentSummary;
    import ChildOrder = api.content.ChildOrder;
    import OrderChildMovements = api.content.OrderChildMovements;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

    export class SortContentDialog extends api.ui.dialog.ModalDialog {

        private sortAction: SaveSortedContentAction;

        private parentContent: api.content.ContentSummary;

        private contentGrid: app.browse.SortContentTreeGrid;

        private sortContentMenu: SortContentTabMenu;

        private curChildOrder: ChildOrder;

        private prevChildOrder: ChildOrder;

        private gridDragHandler: ContentGridDragHandler;

        private isOpen: boolean;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Default sorting")
            });

            var menu = new api.ui.tab.TabMenu();
            var tabMenuItem = (<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel("(sorting type)")).build();
            tabMenuItem.setActive(true);
            menu.addNavigationItem(tabMenuItem);
            menu.selectNavigationItem(0);
            menu.show();

            this.sortContentMenu = new SortContentTabMenu();
            this.sortContentMenu.show();
            this.appendChildToTitle(this.sortContentMenu);

            this.sortContentMenu.onSortOrderChanged(() => {
                var newOrder = this.sortContentMenu.getSelectedNavigationItem().getChildOrder();
                if (!this.curChildOrder.equals(newOrder)) {
                    if (!newOrder.isManual()) {
                        this.curChildOrder = newOrder;
                        this.contentGrid.setChildOrder(this.curChildOrder);
                        api.content.ContentSummaryAndCompareStatusFetcher.fetch(this.parentContent.getContentId()).
                            done((response: api.content.ContentSummaryAndCompareStatus) => {
                                this.contentGrid.reload(response);
                            });
                        this.gridDragHandler.clearContentMovements();
                    } else {
                        this.prevChildOrder = this.curChildOrder;
                        this.curChildOrder = newOrder;
                        this.contentGrid.setChildOrder(this.curChildOrder);
                    }
                }
            });

            this.getEl().addClass("sort-content-dialog");

            this.sortAction = new SaveSortedContentAction(this);

            this.addAction(this.sortAction);

            this.contentGrid = new app.browse.SortContentTreeGrid();
            this.contentGrid.getEl().addClass("sort-content-grid");
            this.contentGrid.onLoaded(() => {
                this.contentGrid.render(true);

                if (this.contentGrid.getContentId()) {
                    this.open();
                }
            });

            this.gridDragHandler = new ContentGridDragHandler(this.contentGrid);
            this.gridDragHandler.onPositionChanged(() => {
                this.sortContentMenu.selectManualSortingItem();
            });

            var header = new api.dom.H6El();
            header.setHtml("Sort content by selecting default sort above, or drag and drop for manual sorting");
            this.appendChildToContentPanel(header);
            this.appendChildToContentPanel(this.contentGrid);


            this.sortAction.onExecuted(() => {

                if (this.curChildOrder.equals(this.parentContent.getChildOrder()) && !this.curChildOrder.isManual()) {
                    this.close();
                } else {
                    if (this.curChildOrder.isManual()) {
                        if (this.prevChildOrder && !this.prevChildOrder.isManual()) {

                            this.setContentChildOrder(this.prevChildOrder, true).done(() => {
                                this.setOrderAndManualReorder(this.curChildOrder, this.gridDragHandler.getContentMovements()).
                                    done(() => {
                                        new api.content.ContentChildOrderUpdatedEvent(this.parentContent.getContentId()).fire();
                                        this.close();
                                    });
                            });

                        } else {
                            this.setManualReorder(this.gridDragHandler.getContentMovements()).done(() => {
                                new api.content.ContentChildOrderUpdatedEvent(this.parentContent.getContentId()).fire();
                                this.close();
                            });
                        }
                    } else {
                        this.setContentChildOrder(this.curChildOrder).done(() => {
                            new api.content.ContentChildOrderUpdatedEvent(this.parentContent.getContentId()).fire();
                            this.close();
                        });
                    }
                }
            });

            OpenSortDialogEvent.on((event) => {
                this.parentContent = event.getContent();
                this.curChildOrder = this.parentContent.getChildOrder();
                this.sortContentMenu.selectNavigationItemByOrder(this.curChildOrder);
                api.content.ContentSummaryAndCompareStatusFetcher.fetch(this.parentContent.getContentId()).
                    done((response: api.content.ContentSummaryAndCompareStatus) => {
                        this.contentGrid.reload(response);
                        if (!response.hasChildren()) {
                            this.contentGrid.getEl().setAttribute("data-content", event.getContent().getPath().toString());
                            this.contentGrid.addClass("no-content");
                        } else {
                            this.contentGrid.removeClass("no-content");
                            this.contentGrid.getEl().removeAttribute("data-content");
                        }
                    });
            });

            this.addCancelButtonToBottom();

        }

        open() {
            if (!this.isOpen) {
                if (this.contentGrid.getGrid().getDataView().getLength() > 0) {
                    this.contentGrid.getGrid().getEl().setHeightPx(45);//chrome invalid grid render fix
                }
                this.contentGrid.getGrid().resizeCanvas();
                super.open();
                this.isOpen = true;
            }
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            this.remove();
            super.close();
            this.isOpen = false;
            this.contentGrid.setChildOrder(null);
            this.gridDragHandler.clearContentMovements();
        }

        getContent(): ContentSummary {
            return this.parentContent;
        }

        private setContentChildOrder(order: ChildOrder, silent: boolean = false): wemQ.Promise<api.content.Content> {
            return new api.content.OrderContentRequest().
                setSilent(silent).
                setContentId(this.parentContent.getContentId()).
                setChildOrder(order).
                sendAndParse();
        }

        private setManualReorder(movements: OrderChildMovements, silent: boolean = false): wemQ.Promise<api.content.Content> {
            return new api.content.OrderChildContentRequest().
                setSilent(silent).
                setContentId(this.parentContent.getContentId()).
                setContentMovements(movements).
                sendAndParse();
        }

        private setOrderAndManualReorder(order: ChildOrder, movements: OrderChildMovements,
                                         silent: boolean = false): wemQ.Promise<api.content.Content> {
            return new api.content.OrderContentAndChildrenRequest().
                setSilent(silent).
                setContentId(this.parentContent.getContentId()).
                setChildOrder(order).
                setContentMovements(movements).
                sendAndParse();
        }
    }


}