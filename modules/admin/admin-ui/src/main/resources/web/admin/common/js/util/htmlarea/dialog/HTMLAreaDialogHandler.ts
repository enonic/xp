module api.util.htmlarea.dialog {

    export class HTMLAreaDialogHandler {

        private static modalDialog: ModalDialog;

        static createAndOpenDialog(event: CreateHtmlAreaDialogEvent): ModalDialog {
            let modalDialog;

            switch (event.getType()) {
            case HtmlAreaDialogType.ANCHOR:
                modalDialog = this.openAnchorDialog(event.getConfig());
                break;
            case HtmlAreaDialogType.IMAGE:
                modalDialog = this.openImageDialog(event.getConfig(), event.getContent());
                break;
            case HtmlAreaDialogType.LINK:
                modalDialog = this.openLinkDialog(event.getConfig(), event.getContent());
                break;
            }

            if (modalDialog) {
                this.modalDialog = modalDialog;
                modalDialog.onHidden(() => {
                    this.modalDialog = null;
                });
            }

            return this.modalDialog;
        }

        static getOpenDialog(): ModalDialog {
            return this.modalDialog;
        }

        private static openLinkDialog(config: HtmlAreaAnchor, content: api.content.ContentSummary): ModalDialog {
            return this.openDialog(new LinkModalDialog(config, content));
        }

        private static openImageDialog(config: HtmlAreaImage, content: api.content.ContentSummary): ModalDialog {
            return this.openDialog(new ImageModalDialog(config, content));
        }

        private static openAnchorDialog(editor: HtmlAreaEditor): ModalDialog {
            return this.openDialog(new AnchorModalDialog(editor));
        }

        private static openDialog(dialog: ModalDialog): ModalDialog {
            dialog.open();
            return dialog;
        }
    }
}