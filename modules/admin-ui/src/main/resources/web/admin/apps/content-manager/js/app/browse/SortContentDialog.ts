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
    import DialogButton = api.ui.dialog.DialogButton;

    export class SortContentDialog extends api.ui.dialog.ModalDialog {

        private sortAction: SaveSortedContentAction;

        private parentContent: api.content.ContentSummary;

        private contentGrid: app.browse.SortContentTreeGrid;

        private sortContentMenu: SortContentTabMenu;

        private curChildOrder: ChildOrder;

        private prevChildOrder: ChildOrder;

        private gridDragHandler: ContentGridDragHandler;

        private isOpen: boolean;

        private saveButton: DialogButton;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Sort items")
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

            this.saveButton = this.addAction(this.sortAction);
            this.saveButton.addClass("save-button");

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
                    this.showLoadingSpinner();

                    if (this.curChildOrder.isManual()) {
                        if (this.prevChildOrder && !this.prevChildOrder.equals(this.parentContent.getChildOrder())) {
                            this.setManualReorder(this.prevChildOrder, this.gridDragHandler.getContentMovements()).done(() => {
                                new api.content.ContentChildOrderUpdatedEvent(this.parentContent.getContentId()).fire();
                                this.hideLoadingSpinner();
                                this.close();
                            });
                        } else {
                            this.setManualReorder(null, this.gridDragHandler.getContentMovements()).done(() => {
                                new api.content.ContentChildOrderUpdatedEvent(this.parentContent.getContentId()).fire();
                                this.hideLoadingSpinner();
                                this.close();
                            });
                        }
                    } else {
                        this.setContentChildOrder(this.curChildOrder).done(() => {
                            new api.content.ContentChildOrderUpdatedEvent(this.parentContent.getContentId()).fire();
                            this.hideLoadingSpinner();
                            this.close();
                        });
                    }
                }
            });

            OpenSortDialogEvent.on((event) => {
                this.parentContent = event.getContent();
                this.curChildOrder = this.parentContent.getChildOrder();
                this.prevChildOrder = undefined;
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

        private showLoadingSpinner() {
            this.saveButton.addClass("spinner");
        }

        private hideLoadingSpinner() {
            this.saveButton.removeClass("spinner");
        }

        private setContentChildOrder(order: ChildOrder, silent: boolean = false): wemQ.Promise<api.content.Content> {
            return new api.content.OrderContentRequest().
                setSilent(silent).
                setContentId(this.parentContent.getContentId()).
                setChildOrder(order).
                sendAndParse();
        }

        private setManualReorder(order: ChildOrder, movements: OrderChildMovements, silent: boolean = false): wemQ.Promise<api.content.Content> {
            return new api.content.OrderChildContentRequest().
                setSilent(silent).
                setManualOrder(true).
                setContentId(this.parentContent.getContentId()).
                setChildOrder(order).
                setContentMovements(movements).
                sendAndParse();
        }
    }


}