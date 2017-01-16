module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;

    export class AnchorModalDialog extends ModalDialog {

        constructor(editor: HtmlAreaEditor) {

            super(editor, 'Insert Anchor');
        }

        protected getMainFormItems(): FormItem[] {
            let nameField = this.createFormItem('name', 'Name', Validators.required);

            this.setFirstFocusField(nameField.getInput());

            return [
                nameField
            ];
        }

        protected initializeActions() {
            let submitAction = new api.ui.Action('Insert');
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
            this.getEditor().insertContent(anchorEl);
        }
    }
}
