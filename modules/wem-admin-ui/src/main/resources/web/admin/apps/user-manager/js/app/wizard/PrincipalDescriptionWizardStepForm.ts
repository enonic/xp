module app.wizard {

    export class PrincipalDescriptionWizardStepForm extends api.app.wizard.WizardStepForm {

        private description: api.ui.text.TextInput;

        constructor() {
            super();

            this.description = new api.ui.text.TextInput("middle");
            var formView = new api.dom.DivEl("form-view"),
                inputView = new api.dom.DivEl("input-view valid"),
                label = new api.dom.LabelEl("Description", this.description, "input-label"),
                inputTypeView = new api.dom.DivEl("input-type-view"),
                inputOccurrenceView = new api.dom.DivEl("input-occurrence-view single-occurrence"),
                inputWrapper = new api.dom.DivEl("input-wrapper");

            inputWrapper.appendChild(this.description);
            inputOccurrenceView.appendChild(inputWrapper);
            inputTypeView.appendChild(inputOccurrenceView);
            inputView.appendChild(label);
            inputView.appendChild(inputTypeView);
            formView.appendChild(inputView);

            this.appendChild(formView);
        }

        layout(principal: api.security.Principal) {
//            this.description.setValue(role.getDescription());
            this.description.setValue("");
        }

        giveFocus(): boolean {
            return this.description.giveFocus();
        }
    }
}
