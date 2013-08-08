module app_new {

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

            this.getEl().addClass("new-content-dialog");


            this.recommendedList = new RecommendedContentTypesList("block recommended");
            this.recommendedList.addSelectedListener((selectedContentType:api_remote_contenttype.ContentType) => {
                this.closeAndIssueNewContentEvent(selectedContentType);
            });

            this.recentList = new RecentContentTypesList("block recent");
            this.recentList.addSelectedListener((selectedContentType:api_remote_contenttype.ContentType) => {
                this.closeAndIssueNewContentEvent(selectedContentType);
            });

            var leftColumn = new api_dom.DivEl().setClass("column column-left");
            leftColumn.appendChild(this.recommendedList);
            leftColumn.appendChild(this.recentList);
            this.appendChildToContentPanel(leftColumn);

            this.allList = new AllContentTypesList("column column-right block all");
            this.allList.addSelectedListener((selectedContentType:api_remote_contenttype.ContentType) => {
                this.closeAndIssueNewContentEvent(selectedContentType);
            });

            this.appendChildToContentPanel(this.allList);

            this.setCancelAction(this.cancelAction);
            this.cancelAction.addExecutionListener(()=> {
                this.close();
            });

            api_dom.Body.get().appendChild(this);
        }

        private closeAndIssueNewContentEvent(contentType:api_remote_contenttype.ContentType) {
            this.close();
            new NewContentEvent(contentType).fire();
        }

        show() {
            this.recentList.refresh();
            this.recommendedList.refresh();
            this.allList.refresh();
            super.show();
        }
    }

    export class CancelNewContentDialog extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }

}