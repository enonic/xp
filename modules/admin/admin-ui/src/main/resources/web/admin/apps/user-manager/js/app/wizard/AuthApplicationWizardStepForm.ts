module app.wizard {

    import Principal = api.security.Principal;
    import FormItemBuilder = api.ui.form.FormItemBuilder;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class AuthApplicationWizardStepForm extends api.app.wizard.WizardStepForm {

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
            console.log("createFormView");
            var formBuilder = new api.form.FormBuilder().
                addFormItem(new api.form.InputBuilder().
                    setName("authConfig").
                    setInputType(new api.form.InputTypeName("AuthApplicationSelector", false)).
                    setLabel("Application").
                    setOccurrences(new api.form.OccurrencesBuilder().setMinimum(1).setMaximum(1).build()).
                    setInputTypeConfig({}).
                    setMaximizeUIInputWidth(true).
                    build());

            this.propertySet = new api.data.PropertyTree().getRoot();
            var authConfig = pathGuard.getAuthConfig();
            if (authConfig) {
                var authConfigPropertySet = new api.data.PropertySet();
                authConfigPropertySet.addString("applicationKey", authConfig.getApplicationKey().toString())
                authConfigPropertySet.addPropertySet("config", authConfig.getConfig().getRoot())
                this.propertySet.addPropertySet("authConfig", authConfigPropertySet);
            }

            return new api.form.FormView(api.form.FormContext.create().build(), formBuilder.build(), this.propertySet);
        }

        getAuthConfig(): api.security.AuthConfig {
            var authConfigPropertySet = this.propertySet.getPropertySet("authConfig");
            if (authConfigPropertySet) {
                var applicationKey = api.application.ApplicationKey.fromString(authConfigPropertySet.getString("applicationKey"));
                var config = new api.data.PropertyTree(authConfigPropertySet.getPropertySet("config"))
                return api.security.AuthConfig.create().
                    setApplicationKey(applicationKey).
                    setConfig(config).
                    build();
            }

            return null;
        }

        giveFocus(): boolean {
            return this.formView.giveFocus();
        }
    }
}