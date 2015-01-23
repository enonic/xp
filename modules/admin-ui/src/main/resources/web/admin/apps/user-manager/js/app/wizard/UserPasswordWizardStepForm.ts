module app.wizard {

    import User = api.security.User;
    import Principal = api.security.Principal;

    import PasswordGenerator = api.ui.text.PasswordGenerator;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import Validators = api.ui.form.Validators;


    export class UserPasswordWizardStepForm extends api.app.wizard.WizardStepForm {

        private password: PasswordGenerator;

        constructor() {
            super();

            this.password = new PasswordGenerator();

            var passwordFormItem = new FormItemBuilder(this.password).
                setLabel('Password').
                setValidator(Validators.required).
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(passwordFormItem);

            var form = new api.ui.form.Form().add(fieldSet);

            form.onFocus((event) => {
                this.notifyFocused(event);
            });
            form.onBlur((event) => {
                this.notifyBlurred(event);
            });

            this.appendChild(form);
        }

        layout(principal: Principal) {
//            this.password.setValue(principal.asUser().getPassword());
        }

        isValid(): boolean {
            return !!this.password.getValue();
        }

        getPassword(): string {
            return this.password.getValue();
        }

        giveFocus(): boolean {
            return this.password.giveFocus();
        }
    }
}
