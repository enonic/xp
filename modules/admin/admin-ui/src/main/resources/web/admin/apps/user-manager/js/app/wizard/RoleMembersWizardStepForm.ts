import "../../api.ts";

import Role = api.security.Role;
import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalType = api.security.PrincipalType;
import {PrincipalMembersWizardStepForm} from "./PrincipalMembersWizardStepForm";

export class RoleMembersWizardStepForm extends PrincipalMembersWizardStepForm {

    constructor() {
        super();
        this.getLoader().load();
    }

    getPrincipalMembers(): PrincipalKey[] {
        return this.getPrincipal().asRole().getMembers();
    }
}
