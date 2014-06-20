module app.wizard {
    export class PublishContentDialog extends api.ui.dialog.ModalDialog {

        private publishAction:PublishAction;

        private grid:CompareContentGrid;
        private content:api.content.Content;
        private compareResult:api.content.CompareContentResult;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Publish Wizard")
            });
            this.getEl().addClass("publish-content-dialog");

            this.publishAction = new PublishAction();

            this.addAction(this.publishAction);

            this.setCancelAction(new api.ui.Action("Cancel", "esc"));

            this.getCancelAction().onExecuted(()=> {
                this.close();
            });

            this.publishAction.onExecuted(() => {
                new api.content.PublishContentRequest(this.content.getId()).sendAndParse().done((content:api.content.Content) => {
                    api.notify.showSuccess('Content [' + content.getDisplayName() + '] published!');
                    this.close();
                });

            });

            OpenPublishDialogEvent.on((event) => {
                this.content = event.getContent();
                this.grid = new CompareContentGrid(event.getContent());
                this.open();
            });

        }

        open() {
            this.appendChildToContentPanel(this.grid);
            super.open();
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            this.removeChildFromContentPanel(this.grid);
            super.close();
            this.remove();
        }
    }

    export class PublishAction extends api.ui.Action {

        constructor() {
            super("Publish now");
        }
    }
}