module api.util.htmlarea.dialog {

    import HTMLAreaEditor = CKEDITOR.editor;

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
            case HtmlAreaDialogType.SEARCHREPLACE:
                modalDialog = this.openSearchReplaceDialog(event.getConfig());
                break;
            case HtmlAreaDialogType.CODE:
                modalDialog = this.openCodeDialog(event.getConfig());
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

        private static openAnchorDialog(editor: HTMLAreaEditor): ModalDialog {
            return this.openDialog(new AnchorModalDialog(editor));
        }

        private static openMacroDialog(config: HtmlAreaMacro, content: api.content.ContentSummary,
                                       applicationKeys: api.application.ApplicationKey[]): ModalDialog {
            return this.openDialog(new MacroModalDialog(config, content, applicationKeys));
        }

        private static openSearchReplaceDialog(editor: HTMLAreaEditor): ModalDialog {
            return this.openDialog(new SearchReplaceModalDialog(editor));
        }

        private static openCodeDialog(editor: HTMLAreaEditor): ModalDialog {
            return this.openDialog(new CodeDialog(editor));
        }

        private static openCharMapDialog(editor: HTMLAreaEditor): ModalDialog {
            return this.openDialog(new CharMapDialog(editor));
        }

        private static openDialog(dialog: ModalDialog): ModalDialog {
            dialog.open();
            return dialog;
        }
    }
}
