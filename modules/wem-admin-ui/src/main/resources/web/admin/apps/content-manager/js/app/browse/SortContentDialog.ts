module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import Element = api.dom.Element;
    import SaveSortedContentAction = action.SaveSortedContentAction;

    export class SortContentDialog extends api.ui.dialog.ModalDialog {

        private sortAction: SaveSortedContentAction;

        private contentGrid: app.browse.SortContentTreeGrid;

        constructor() {
            var menu = new api.ui.tab.TabMenu();
            var tabMenuItem = new api.ui.tab.TabMenuItemBuilder().setLabel("(sorting type)").build();
            tabMenuItem.setActive(true);
            menu.addNavigationItem(tabMenuItem);
            menu.selectNavigationItem(0);
//            menu.setButtonLabel("fdsfdsf");
            menu.show();

            super({
                title: new api.ui.dialog.ModalDialogHeader("")
            });

            this.appendChild(menu);

            this.getEl().addClass("sort-content-dialog");

            this.sortAction = new SaveSortedContentAction();

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

            OpenSortDialogEvent.on((event) => {
                api.content.ContentSummaryAndCompareStatusFetcher.fetch(event.getContent().getContentId()).
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

            /*this.onShown(() => {
             this.contentGrid.getGrid().resizeCanvas();
             });*/
        }

        open() {
            super.open();
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            super.close();
            this.remove();
        }
    }


}