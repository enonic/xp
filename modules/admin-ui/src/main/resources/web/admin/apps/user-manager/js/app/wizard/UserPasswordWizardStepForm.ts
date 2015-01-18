module app.wizard {

    import User = api.security.User;
    import Principal = api.security.Principal;

    import PasswordGenerator = api.ui.text.PasswordGenerator;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class UserPasswordWizardStepForm extends api.app.wizard.WizardStepForm {

        private password: PasswordGenerator;

        private label: LabelEl;

        constructor() {
            super();

            this.password = new PasswordGenerator();

            var label = new DivEl("input-label"),
                wrapper = new DivEl("wrapper required");
            this.label = new LabelEl("Password");
            wrapper.appendChild(this.label);
            label.appendChild(wrapper);

            var formView = new DivEl("form-view"),
                inputView = new DivEl("input-view valid"),
                inputTypeView = new DivEl("input-type-view"),
                inputOccurrenceView = new DivEl("input-occurrence-view single-occurrence"),
                inputWrapper = new DivEl("input-wrapper");

            inputWrapper.appendChild(this.password);
            inputOccurrenceView.appendChild(inputWrapper);
            inputTypeView.appendChild(inputOccurrenceView);
            inputView.appendChild(label);
            inputView.appendChild(inputTypeView);
            formView.appendChild(inputView);

            this.appendChild(formView);
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
