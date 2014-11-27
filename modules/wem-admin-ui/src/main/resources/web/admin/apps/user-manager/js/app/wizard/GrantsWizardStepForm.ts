module app.wizard {

    import Role = api.security.Role;
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;

    export class GrantsWizardStepForm extends api.app.wizard.WizardStepForm {

        private principals: api.security.PrincipalComboBox;

        private role: Role;

        constructor() {
            super();

            var loader = new api.security.PrincipalLoader();
            loader.setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER]);
            this.principals = new api.security.PrincipalComboBox(loader);
            var handler = () => { this.selectMembers(); this.principals.unLoaded(handler); };
            this.principals.onLoaded(handler);

            var formView = new api.dom.DivEl("form-view"),
                inputView = new api.dom.DivEl("input-view valid"),
                label = new api.dom.LabelEl("Has Role", this.principals, "input-label"),
                inputTypeView = new api.dom.DivEl("input-type-view"),
                inputOccurrenceView = new api.dom.DivEl("input-occurrence-view single-occurrence"),
                inputWrapper = new api.dom.DivEl("input-wrapper");

            inputWrapper.appendChild(this.principals);
            inputOccurrenceView.appendChild(inputWrapper);
            inputTypeView.appendChild(inputOccurrenceView);
            inputView.appendChild(label);
            inputView.appendChild(inputTypeView);
            formView.appendChild(inputView);

            this.appendChild(formView);
        }

        layout(role: Role) {
            this.role = role;
            this.selectMembers();
        }

        private selectMembers(): void {
            if (!!this.role) {
                var principalKeys = this.role.getMembers().map((key:PrincipalKey) => {
                    return key.getId();
                });
                var selected = this.principals.getValues().filter((principal:Principal) => {
                    return principalKeys.indexOf(principal.getKey().getId()) >= 0;
                });
                selected.forEach((selection) => {
                    this.principals.select(selection);
                });
            }
        }

        getMembers(): Principal[] {
            return this.principals.getSelectedValues();
        }

        giveFocus(): boolean {
            return this.principals.giveFocus();
        }
    }
}
