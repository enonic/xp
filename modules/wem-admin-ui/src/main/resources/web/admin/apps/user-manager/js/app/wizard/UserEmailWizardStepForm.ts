module app.wizard {

    import Principal = api.security.Principal;

    import EmailInput = api.ui.text.EmailInput;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class UserEmailWizardStepForm extends api.app.wizard.WizardStepForm {

        private email: EmailInput;

        private label: LabelEl;

        private userStoreKey: api.security.UserStoreKey;

        constructor(userStoreKey: api.security.UserStoreKey) {
            super();

            this.userStoreKey = userStoreKey;
            this.email = new EmailInput();
            this.email.setUserStoreKey(this.userStoreKey);

            var label = new DivEl("input-label"),
                wrapper = new DivEl("wrapper required");
            this.label = new LabelEl("Email");
            wrapper.appendChild(this.label);
            label.appendChild(wrapper);

            var formView = new DivEl("form-view"),
                inputView = new DivEl("input-view valid"),
                inputTypeView = new DivEl("input-type-view"),
                inputOccurrenceView = new DivEl("input-occurrence-view single-occurrence"),
                inputWrapper = new DivEl("input-wrapper");

            inputWrapper.appendChild(this.email);
            inputOccurrenceView.appendChild(inputWrapper);
            inputTypeView.appendChild(inputOccurrenceView);
            inputView.appendChild(label);
            inputView.appendChild(inputTypeView);
            formView.appendChild(inputView);

            this.appendChild(formView);
        }

        layout(principal: Principal) {
            this.email.setValue(principal.asUser().getEmail());
            this.email.setName(principal.asUser().getEmail());
            this.email.setOriginEmail(principal.asUser().getEmail());
        }

        isValid(): boolean {
            return this.email.isAvailable();
        }

        getEmail(): string {
            return this.email.getValue();
        }

        giveFocus(): boolean {
            return this.email.giveFocus();
        }
    }
}
