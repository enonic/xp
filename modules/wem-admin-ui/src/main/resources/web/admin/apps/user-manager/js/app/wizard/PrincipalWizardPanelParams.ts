module app.wizard {

    export class PrincipalWizardPanelParams {

        tabId: api.app.bar.AppBarTabId;

        persistedPrincipal: api.security.Principal;

        persistedType: api.security.PrincipalType;

        persistedPath: string;

        userStore: api.security.UserStoreKey;

        setAppBarTabId(value: api.app.bar.AppBarTabId): PrincipalWizardPanelParams {
            this.tabId = value;
            return this;
        }

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

        setUserStore(value: api.security.UserStoreKey): PrincipalWizardPanelParams {
            this.userStore = value;
            return this;
        }
    }
}