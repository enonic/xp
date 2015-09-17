module app.wizard {

    export class UserItemWizardPanelParams {

        tabId: api.app.bar.AppBarTabId;

        userStoreKey: api.security.UserStoreKey;

        persistedPath: string;

        getPersistedItem() {
            throw new Error("Must be implemented by inheritors");
        }


    }
}