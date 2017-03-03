module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import TextArea = api.ui.text.TextArea;

    export class SourceDialog extends ModalDialog {

        private textArea: TextArea;

        constructor(editor: HtmlAreaEditor) {
            super(editor, 'Source code', 'source-code-modal-dialog');
        }

        protected layout() {
            super.layout();

            this.textArea = new TextArea('waaat');
            this.appendChildToContentPanel(this.textArea);
        }

        open() {
            super.open();

            this.textArea.setValue(this.getEditor().getContent({source_view: true}));
            this.textArea.getEl().setHeightPx(this.textArea.getHTMLElement().scrollHeight + 5); // resizing text area to content size
            this.centerMyself();
        }

        protected initializeActions() {
            const okAction: api.ui.Action = new api.ui.Action('Ok');

            this.addAction(okAction.onExecuted(() => {
                this.getEditor().focus();

                this.getEditor().undoManager.transact(() => {
                    this.getEditor().setContent(this.textArea.getValue());
                });

                this.getEditor().selection.setCursorLocation();
                this.getEditor().nodeChanged();

                this.close();
            }));

            super.initializeActions();
        }
    }
}