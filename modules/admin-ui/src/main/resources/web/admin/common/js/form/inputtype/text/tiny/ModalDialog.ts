module api.form.inputtype.text.tiny {

    import FormView = api.form.FormView;
    import Form = api.ui.form.Form;
    import Fieldset = api.ui.form.Fieldset;
    import FormItem = api.ui.form.FormItem;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import Validators = api.ui.form.Validators;
    import BaseDialog = api.ui.dialog.ModalDialog;

    export class ModalDialog extends BaseDialog {
        private editor: TinyMceEditor;

        constructor(editor: TinyMceEditor, title: api.ui.dialog.ModalDialogHeader) {
            super({
                title: title
            });

            this.editor = editor;
            this.getEl().addClass("tinymce-modal-dialog");

            this.layout();
            this.initializeActions();
        }

        layout() {
            throw new Error("Must be implemented by inheritors");
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        protected createForm(formItems: FormItem[]): Form {
            var form = new Form(),
                validationCls = "display-validation-errors";

            formItems.forEach((formItem: FormItem) => {
                form.add(this.createFieldSet(formItem));
                if (formItem.getValidator() && validationCls) {
                    form.addClass(validationCls);
                    validationCls = "";
                }
            });

            return form;
        }

        protected createFormPanel(formItems: FormItem[]): api.ui.panel.Panel {
            var panel = new api.ui.panel.Panel(),
                form = this.createForm(formItems);

            panel.appendChild(form);

            return panel;
        }

        private getMandatoryValidationRecording(name: string): ValidationRecording {
            var recording = new ValidationRecording(),
                validationRecordingPath = new ValidationRecordingPath(null, name, 1, 1);

            recording.breaksMinimumOccurrences(validationRecordingPath);

            return recording;
        }

        private createFieldSet(formItem: FormItem): Fieldset {
            var fieldSet = new Fieldset();

            fieldSet.addClass("modal-dialog-fieldset");
            fieldSet.add(formItem);

            if (formItem.getValidator()) {
                var validationRecordingViewer = new ValidationRecordingViewer();

                fieldSet.appendChild(validationRecordingViewer);
                fieldSet.onValidityChanged((event: ValidityChangedEvent) => {
                    var validationRecording = event.isValid() ?
                                              new ValidationRecording() :
                                              this.getMandatoryValidationRecording(formItem.getLabel().getValue());

                    validationRecordingViewer.setObject(validationRecording);
                });
            }

            return fieldSet;
        }

        protected createFormItem(label: string, required: boolean, inputEl?: api.dom.FormItemEl): FormItem {
            var formItemBuilder = new FormItemBuilder(inputEl || new api.ui.text.TextInput()).setLabel(label);

            if (required) {
                formItemBuilder.setValidator(Validators.required);
            }

            return formItemBuilder.build();
        }

        protected initializeActions() {
            this.addCancelButtonToBottom();
        }
    }
}
