module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import TextArea = api.ui.text.TextArea;
    import i18n = api.util.i18n;
    import editor = CKEDITOR.editor;

    export class CodeDialog extends ModalDialog {

        private textArea: TextArea;

        constructor(editor: editor) {
            super(<HtmlAreaModalDialogConfig>{editor: editor, title: i18n('dialog.sourcecode.title'), cls: 'source-code-modal-dialog'});
        }

        protected layout() {
            super.layout();

            this.textArea = new TextArea('source-textarea');
            this.appendChildToContentPanel(this.textArea);
        }

        open() {
            super.open();

            this.textArea.setValue(this.getEditor().getSnapshot());
            this.getEl().setAttribute('spellcheck', 'false');
            this.resetHeight();
            this.textArea.giveFocus();
            this.centerMyself();
        }

        private resetHeight() {
            const size: any = CKEDITOR.document.getWindow().getViewPaneSize();
            const height: number = Math.min(size.height, 500);

            this.textArea.getEl().setMinHeightPx(height);
            this.textArea.getEl().setMaxHeightPx(height);
        }

        protected initializeActions() {
            const okAction: api.ui.Action = new api.ui.Action(i18n('action.ok'));

            this.addAction(okAction.onExecuted(() => {
                this.getEditor().focus();
                this.getEditor().setData(this.textArea.getValue());
                this.close();
            }));

            super.initializeActions();
        }
    }
}
