module app.wizard {

    import User = api.security.User;
    import Principal = api.security.Principal;

    import PasswordGenerator = api.ui.text.PasswordGenerator;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import Validators = api.ui.form.Validators;
    import FormItem = api.ui.form.FormItem;
    import Button = api.ui.button.Button;


    export class UserPasswordWizardStepForm extends api.app.wizard.WizardStepForm {

        private password: PasswordGenerator;

        private changePasswordButton: Button;

        private createPasswordFormItem: FormItem;

        private updatePasswordFormItem: FormItem;

        private principal: Principal;


        constructor() {
            super();

            this.password = new PasswordGenerator();

            this.changePasswordButton = new Button("Change Password");
            this.changePasswordButton.addClass("change-password-button");

            this.createPasswordFormItem = new FormItemBuilder(this.password).
                setLabel('Password').
                setValidator(Validators.required).
                build();

            this.updatePasswordFormItem = new FormItemBuilder(this.changePasswordButton).
                setLabel('Password').
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(this.createPasswordFormItem);
            fieldSet.add(this.updatePasswordFormItem);

            var passwordForm = new api.ui.form.Form().add(fieldSet);

            this.changePasswordButton.onClicked(() => {
                new OpenChangePasswordDialogEvent(this.principal).fire();
            });
            this.updatePasswordFormItem.setVisible(false);
            this.appendChild(passwordForm);
        }

        layout(principal: Principal) {
           this.updatePrincipal(principal);
        }

        updatePrincipal(principal: Principal) {
            this.principal = principal;
            if(principal) {
                this.createPasswordFormItem.setVisible(false);
                this.updatePasswordFormItem.setVisible(true);
            } else {
                this.createPasswordFormItem.setVisible(true);
                this.updatePasswordFormItem.setVisible(false);
            }
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
