module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import TextArea = api.ui.text.TextArea;
    import Action = api.ui.Action;
    import i18n = api.util.i18n;

    export class CodeDialog extends ModalDialog {

        private textArea: TextArea;

        private okAction: Action;

        constructor(editor: HtmlAreaEditor) {
            super(<HtmlAreaModalDialogConfig>{
                editor: editor,
                title: i18n('dialog.sourcecode.title'), cls: 'source-code-modal-dialog',
                confirmation: {
                    yesCallback: () => this.okAction.execute(),
                    noCallback: () => this.close(),
                }
            });
        }

        protected layout() {
            super.layout();

            this.textArea = new TextArea('source-textarea');
            this.appendChildToContentPanel(this.textArea);
        }

        open() {
            super.open();

            this.textArea.setValue(this.getEditor().getContent({source_view: true}));
            this.getEl().setAttribute('spellcheck', 'false');
            this.resetHeight();
            this.textArea.giveFocus();
            this.centerMyself();
        }

        private resetHeight() {
            // textarea has 'height' style property updated on focus, shown events etc, overriding it gently
            const height: number = this.getEditor().getParam('code_dialog_height', Math.min(tinymce.DOM.getViewPort().h - 200, 500));
            this.textArea.getEl().setMinHeightPx(height);
            this.textArea.getEl().setMaxHeightPx(height);
        }

        protected initializeActions() {
            this.okAction = new Action(i18n('action.ok'));

            this.addAction(this.okAction.onExecuted(() => {
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

        isDirty(): boolean {
            return this.textArea.isDirty();
        }
    }
}
