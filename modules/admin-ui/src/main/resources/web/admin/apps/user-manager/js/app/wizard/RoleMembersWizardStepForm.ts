module app.wizard {

    import Role = api.security.Role;
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;

    export class RoleMembersWizardStepForm extends PrincipalMembersWizardStepForm {

        constructor() {
            super();
            this.getLabel().setValue("Has Role");
        }

        getPrincipalMembers(): PrincipalKey[] {
            return this.getPrincipal().asRole().getMembers();
        }
    }
}
