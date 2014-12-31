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
            this.appendChild(this.contentPath);
            this.appendChild(descMessage);

            this.contentComboBox = new api.content.ContentMoveComboBox();
            this.contentComboBox.addClass("content-selector");
            this.appendChild(this.contentComboBox);

            this.initializeActions();

            this.contentMoveMask = new api.ui.mask.LoadMask(this);
            this.appendChild(this.contentMoveMask);

            OpenMoveDialogEvent.on((event) => {

                this.movedContentSummary = event.getContent();
                this.contentComboBox.setFilterContentPath(this.movedContentSummary.getPath());

                this.contentComboBox.loader.load();
                this.contentPath.setHtml(event.getContent().getPath().toString());

                this.open();
            });
        }


        private initializeActions() {

            this.setCancelAction(new api.ui.Action("Cancel", "esc").onExecuted(() => {
                this.close();
            }));

            this.addAction(new api.ui.Action("Move", "").onExecuted(() => {

                this.contentMoveMask.show();

                var parentContent = this.contentComboBox.getSelectedDisplayValues()[0];

                new api.content.GetContentByIdRequest(this.movedContentSummary.getContentId()).sendAndParse().done((content: api.content.Content) => {

                    if (content.isInheritPermissionsEnabled()) {
                        //disable permission inheritance
                        new api.content.ApplyContentPermissionsRequest().
                            setId(this.movedContentSummary.getId()).
                            setInheritPermissions(false).
                            setOverwriteChildPermissions(false).
                            setPermissions(content.getPermissions()).
                            sendAndParse().done(() => {
                                this.moveContent(parentContent);
                            });
                    } else {
                        this.moveContent(parentContent);
                    }
                });


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
                });
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