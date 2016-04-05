module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;

    export class AnchorModalDialog extends ModalDialog {

        constructor(editor:HtmlAreaEditor) {

            super(editor, new api.ui.dialog.ModalDialogHeader("Insert Anchor"));
        }

        protected getMainFormItems():FormItem[] {
            var nameField = this.createFormItem("name", "Name", Validators.required);

            this.setFirstFocusField(nameField.getInput());

            return [
                nameField
            ];
        }

        protected initializeActions() {
            var submitAction = new api.ui.Action("Insert");
            this.setSubmitAction(submitAction);

            this.addAction(submitAction.onExecuted(() => {
                if (this.validate()) {
                    this.insertAnchor();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private createAnchorEl():api.dom.AEl {
            var anchorEl = new api.dom.AEl();

            anchorEl.setId(this.getName());
            anchorEl.getEl().removeAttribute('href');

            return anchorEl;
        }

        private getName():string {
            return (<api.ui.text.TextInput>this.getFieldById("name")).getValue();
        }

        private insertAnchor():void {
            var anchorEl = this.createAnchorEl();
            this.getEditor().insertContent(anchorEl.toString());
        }
    }
}