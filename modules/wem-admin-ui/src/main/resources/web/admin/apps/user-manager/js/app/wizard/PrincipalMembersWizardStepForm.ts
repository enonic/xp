module app.wizard {

    import Role = api.security.Role;
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalLoader = api.security.PrincipalLoader;

    import PrincipalComboBox = api.ui.security.PrincipalComboBox;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;

    export class PrincipalMembersWizardStepForm extends api.app.wizard.WizardStepForm {

        private principals: PrincipalComboBox;

        private label: LabelEl;

        private principal: Principal;

        constructor(loadedHandler?: Function) {
            super();

            loadedHandler = loadedHandler || (() => {});
            var loader = new PrincipalLoader().setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER]).
                skipPrincipals([PrincipalKey.ofAnonymous()]);

            this.principals = new PrincipalComboBox(loader);
            var handler = () => { this.selectMembers(); loadedHandler(); this.principals.unLoaded(handler); };
            this.principals.onLoaded(handler);

            this.label = new LabelEl("", this.principals, "input-label");

            var formView = new DivEl("form-view"),
                inputView = new DivEl("input-view valid"),
                inputTypeView = new DivEl("input-type-view"),
                inputOccurrenceView = new DivEl("input-occurrence-view single-occurrence"),
                inputWrapper = new DivEl("input-wrapper");

            inputWrapper.appendChild(this.principals);
            inputOccurrenceView.appendChild(inputWrapper);
            inputTypeView.appendChild(inputOccurrenceView);
            inputView.appendChild(this.label);
            inputView.appendChild(inputTypeView);
            formView.appendChild(inputView);

            this.appendChild(formView);
        }

        layout(principal: Principal) {
            this.principal = principal;
            this.selectMembers();
        }

        private selectMembers(): void {
            if (!!this.principal) {
                var principalKeys = this.getPrincipalMembers().map((key: PrincipalKey) => {
                    return key.getId();
                });
                var selected = this.principals.getDisplayValues().filter((principal: Principal) => {
                    return principalKeys.indexOf(principal.getKey().getId()) >= 0;
                });
                selected.forEach((selection) => {
                    this.principals.select(selection);
                });
            }
        }

        getMembers(): Principal[] {
            return this.principals.getSelectedDisplayValues();
        }

        getPrincipals(): PrincipalComboBox {
            return this.principals;
        }

        getLabel(): LabelEl {
            return this.label;
        }

        getPrincipal(): Principal {
            return this.principal;
        }

        getPrincipalMembers(): PrincipalKey[] {
            throw new Error("Must be implemented by inheritors");
        }

        giveFocus(): boolean {
            return this.principals.giveFocus();
        }
    }
}
