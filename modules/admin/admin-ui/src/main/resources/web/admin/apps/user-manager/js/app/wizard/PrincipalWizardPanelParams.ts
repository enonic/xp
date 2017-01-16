import '../../api.ts';
import {UserItemWizardPanelParams} from './UserItemWizardPanelParams';
import Principal = api.security.Principal;
import PrincipalType = api.security.PrincipalType;
import UserStore = api.security.UserStore;
import PrincipalKey = api.security.PrincipalKey;

export class PrincipalWizardPanelParams extends UserItemWizardPanelParams<Principal> {

    persistedType: PrincipalType;

    userStore: UserStore;

    parentOfSameType: boolean;

    principalKey: PrincipalKey;

    setPrincipalKey(value: api.security.PrincipalKey): PrincipalWizardPanelParams {
        this.principalKey = value;
        return this;
    }

    setPersistedType(value: PrincipalType): PrincipalWizardPanelParams {
        this.persistedType = value;
        return this;
    }

    setUserStore(value: UserStore): PrincipalWizardPanelParams {
        this.userStore = value;
        return this;
    }

    setParentOfSameType(value: boolean): PrincipalWizardPanelParams {
        this.parentOfSameType = value;
        return this;
    }
}
