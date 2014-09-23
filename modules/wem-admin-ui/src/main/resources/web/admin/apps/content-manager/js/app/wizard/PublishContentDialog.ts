module app.wizard {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;

    export class PublishContentDialog extends api.ui.dialog.ModalDialog {

        private publishAction: PublishAction;
        private scheduleAction: ScheduleAction;

        private grid: CompareContentGrid;
        private content: api.content.Content;
        private compareResult: api.content.CompareContentResults;
        private publishList: PublishDialogItemList = new PublishDialogItemList();

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Publish Wizard")
            });
            this.getEl().addClass("publish-content-dialog");
            this.appendChildToContentPanel(this.publishList);

            this.publishAction = new PublishAction();
            this.scheduleAction = new ScheduleAction();

            this.addAction(this.publishAction);
            this.addAction(this.scheduleAction);

            this.setCancelAction(new api.ui.Action("Cancel", "esc"));

            this.getCancelAction().onExecuted(()=> {
                this.close();
            });

            this.publishAction.onExecuted(() => {
                new api.content.PublishContentRequest(this.content.getId()).sendAndParse().done((content: api.content.Content) => {
                    api.notify.showSuccess('Content [' + content.getDisplayName() + '] published!');
                    this.close();
                });

            });

            OpenPublishDialogEvent.on((event) => {
                this.content = event.getContent();
                //this.grid = new CompareContentGrid(event.getContent());
                //this.grid.selectAll();
//                this.grid.onRowSelectionChanged((selectedRows:TreeNode<ContentSummaryAndCompareStatus>[]) => {
//                    this.publishAction.setToBePublishedAmount(selectedRows.length);
//                });
                this.publishList.clear();
                var req = api.content.CompareContentRequest.fromContentSummaries([this.content]);
                var res = req.sendAndParse();
                res.done((results: api.content.CompareContentResults) => {
                    this.publishList.appendChild(new PublishDialogItemComponent(this.content,
                        results.get(this.content.getContentId().toString()).getCompareStatus()));
                    this.open();
                });
            });

        }

        open() {
            //this.appendChildToContentPanel(this.grid);
            super.open();
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            //this.removeChildFromContentPanel(this.grid);
            super.close();
            this.remove();
        }
    }

    export class PublishDialogItemList extends api.dom.DivEl {
        constructor() {
            super();
            this.getEl().addClass("item-list");
        }

        clear() {
            this.removeChildren();
        }
    }

    class PublishDialogItemComponent extends api.dom.DivEl {

        constructor(content: api.content.Content, compareStatus: api.content.CompareStatus) {
            super();
            this.getEl().addClass("item");

            var icon = new api.dom.ImgEl(new ContentIconUrlResolver().setContent(content).resolve());
            this.appendChild(icon);

            var displayName = new api.dom.H4El();
            displayName.getEl().setInnerHtml(content.getDisplayName());

            var compareStatusEl = new api.dom.SpanEl();
            compareStatusEl.getEl().setInnerHtml(api.content.CompareStatus[compareStatus] + "");
            displayName.appendChild(compareStatusEl);

            this.appendChild(displayName);
        }
    }
}