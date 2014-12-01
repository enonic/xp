module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import Element = api.dom.Element;
    import SaveSortedContentAction = action.SaveSortedContentAction;
    import ContentSummary = api.content.ContentSummary;
    import ChildOrder = api.content.ChildOrder;

    export class SortContentDialog extends api.ui.dialog.ModalDialog {

        private sortAction: SaveSortedContentAction;

        private parentContent: api.content.ContentSummary;

        private contentGrid: app.browse.SortContentTreeGrid;
        private sortContentMenu: SortContentTabMenu;

        private curChildOrder: ChildOrder;
        private isOpen: boolean;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("")
            });

            var menu = new api.ui.tab.TabMenu();
            var tabMenuItem = new api.ui.tab.TabMenuItemBuilder().setLabel("(sorting type)").build();
            tabMenuItem.setActive(true);
            menu.addNavigationItem(tabMenuItem);
            menu.selectNavigationItem(0);
            menu.show();

            this.sortContentMenu = new SortContentTabMenu();
            this.sortContentMenu.show();
            this.appendChild(this.sortContentMenu);
            this.sortContentMenu.onSortOrderChanged(() => {
                this.curChildOrder = this.sortContentMenu.getSelectedNavigationItem().getChildOrder();
                this.contentGrid.setChildOrder(this.curChildOrder);
                api.content.ContentSummaryAndCompareStatusFetcher.fetch(this.parentContent.getContentId()).
                    done((response: api.content.ContentSummaryAndCompareStatus) => {
                        this.contentGrid.reload(response);
                    });

            });

            this.getEl().addClass("sort-content-dialog");

            this.sortAction = new SaveSortedContentAction(this);

            this.setCancelAction(new api.ui.Action("Cancel", "esc"));
            this.addAction(this.sortAction);

            this.contentGrid = new app.browse.SortContentTreeGrid();
            this.contentGrid.getEl().addClass("sort-content-grid");
            this.contentGrid.onLoaded(() => {
                this.contentGrid.render(true);

                if (this.contentGrid.getContentId()) {
                    this.open();
                }
            });

            var header = new api.dom.H6El();
            header.setHtml("Sort content by selecting default sort above, or drag and drop for manual sorting");
            this.appendChild(header);
            this.appendChild(this.contentGrid);


            this.getCancelAction().onExecuted(()=> {
                this.close();
            });
            this.sortAction.onExecuted(() => {
                if (this.curChildOrder.equals(this.parentContent.getChildOrder())) {
                    this.close();
                } else {
                    new api.content.OrderContentRequest()
                        .setContentId(this.parentContent.getContentId())
                        .setChildOrder(this.curChildOrder).
                        sendAndParse().done(() => {
                            this.close();
                        });
                }
            });

            /* this.onShown(() => {
             this.contentGrid.getGrid().resizeCanvas();
             });*/

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
            super.close();
            this.remove();
            this.isOpen = false;
        }

        getContent(): ContentSummary {
            return this.parentContent;
        }
    }


}