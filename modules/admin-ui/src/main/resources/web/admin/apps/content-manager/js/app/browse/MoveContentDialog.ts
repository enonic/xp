module app.browse {

    import ContentPath = api.content.ContentPath;
    import ContentType = api.schema.content.ContentType;
    import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
    import ContentSummary = api.content.ContentSummary;
    import ContentResponse = api.content.ContentResponse;
    import ContentIds = api.content.ContentIds;
    import MoveContentResult = api.content.MoveContentResult;
    import MoveContentResultFailure = api.content.MoveContentResultFailure;


    export class MoveContentDialog extends api.ui.dialog.ModalDialog {

        private contentComboBox: api.content.ContentMoveComboBox;

        private movedContentSummaries: api.content.ContentSummary[];

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

                this.movedContentSummaries = event.getContentSummaries();

                if (event.getContentSummaries().length == 1) {
                    var contentToMove = event.getContentSummaries()[0];

                    new GetContentTypeByNameRequest(contentToMove.getType()).sendAndParse().then((contentType: ContentType) => {

                        this.contentComboBox.setFilterContentPath(contentToMove.getPath());
                        this.contentComboBox.setFilterSourceContentType(contentType);
                        this.contentPath.setHtml(contentToMove.getPath().toString());

                        this.open();
                    }).catch((reason)=> {
                        api.notify.showError(reason.getMessage());
                    }).done();
                } else {
                    this.contentComboBox.setFilterContentPath(null);
                    this.contentPath.setHtml("");
                    this.open();
                }

            });

            this.addCancelButtonToBottom();
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

            var contentIds = ContentIds.create().
                fromContentIds(this.movedContentSummaries.map(summary => summary.getContentId())).
                build();

            new api.content.MoveContentRequest(contentIds, parentRoot).
                sendAndParse().then((response: MoveContentResult) => {
                    if (parentContent) {
                        this.contentComboBox.deselect(parentContent);
                    }
                    this.contentMoveMask.hide();

                    if (response.getMoved().length > 0) {
                        if (response.getMoved().length > 1) {
                            api.notify.showFeedback(response.getMoved().length + ' items moved');
                        } else {
                            api.notify.showFeedback("\"" + response.getMoved()[0] + '\" moved');
                        }
                    }

                    response.getMoveFailures().forEach((failure: MoveContentResultFailure) => {
                        api.notify.showWarning(failure.getReason());
                    });
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
            this.contentComboBox.giveFocus();
        }

        close() {
            super.close();
        }
    }
}