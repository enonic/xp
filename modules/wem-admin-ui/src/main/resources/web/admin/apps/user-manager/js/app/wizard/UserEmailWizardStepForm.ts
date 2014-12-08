module app.wizard {

    import Principal = api.security.Principal;

    import EmailInput = api.ui.text.EmailInput;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class UserEmailWizardStepForm extends api.app.wizard.WizardStepForm {

        private email: EmailInput;

        private label: LabelEl;

        constructor() {
            super();

            this.email = new EmailInput();

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
