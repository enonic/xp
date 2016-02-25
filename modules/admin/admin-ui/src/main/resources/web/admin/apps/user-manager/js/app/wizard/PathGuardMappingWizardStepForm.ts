module app.wizard {
    export class PathGuardMappingWizardStepForm extends api.app.wizard.WizardStepForm {

        private textInput: api.ui.text.TextInput;

        constructor() {
            super();

            this.textInput = new api.ui.text.TextInput("middle");

            var formItem = new api.ui.form.FormItemBuilder(this.textInput).setLabel("Protected resources").
                setValidator(api.ui.form.Validators.required).
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(formItem);
            var form = new api.ui.form.Form().add(fieldSet);
            form.onFocus((event) => {
                this.notifyFocused(event);
            });
            form.onBlur((event) => {
                this.notifyBlurred(event);
            });
            form.onValidityChanged((event: api.ValidityChangedEvent) => {
                this.notifyValidityChanged(new api.app.wizard.WizardStepValidityChangedEvent(event.isValid()));
            });
            this.appendChild(form);
        }

        layout(pathGuard: api.security.PathGuard) {
            var paths = pathGuard.getPaths();
            if (paths && paths.length > 0) {
                this.textInput.setValue(paths[0]);
            } else {
                this.textInput.reset();
            }
        }

        isValid(): boolean {
            return this.textInput.isValid();
        }

        getPaths(): string[] {
            return this.textInput.getValue() ? [this.textInput.getValue()] : []; //TODO
        }

        giveFocus(): boolean {
            return this.textInput.giveFocus();
        }
    }
}