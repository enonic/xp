import "../../../api.ts";
import {UserItemWizardActions} from "./UserItemWizardActions";
import {UserItemWizardPanel} from "../UserItemWizardPanel";

import GetPrincipalsByUserStoreRequest = api.security.GetPrincipalsByUserStoreRequest;
import PrincipalType = api.security.PrincipalType;
import UserStore = api.security.UserStore;

export class UserStoreWizardActions extends UserItemWizardActions<api.security.UserStore> {

    constructor(wizardPanel: UserItemWizardPanel<api.security.UserStore>) {
        super(wizardPanel);

        let userStore = wizardPanel.getPersistedItem();
        this.establishDeleteActionState(userStore ? userStore.getKey() : null);
    }

    establishDeleteActionState(key: api.security.UserStoreKey) {
        if (key) {
            UserStore.checkOnDeletable(key).then((result: boolean) => {
                this.getDeleteAction().setEnabled(result);
            });
        }
    }
}
