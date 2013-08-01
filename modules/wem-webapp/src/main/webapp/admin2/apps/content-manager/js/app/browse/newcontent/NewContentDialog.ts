module app_browse_newcontent {

    export class NewContentDialog extends api_ui_dialog.ModalDialog {

        private cancelAction:api_ui.Action = new CancelNewContentDialog();

        private recentList:RecentContentTypesList;

        private recommendedList:RecommendedContentTypesList;

        private allList:AllContentTypesList;

        constructor() {
            super({
                title: "Select Content Type",
                width: 800,
                height: 520
            });

            this.getEl().addClass("new-dialog");


            this.recommendedList = new RecommendedContentTypesList("block recommended");
            this.recentList = new RecentContentTypesList("block recent");

            var leftColumn = new api_dom.DivEl().setClass("column column-left");
            leftColumn.appendChild(this.recommendedList);
            leftColumn.appendChild(this.recentList);
            this.appendChildToContentPanel(leftColumn);

            this.allList = new AllContentTypesList("column column-right block all");
            this.appendChildToContentPanel(this.allList);

            this.setCancelAction(this.cancelAction);
            this.cancelAction.addExecutionListener(()=> {
                this.close();
            });

            api_dom.Body.get().appendChild(this);

        }

        show() {
            this.recentList.refresh();
            this.recommendedList.setNodes(this.recentList.getNodes());
            super.show();
        }
    }

    export class CancelNewContentDialog extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }

}