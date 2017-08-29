import '../../api.ts';
import {PrincipalMembersWizardStepForm} from './PrincipalMembersWizardStepForm';

import Role = api.security.Role;
import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalType = api.security.PrincipalType;

export class GroupMembersWizardStepForm extends PrincipalMembersWizardStepForm {

    constructor() {
        super();
        this.getLoader().load();
    }

    getPrincipalMembers(): PrincipalKey[] {
        return this.getPrincipal().asGroup().getMembers();
    }
}
