module app.browse {

    import ContentPath = api.content.ContentPath;

    export class MoveContentDialog extends api.ui.dialog.ModalDialog {

        private contentComboBox: api.content.ContentMoveComboBox;

        private movedContentSummary: api.content.ContentSummary;

        private contentPath: api.dom.H6El;

        private contentMoveMask: api.ui.mask.LoadMask;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Move item with children")
            });
            this.getEl().addClass("move-content-dialog");

            this.contentPath = new api.dom.H6El().addClass("content-path");
            var descMessage = new api.dom.H6El().addClass("desc-message").
                setHtml("Moves selected items with all children and current permissions to selected destination");
            this.appendChildToContentPanel(this.contentPath);
            this.appendChildToContentPanel(descMessage);

            this.contentComboBox = new api.content.ContentMoveComboBox();
            this.contentComboBox.addClass("content-selector");
            this.appendChildToContentPanel(this.contentComboBox);

            this.initializeActions();

            this.contentMoveMask = new api.ui.mask.LoadMask(this);
            this.appendChildToContentPanel(this.contentMoveMask);

            OpenMoveDialogEvent.on((event) => {

                this.movedContentSummary = event.getContent();
                this.contentComboBox.setFilterContentPath(this.movedContentSummary.getPath());

                this.contentPath.setHtml(event.getContent().getPath().toString());

                this.open();
            });
        }


        private initializeActions() {

            this.addAction(new api.ui.Action("Move", "").onExecuted(() => {

                this.contentMoveMask.show();

                var parentContent = this.getParentContent();
                this.moveContent(parentContent);
            }));
        }

        private moveContent(parentContent: api.content.ContentSummary) {
            var parentRoot = (!!parentContent) ? parentContent.getPath() : ContentPath.ROOT;

            new api.content.MoveContentRequest(this.movedContentSummary.getContentId(), parentRoot).
                sendAndParse().then((content: api.content.Content) => {
                    if (parentContent) {
                        this.contentComboBox.deselect(parentContent);
                    }
                    this.contentMoveMask.hide();
                    new api.content.ContentMovedEvent(content.getContentId()).fire();
                    api.notify.showFeedback('Content was moved!');
                    this.close();
                }).catch((reason)=> {
                    api.notify.showWarning(reason.getMessage());
                    this.close();
                    this.contentComboBox.deselect(this.getParentContent());
                }).done();
        }

        private getParentContent(): api.content.ContentSummary {
            return this.contentComboBox.getSelectedDisplayValues()[0];
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
        }
    }
}