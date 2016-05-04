import "../../api.ts";

import UserStore = api.security.UserStore;
import UserStoreKey = api.security.UserStoreKey;
import {UserStoreWizardPanel} from "./UserstoreWizardPanel";
import {UserStoreWizardPanelParams} from "./UserStoreWizardPanelParams";

export class UserStoreWizardPanelFactory {

    private creatingForNew: boolean;

    private userStoreKey: UserStoreKey;

    private appBarTabId: api.app.bar.AppBarTabId;

    private userStore: UserStore;

    private userStoreToEdit: UserStore;

    private defaultUserStore: UserStore;

    setUserStore(value: UserStore): UserStoreWizardPanelFactory {
        this.userStore = value;
        return this;
    }

    setUserStoreKey(value: UserStoreKey): UserStoreWizardPanelFactory {
        this.userStoreKey = value;
        return this;
    }

    setAppBarTabId(value: api.app.bar.AppBarTabId): UserStoreWizardPanelFactory {
        this.appBarTabId = value;
        return this;
    }

    createForNew(): wemQ.Promise<UserStoreWizardPanel> {

        this.creatingForNew = true;

        return this.loadDefaultUserStore().then((defaultUserStore: UserStore) => {
            this.defaultUserStore = defaultUserStore;
            return this.newUserStoreWizardPanelForNew();
        });
    }

    createForEdit(): wemQ.Promise<UserStoreWizardPanel> {

        this.creatingForNew = false;

        return this.loadUserStoreToEdit().then((loadedUserStoreToEdit: UserStore) => {
            this.userStoreToEdit = loadedUserStoreToEdit;
            return this.loadDefaultUserStore().then((defaultUserStore: UserStore) => {
                this.defaultUserStore = defaultUserStore;
                return this.newUserStoreWizardPanelForEdit();
            });
        });
    }

    private loadUserStoreToEdit(): wemQ.Promise<UserStore> {
        return new api.security.GetUserStoreByKeyRequest(this.userStoreKey).sendAndParse();
    }

    private loadDefaultUserStore(): wemQ.Promise<UserStore> {
        return new api.security.GetDefaultUserStoreRequest().sendAndParse();
    }

    private newUserStoreWizardPanelForNew(): wemQ.Promise<UserStoreWizardPanel> {

        var deferred = wemQ.defer<UserStoreWizardPanel>();

        var wizardParams = new UserStoreWizardPanelParams().setUserStoreKey(this.userStoreKey).setDefaultUserStore(
            this.defaultUserStore).setAppBarTabId(this.appBarTabId);

        this.resolveUserStoreWizardPanel(deferred, wizardParams);

        return deferred.promise;
    }

    private newUserStoreWizardPanelForEdit(): wemQ.Promise<UserStoreWizardPanel> {

        var deferred = wemQ.defer<UserStoreWizardPanel>();

        var wizardParams = new UserStoreWizardPanelParams().setUserStoreKey(this.userStoreKey).setUserStore(
            this.userStoreToEdit).setDefaultUserStore(this.defaultUserStore).setAppBarTabId(this.appBarTabId);

        this.resolveUserStoreWizardPanel(deferred, wizardParams);

        return deferred.promise;
    }

    private resolveUserStoreWizardPanel(deferred: wemQ.Deferred<UserStoreWizardPanel>, wizardParams: UserStoreWizardPanelParams) {
        new UserStoreWizardPanel(wizardParams, (wizard: UserStoreWizardPanel) => {
            deferred.resolve(wizard);
        });

    }
}
