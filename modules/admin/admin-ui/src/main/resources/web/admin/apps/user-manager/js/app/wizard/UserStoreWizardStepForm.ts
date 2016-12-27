import Principal = api.security.Principal;
import FormItemBuilder = api.ui.form.FormItemBuilder;

import DivEl = api.dom.DivEl;
import LabelEl = api.dom.LabelEl;


export class UserStoreWizardStepForm extends api.app.wizard.WizardStepForm {

    private formView: api.form.FormView;

    private propertySet: api.data.PropertySet;

    constructor() {
        super();
    }

    layout(userStore?: api.security.UserStore): wemQ.Promise<void> {

        this.formView = this.createFormView(userStore);

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

    private createFormView(userStore?: api.security.UserStore): api.form.FormView {
        var isSystemUserStore = (!!userStore && userStore.getKey().isSystem()).toString();
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
                setName("authConfig").
                setInputType(new api.form.InputTypeName("AuthApplicationSelector", false)).setLabel("ID Provider").setOccurrences(
            new api.form.OccurrencesBuilder().setMinimum(0).setMaximum(1).build()).setInputTypeConfig(
            {readOnly: [{value: isSystemUserStore}]}).
                setMaximizeUIInputWidth(true).
                build());

        this.propertySet = new api.data.PropertyTree().getRoot();
        if (userStore) {
            this.propertySet.addString("description", userStore.getDescription());
            var authConfig = userStore.getAuthConfig();
            if (authConfig) {
                var authConfigPropertySet = new api.data.PropertySet();
                authConfigPropertySet.addString("applicationKey", authConfig.getApplicationKey().toString());
                authConfigPropertySet.addPropertySet("config", authConfig.getConfig().getRoot());
                this.propertySet.addPropertySet("authConfig", authConfigPropertySet);
            }
        }

        return new api.form.FormView(api.form.FormContext.create().build(), formBuilder.build(), this.propertySet);
    }

    public validate(silent?: boolean): api.form.ValidationRecording {
        return this.formView.validate(silent);
    }

    getAuthConfig(): api.security.AuthConfig {
        var authConfigPropertySet = this.propertySet.getPropertySet("authConfig");
        if (authConfigPropertySet) {
            var applicationKey = api.application.ApplicationKey.fromString(authConfigPropertySet.getString("applicationKey"));
            var config = new api.data.PropertyTree(authConfigPropertySet.getPropertySet("config"));
            return api.security.AuthConfig.create().
                setApplicationKey(applicationKey).
                setConfig(config).
                build();
        }

        return null;
    }

    getDescription(): string {
        return this.propertySet.getString("description");
    }

    giveFocus(): boolean {
        return this.formView.giveFocus();
    }
}
