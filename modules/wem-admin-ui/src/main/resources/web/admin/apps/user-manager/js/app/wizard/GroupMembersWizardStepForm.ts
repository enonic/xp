module app.wizard {

    import Role = api.security.Role;
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;

    export class GroupMembersWizardStepForm extends PrincipalMembersWizardStepForm {

        constructor() {
            super(() => {
                /*
                 * TODO: May need more define logic for the displayed elements in the ComboBox
                 * We may not need the edited element or it's parents to be present in combobox.
                 */
                if (this.getPrincipal()) {
                    var displayedOptions = this.getPrincipals().getComboBox().getDisplayedOptions();
                    displayedOptions = displayedOptions.filter((option) => {
                        return option.displayValue.getKey().getId() !== this.getPrincipal().getKey().getId();
                    });
                    this.getPrincipals().getComboBox().setOptions(displayedOptions);
                }
            });
            this.getLabel().setValue("Members");
        }

        getPrincipalMembers(): PrincipalKey[] {
            return this.getPrincipal().asGroup().getMembers();
        }
    }
}
