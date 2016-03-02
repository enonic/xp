module api.util.htmlarea.dialog {

    export class HTMLAreaDialogHandler {

        static createAndOpenDialog(event: CreateHtmlAreaDialogEvent): ModalDialog {
            let modalDialog;

            switch (event.getType()) {
            case HtmlAreaDialogType.ANCHOR:
                modalDialog = this.openAnchorDialog(event.getConfig());
                break;
            case HtmlAreaDialogType.IMAGE:
                modalDialog = this.openImageDialog(event.getConfig(), event.getContentId());
                break;
            case HtmlAreaDialogType.LINK:
                modalDialog = this.openLinkDialog(event.getConfig());
                break;
            }
            return modalDialog;
        }

        private static openLinkDialog(config: HtmlAreaAnchor): ModalDialog {
            return this.openDialog(new LinkModalDialog(config));
        }

        private static openImageDialog(config: HtmlAreaImage, contentId: api.content.ContentId): ModalDialog {
            return this.openDialog(new ImageModalDialog(config, contentId));
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