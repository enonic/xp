module app.wizard {
    export class PublishContentDialog extends api.ui.dialog.ModalDialog {

        private publishAction:PublishAction;

        private itemList:PublishDialogItemList = new PublishDialogItemList();
        private content:api.content.Content;
        private compareResult:api.content.CompareContentResult;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Publish Wizard")
            });
            this.getEl().addClass("publish-content-dialog");

            this.appendChildToContentPanel(this.itemList);

            this.publishAction = new PublishAction();

            this.addAction(this.publishAction);

            this.setCancelAction(new api.ui.Action("Cancel", "esc"));

            this.getCancelAction().onExecuted(()=> {
                this.close();
            });

            this.publishAction.onExecuted(() => {
                new api.content.PublishContentRequest(this.content.getId()).sendAndParse().done((content:api.content.Content) => {
                    api.notify.showFeedback('Content [' + content.getDisplayName() + '] published!');
                    this.close();
                });

            });

            OpenPublishDialogEvent.on((event) => {
                this.content = event.getContent();
                new api.content.CompareContentRequest(this.content.getContentId()).sendAndParse().done((result) => {
                    console.log("compareContentRequest", arguments);
                    this.compareResult = result;
                    this.open();
                });

            });
        }

        open() {
            var publishItem = new PublishDialogItemComponent(this.content, this.compareResult);
            this.itemList.clear();
            this.itemList.appendChild(publishItem);
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

    class PublishDialogItemList extends api.dom.DivEl {
        constructor() {
            super();
            this.getEl().addClass("item-list");
        }

        clear() {
            this.removeChildren();
        }
    }


    class PublishDialogItemComponent extends api.dom.DivEl {
        constructor(content: api.content.Content, compareResult: api.content.CompareContentResult) {
            super();
            this.getEl().addClass("item");

            var icon = new api.dom.ImgEl(content.getIconUrl());
            this.appendChild(icon);

            var displayName = new api.dom.H4El();
            displayName.getEl().setInnerHtml(content.getDisplayName());
            this.appendChild(displayName);

            var compareResultEl = new api.dom.DivEl("staus");
            compareResultEl.getEl().setInnerHtml(""+api.content.CompareStatus[compareResult.compareStatus]);
            this.appendChild(compareResultEl);
        }
    }

    export class PublishAction extends api.ui.Action {

        constructor() {
            super("Publish now");
        }
    }
}