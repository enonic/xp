module app_new {

    export class NewContentDialog extends api_ui_dialog.ModalDialog {

        private parentContent:api_content.Content;

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
            this.recommendedList.addListener({
                onSelected: (contentTypeListItem:ContentTypeListItem) => {
                    this.closeAndIssueNewContentEvent(contentTypeListItem);
                }
            });

            this.recentList = new RecentContentTypesList("block recent");
            this.recentList.addListener({
                onSelected: (contentTypeListItem:ContentTypeListItem) => {
                    this.closeAndIssueNewContentEvent(contentTypeListItem);
                }
            });

            var leftColumn = new api_dom.DivEl().setClass("column column-left");
            leftColumn.appendChild(this.recommendedList);
            leftColumn.appendChild(this.recentList);
            this.appendChildToContentPanel(leftColumn);

            this.allList = new app_new.AllContentTypesList("column column-right block all");
            this.allList.addListener({
                onSelected: (contentTypeListItem:ContentTypeListItem) => {
                    this.closeAndIssueNewContentEvent(contentTypeListItem);
                }
            });

            this.appendChildToContentPanel(this.allList);

            this.setCancelAction(new CancelNewContentDialog());
            this.getCancelAction().addExecutionListener(()=> {
                this.close();
            });

            api_dom.Body.get().appendChild(this);
        }

        private closeAndIssueNewContentEvent(contentTypeListItem:ContentTypeListItem) {
            this.close();
            new NewContentEvent(contentTypeListItem.getContentType(), this.parentContent, contentTypeListItem.isSiteRoot()).fire();
        }

        setParentContent(value:api_content.Content) {
            this.parentContent = value;
        }

        show() {

            super.show();

            SiteRootContentTypes.load((siteRootContentTypes:SiteRootContentTypes)=>{
                ContentTypes.load((contentTypes:ContentTypes)=>{

                    this.recentList.setContentTypes(contentTypes, siteRootContentTypes);
                    this.recommendedList.setContentTypes(contentTypes, siteRootContentTypes);
                    this.allList.setContentTypes(contentTypes, siteRootContentTypes);
                });
            });
        }
    }

    export class CancelNewContentDialog extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }

}