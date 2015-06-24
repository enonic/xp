module api.form.inputtype.text.tiny {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;

    export class AnchorModalDialog extends ModalDialog {

        private anchor: HTMLElement;

        constructor(config: api.form.inputtype.text.TinyMCEAnchor) {
            this.anchor = config.element;

            super(config.editor, new api.ui.dialog.ModalDialogHeader("Insert Anchor"));
        }

        protected getMainFormItems(): FormItem[] {
            var nameField = this.createFormItem("name", "Name", Validators.required, this.getAnchorName());

            this.setFirstFocusField(nameField.getInput());

            return [
                nameField
            ];
        }

        protected initializeActions() {
            this.addAction(new api.ui.Action(this.anchor ? "Update" : "Insert").onExecuted(() => {
                if (this.validate()) {
                    this.createAnchor();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private createAnchorEl(): api.dom.AEl {
            var anchorEl = new api.dom.AEl();

            anchorEl.setId(this.getName());
            anchorEl.getEl().removeAttribute('href');

            return anchorEl;
        }

        private getAnchorName(): string {
            return this.anchor ? this.anchor.getAttribute("id") : "";
        }

        private getName(): string {
            return (<api.ui.text.TextInput>this.getFieldById("name")).getValue();
        }

        private createAnchor(): void {
            if (this.anchor) {
                this.anchor.setAttribute("id", this.getName());
            }
            else {
                var anchorEl = this.createAnchorEl();
                this.getEditor().insertContent(anchorEl.toString());
            }
        }
    }
}