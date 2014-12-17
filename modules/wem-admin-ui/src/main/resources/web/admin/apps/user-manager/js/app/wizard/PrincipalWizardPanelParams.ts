module app.wizard {

    export class PrincipalWizardPanelParams extends UserItemWizardPanelParams {

        persistedPrincipal: api.security.Principal;

        persistedType: api.security.PrincipalType;

        persistedPath: string;


        setPersistedPrincipal(value: api.security.Principal): PrincipalWizardPanelParams {
            this.persistedPrincipal = value;
            return this;
        }

        setPrincipalType(value: api.security.PrincipalType): PrincipalWizardPanelParams {
            this.persistedType = value;
            return this;
        }

        setPrincipalPath(value: string): PrincipalWizardPanelParams {
            this.persistedPath = value;
            return this;
        }

        setAppBarTabId(value: api.app.bar.AppBarTabId): PrincipalWizardPanelParams {
            this.tabId = value;
            return this;
        }

        setUserStoreKey(value: api.security.UserStoreKey): PrincipalWizardPanelParams {
            this.userStoreKey = value;
            return this;
        }

        setPersistedPath(value: string): PrincipalWizardPanelParams {
            this.persistedPath = value;
            return this;
        }

        getPersistedItem() {
            return this.persistedPrincipal;
        }
    }
}