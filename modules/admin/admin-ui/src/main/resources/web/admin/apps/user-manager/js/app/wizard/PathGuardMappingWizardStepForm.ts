module app.wizard {
    export class PathGuardMappingWizardStepForm extends api.app.wizard.WizardStepForm {

        private formView: api.form.FormView;

        private propertySet: api.data.PropertySet;

        constructor() {
            super();
        }

        layout(pathGuard: api.security.PathGuard) {

            var formBuilder = new api.form.FormBuilder().
                addFormItem(new api.form.InputBuilder().
                    setName("paths").
                    setInputType(api.form.inputtype.text.TextLine.getName()).
                    setLabel("Protected resources").
                    setOccurrences(new api.form.OccurrencesBuilder().setMinimum(0).setMaximum(2).build()).
                    setInputTypeConfig({}).
                    build());

            this.propertySet = new api.data.PropertySet()
            var paths = pathGuard.getPaths();
            if (paths && paths.length > 0) {
                this.propertySet.addString("paths", paths[0]);
            }


            this.formView = new api.form.FormView(api.form.FormContext.create().build(), formBuilder.build(), this.propertySet);
            return this.formView.layout().then(() => {

                this.formView.onFocus((event) => {
                    this.notifyFocused(event);
                });
                this.formView.onBlur((event) => {
                    this.notifyBlurred(event);
                });

                this.appendChild(this.formView);

                this.formView.onValidityChanged((event: api.form.FormValidityChangedEvent) => {
                    this.previousValidation = event.getRecording();
                    this.notifyValidityChanged(new api.app.wizard.WizardStepValidityChangedEvent(event.isValid()));
                });

                var formViewValid = this.formView.isValid();
                this.notifyValidityChanged(new api.app.wizard.WizardStepValidityChangedEvent(formViewValid));
            });
        }

        getPaths(): string[] {
            return this.propertySet ? this.propertySet.getStrings("paths") : [];
        }

        giveFocus(): boolean {
            return this.formView.giveFocus();
        }
    }
}