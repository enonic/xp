module app.wizard {

    import Principal = api.security.Principal;
    import FormItemBuilder = api.ui.form.FormItemBuilder;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class AuthApplicationWizardStepForm extends api.app.wizard.WizardStepForm {

        private appCombobox: api.application.ApplicationComboBox;

        constructor() {
            super();

            this.appCombobox = new api.application.ApplicationComboBox(1);

            var authApplicationFormItem = new FormItemBuilder(this.appCombobox).
                setLabel('Authentication').
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(authApplicationFormItem);

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

        layout(userStore: api.security.UserStore) {
            console.log("AuthApplicationWizardStepForm: " + userStore.getAuthApplication());
        }

        getApplication(): string {
            if (this.appCombobox.countSelected() == 0) {
                return null;
            }
            return this.appCombobox.getSelectedDisplayValues()[0].
                getApplicationKey().
                toString();
        }

        giveFocus(): boolean {
            return this.appCombobox.giveFocus();
        }
    }
}
