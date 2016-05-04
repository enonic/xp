import "../../../api.ts";

import GetPrincipalsByUserStoreRequest = api.security.GetPrincipalsByUserStoreRequest;
import PrincipalType = api.security.PrincipalType;
import UserStore = api.security.UserStore;
import {UserItemWizardActions} from "./UserItemWizardActions";
import {UserItemWizardPanel} from "../UserItemWizardPanel";

export class UserStoreWizardActions extends UserItemWizardActions<api.security.UserStore> {

    constructor(wizardPanel: UserItemWizardPanel<api.security.UserStore>) {
        super(wizardPanel);
        this.establishDeleteActionState(wizardPanel.getPersistedItemKey());
    }

    private establishDeleteActionState(key: api.security.UserStoreKey) {
        if (key) {
            UserStore.checkOnDeletable(key).then((result: boolean) => {
                this.getDeleteAction().setEnabled(result);
            });
        }
    }
}
