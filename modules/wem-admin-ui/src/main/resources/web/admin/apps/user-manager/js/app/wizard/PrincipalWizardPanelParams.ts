module app.wizard {

    export class PrincipalWizardPanelParams {

        tabId: api.app.bar.AppBarTabId;

        persistedPrincipal: api.security.Principal;

        setAppBarTabId(value: api.app.bar.AppBarTabId): PrincipalWizardPanelParams {
            this.tabId = value;
            return this;
        }

        setPersistedPrincipal(value: api.security.Principal): PrincipalWizardPanelParams {
            this.persistedPrincipal = value;
            return this;
        }
    }
}