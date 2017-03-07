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
            case HtmlAreaDialogType.MACRO:
                modalDialog = this.openMacroDialog(event.getConfig(), event.getContent(), event.getApplicationKeys());
                break;
            case HtmlAreaDialogType.SEARCHANDREPLACE:
                modalDialog = this.openSearchAndReplaceDialog(event.getConfig());
                break;
            case HtmlAreaDialogType.SOURCE:
                modalDialog = this.openSourceDialog(event.getConfig());
                break;
            case HtmlAreaDialogType.CHARMAP:
                modalDialog = this.openCharMapDialog(event.getConfig());
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

        private static openMacroDialog(config: HtmlAreaMacro, content: api.content.ContentSummary,
                                       applicationKeys: api.application.ApplicationKey[]): ModalDialog {
            return this.openDialog(new MacroModalDialog(config, content, applicationKeys));
        }

        private static openSearchAndReplaceDialog(editor: HtmlAreaEditor): ModalDialog {
            return this.openDialog(new SearchAndReplaceModalDialog(editor));
        }

        private static openSourceDialog(editor: HtmlAreaEditor): ModalDialog {
            return this.openDialog(new SourceDialog(editor));
        }

        private static openCharMapDialog(editor: HtmlAreaEditor): ModalDialog {
            return this.openDialog(new CharMapDialog(editor));
        }

        private static openDialog(dialog: ModalDialog): ModalDialog {
            dialog.open();
            return dialog;
        }
    }
}
