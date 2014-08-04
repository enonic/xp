module app.wizard {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

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
//                    this.publishAction.setToBePublishedAmout(selectedRows.length);
//                });
                this.publishList.clear();
                this.publishList.appendChild(new PublishDialogItemComponent(this.content));
                this.open();
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

    export class PublishAction extends api.ui.Action {

        private static BASE_STRING:string = "Publish now";

        constructor() {
            super(PublishAction.BASE_STRING);
        }

        setToBePublishedAmout(amount:number) {
            if (amount < 1) {
                this.setEnabled(false);
            } else {
                this.setEnabled(true);

            }
            this.setLabel(PublishAction.BASE_STRING + " (" + amount +")")
        }
    }

    export class ScheduleAction extends api.ui.Action {
        constructor() {
            super("Schedule");
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
        constructor(content: api.content.Content) {
            super();
            this.getEl().addClass("item");

            var icon = new api.dom.ImgEl(content.getIconUrl());
            this.appendChild(icon);

            var displayName = new api.dom.H4El();
            displayName.getEl().setInnerHtml(content.getDisplayName());
            this.appendChild(displayName);
        }
    }
}