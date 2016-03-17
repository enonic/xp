module app.wizard {

    import Principal = api.security.Principal;
    import FormItemBuilder = api.ui.form.FormItemBuilder;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class PathGuardWizardStepForm extends api.app.wizard.WizardStepForm {

        private formView: api.form.FormView;

        private propertySet: api.data.PropertySet;

        constructor() {
            super();
        }

        layout(pathGuard: api.security.PathGuard) {

            this.formView = this.createFormView(pathGuard);

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

        private createFormView(pathGuard: api.security.PathGuard): api.form.FormView {
            var formBuilder = new api.form.FormBuilder().
                addFormItem(new api.form.InputBuilder().
                    setName("description").
                    setInputType(api.form.inputtype.text.TextLine.getName()).
                    setLabel("Description").
                    setOccurrences(new api.form.OccurrencesBuilder().setMinimum(0).setMaximum(1).build()).
                    setInputTypeConfig({}).
                    setMaximizeUIInputWidth(true).
                    build()).
                addFormItem(new api.form.InputBuilder().
                    setName("userStoreKey").
                    setInputType(new api.form.InputTypeName("UserStoreSelector", false)).
                    setLabel("User Store").
                    setOccurrences(new api.form.OccurrencesBuilder().setMinimum(1).setMaximum(1).build()).
                    setInputTypeConfig({}).
                    setMaximizeUIInputWidth(true).
                    build()).
                addFormItem(new api.form.InputBuilder().
                    setName("passive").
                    setInputType(new api.form.InputTypeName("Checkbox", false)).
                    setLabel("Passive (will only authenticate for protected resources)").
                    setOccurrences(new api.form.OccurrencesBuilder().setMinimum(1).setMaximum(1).build()).
                    setInputTypeConfig({}).
                    setMaximizeUIInputWidth(true).
                    build());

            this.propertySet = new api.data.PropertyTree().getRoot();
            if (pathGuard) {
                this.propertySet.addString("description", pathGuard.getDescription());
                if (pathGuard.getUserStoreKey()) {
                    this.propertySet.addString("userStoreKey", pathGuard.getUserStoreKey().getId());
                }
                this.propertySet.addBoolean("passive", pathGuard.isPassive());
            }

            return new api.form.FormView(api.form.FormContext.create().build(), formBuilder.build(), this.propertySet);
        }

        getDescription(): string {
            return this.propertySet.getString("description");
        }

        isPassive(): boolean {
            return this.propertySet.getBoolean("passive");
        }

        getUserStoreKey(): api.security.UserStoreKey {
            var userStoreKey = this.propertySet.getString("userStoreKey");
            return userStoreKey ? api.security.UserStoreKey.fromString(userStoreKey) : null;
        }

        giveFocus(): boolean {
            return this.formView.giveFocus();
        }
    }
}