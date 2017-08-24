module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import i18n = api.util.i18n;
    import HTMLAreaEditor = CKEDITOR.editor;

    export class AnchorModalDialog extends ModalDialog {

        constructor(editor: HTMLAreaEditor) {

            super(<HtmlAreaModalDialogConfig>{editor: editor, title: i18n('dialog.anchor.title')});
        }

        protected getMainFormItems(): FormItem[] {
            let formItemBuilder = new ModalDialogFormItemBuilder('name', i18n('dialog.anchor.formitem.name')).setValidator(
                Validators.required);
            let nameField = this.createFormItem(formItemBuilder);

            this.setFirstFocusField(nameField.getInput());

            return [nameField];
        }

        protected initializeActions() {
            let submitAction = new api.ui.Action(i18n('action.insert'));
            this.setSubmitAction(submitAction);

            this.addAction(submitAction.onExecuted(() => {
                if (this.validate()) {
                    this.insertAnchor();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private createAnchorEl(): string {
            let anchorEl = new api.dom.AEl();

            anchorEl.setId(this.getName());
            anchorEl.getEl().removeAttribute('href');

            return '<p>&nbsp;' + anchorEl.toString() + '</p>';
        }

        private getName(): string {
            return (<api.ui.text.TextInput>this.getFieldById('name')).getValue();
        }

        private insertAnchor(): void {
            let anchorEl = this.createAnchorEl();
            //this.getEditor().insertContent(anchorEl);
        }
    }
}
