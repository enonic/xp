module app.wizard {

    export class UserStoreWizardPanelParams extends UserItemWizardPanelParams {

        userStore: api.security.UserStore;

        defaultUserStore: api.security.UserStore;

        setUserStore(value: api.security.UserStore): UserStoreWizardPanelParams {
            this.userStore = value;
            return this;
        }

        setDefaultUserStore(value: api.security.UserStore): UserStoreWizardPanelParams {
            this.defaultUserStore = value;
            return this;
        }

        setAppBarTabId(value: api.app.bar.AppBarTabId): UserStoreWizardPanelParams {
            this.tabId = value;
            return this;
        }

        setUserStoreKey(value: api.security.UserStoreKey): UserStoreWizardPanelParams {
            this.userStoreKey = value;
            return this;
        }

        setPersistedPath(value: string): UserStoreWizardPanelParams {
            this.persistedPath = value;
            return this;
        }

        getPersistedItem() {
            return this.userStore;
        }

    }
}