import "../../api.ts";
import {UserItemWizardPanel} from "./UserItemWizardPanel";
import {SecurityWizardStepForm} from "./SecurityWizardStepForm";
import {UserStoreWizardPanelParams} from "./UserStoreWizardPanelParams";
import {UserStoreWizardActions} from "./action/UserStoreWizardActions";
import {UserStoreWizardToolbar} from "./UserStoreWizardToolbar";
import {UserStoreWizardStepForm} from "./UserStoreWizardStepForm";
import {Router} from "../Router";
import {UserStoreWizardDataLoader} from "./UserStoreWizardDataLoader";

import UserStore = api.security.UserStore;
import UserStoreKey = api.security.UserStoreKey;
import UserStoreNamedEvent = api.security.UserStoreNamedEvent;
import CreateUserStoreRequest = api.security.CreateUserStoreRequest;
import UpdateUserStoreRequest = api.security.UpdateUserStoreRequest;
import UserStoreBuilder = api.security.UserStoreBuilder;

import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import WizardStep = api.app.wizard.WizardStep;
import FormIcon = api.app.wizard.FormIcon;
import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;

export class UserStoreWizardPanel extends UserItemWizardPanel<UserStore> {

    private userStoreWizardStepForm: UserStoreWizardStepForm;

    private permissionsWizardStepForm: SecurityWizardStepForm;

    //private userStoreParams: UserStoreWizardPanelParams;

    isUserStoreFormValid: boolean;

    userStoreNamedListeners: {(event: UserStoreNamedEvent): void}[];

    private defaultUserStore: UserStore;

    public static debug: boolean = false;

    constructor(params: UserStoreWizardPanelParams) {

        super(params);

        this.isUserStoreFormValid = false;
        this.userStoreNamedListeners = [];

        //this.userStoreParams = params;

        this.listenToUserItemEvents();
    }

    protected doLoadData(): Q.Promise<UserStore> {
        if (UserStoreWizardPanel.debug) {
            console.debug("UserStoreWizardPanel.doLoadData");
        }
        // don't call super.doLoadData to prevent saving new entity
        return new UserStoreWizardDataLoader().loadData(this.getParams())
            .then((loader) => {
                if (UserStoreWizardPanel.debug) {
                    console.debug("UserStoreWizardPanel.doLoadData: loaded data", loader);
                }
                if (loader.userStore) {
                    this.formState.setIsNew(false);
                    this.setPersistedItem(loader.userStore);
                }
                this.defaultUserStore = loader.defaultUserStore;
                return loader.userStore;
            });
    }

    protected createWizardActions(): UserStoreWizardActions {
        return new UserStoreWizardActions(this);
    }

    protected createMainToolbar(): UserStoreWizardToolbar {
        return new UserStoreWizardToolbar({
            saveAction: this.wizardActions.getSaveAction(),
            deleteAction: this.wizardActions.getDeleteAction()
        });
    }

    public getMainToolbar(): api.ui.toolbar.Toolbar {
        return <UserStoreWizardToolbar>super.getMainToolbar();
    }

    protected createFormIcon(): api.app.wizard.FormIcon {
        let iconUrl = api.dom.ImgEl.PLACEHOLDER;
        let formIcon = new FormIcon(iconUrl, "icon");
        formIcon.addClass("icon-xlarge icon-address-book");
        return formIcon;
    }

    protected createWizardHeader(): api.app.wizard.WizardHeaderWithDisplayNameAndName {
        let wizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().build();

        let existing = this.getPersistedItem(),
            displayName = "",
            name = "";

        if (!!existing) {
            displayName = existing.getDisplayName();
            name = existing.getKey().getId();

            wizardHeader.disableNameInput();
            wizardHeader.setAutoGenerationEnabled(false);
        } else {
            displayName = "";
            name = "";

            wizardHeader.onPropertyChanged((event: api.PropertyChangedEvent) => {
                let updateStatus = event.getPropertyName() === "name" ||
                                   (wizardHeader.isAutoGenerationEnabled()
                                    && event.getPropertyName() === "displayName");

                if (updateStatus) {
                    this.wizardActions.getSaveAction().setEnabled(!!event.getNewValue());
                }
            });

        }

        wizardHeader.setPath(this.getParams().persistedPath);
        wizardHeader.initNames(displayName, name, false);

        return wizardHeader;
    }

    doRenderOnDataLoaded(rendered: boolean): Q.Promise<boolean> {
        return super.doRenderOnDataLoaded(rendered).then((nextRendered) => {
            if (UserStoreWizardPanel.debug) {
                console.debug("UserStoreWizardPanel.doRenderOnDataLoaded");
            }
            this.addClass("principal-wizard-panel");

            this.getFormIcon().addClass("icon-address-book");

            return nextRendered;
        });
    }

    getUserItemType(): string {
        return "User Store";
    }

    createSteps(persistedItem: UserStore): WizardStep[] {
        let steps: WizardStep[] = [];

        this.userStoreWizardStepForm = new UserStoreWizardStepForm();
        this.permissionsWizardStepForm = new SecurityWizardStepForm();

        steps.push(new WizardStep("User Store", this.userStoreWizardStepForm));
        steps.push(new WizardStep("Permissions", this.permissionsWizardStepForm));

        return steps;
    }

    doLayout(persistedUserStore: UserStore): wemQ.Promise<void> {
        return super.doLayout(persistedUserStore).then(() => {

            if (this.isRendered()) {
                return wemQ<void>(null);
            } else {
                return this.doLayoutPersistedItem(persistedUserStore ? persistedUserStore.clone() : null);
            }

        });
    }

    protected doLayoutPersistedItem(persistedItem: UserStore): Q.Promise<void> {

        if (!!persistedItem) {
            this.getWizardHeader().setDisplayName(persistedItem.getDisplayName());
            this.userStoreWizardStepForm.layout(persistedItem);
            this.permissionsWizardStepForm.layout(persistedItem, this.defaultUserStore);
        } else {
            this.userStoreWizardStepForm.layout();
            this.permissionsWizardStepForm.layoutReadOnly(this.defaultUserStore);
        }

        return wemQ<void>(null);
    }

    persistNewItem(): wemQ.Promise<UserStore> {
        return this.produceCreateUserStoreRequest().sendAndParse().then((userStore: UserStore) => {

            api.notify.showFeedback('UserStore was created!');
            new api.security.UserItemCreatedEvent(null, userStore).fire();

            return userStore;
        });
    }

    postPersistNewItem(userStore: UserStore): wemQ.Promise<UserStore> {
        Router.setHash("edit/" + userStore.getKey());

        return wemQ(userStore);
    }

    updatePersistedItem(): wemQ.Promise<UserStore> {
        return this.produceUpdateUserStoreRequest(this.assembleViewedUserStore()).sendAndParse().then((userStore: UserStore) => {
            if (!this.getPersistedItem().getDisplayName() && !!userStore.getDisplayName()) {
                this.notifyUserStoreNamed(userStore);
            }
            api.notify.showFeedback('UserStore was updated!');
            new api.security.UserItemUpdatedEvent(null, userStore).fire();

            return userStore;
        });
    }

    hasUnsavedChanges(): boolean {
        let persistedUserStore: UserStore = this.getPersistedItem();
        if (persistedUserStore == undefined) {
            let wizardHeader = this.getWizardHeader();
            return wizardHeader.getName() !== "" ||
                   wizardHeader.getDisplayName() !== "" ||
                   !this.permissionsWizardStepForm.getPermissions().equals(this.defaultUserStore.getPermissions());
        } else {
            let viewedUserStore = this.assembleViewedUserStore();
            return !this.getPersistedItem().equals(viewedUserStore);
        }
    }

    resolveUserStoreNameForUpdateRequest(): string {
        let wizardHeader = this.getWizardHeader();
        if (api.util.StringHelper.isEmpty(wizardHeader.getName())) {
            return this.getPersistedItem().getDisplayName();
        } else {
            return wizardHeader.getName();
        }
    }

    private assembleViewedUserStore(): UserStore {
        return new UserStoreBuilder().setDisplayName(this.getWizardHeader().getDisplayName()).setKey(
            this.getPersistedItem().getKey().toString()).setDescription(this.userStoreWizardStepForm.getDescription()).setAuthConfig(
            this.userStoreWizardStepForm.getAuthConfig()).setPermissions(this.permissionsWizardStepForm.getPermissions()).build();
    }

    private produceCreateUserStoreRequest(): CreateUserStoreRequest {
        let header = this.getWizardHeader(),
            key = new UserStoreKey(header.getName()),
            name = header.getDisplayName(),
            description = this.userStoreWizardStepForm.getDescription(),
            authConfig = this.userStoreWizardStepForm.getAuthConfig(),
            permissions = this.permissionsWizardStepForm.getPermissions();

        return new CreateUserStoreRequest()
            .setDisplayName(name)
            .setKey(key)
            .setDescription(description)
            .setAuthConfig(authConfig)
            .setPermissions(permissions);
    }

    private produceUpdateUserStoreRequest(viewedUserStore: UserStore): UpdateUserStoreRequest {
        let key = this.getPersistedItem().getKey(),
            name = viewedUserStore.getDisplayName(),
            description = viewedUserStore.getDescription(),
            authConfig = viewedUserStore.getAuthConfig(),
            permissions = viewedUserStore.getPermissions();

        return new UpdateUserStoreRequest()
            .setKey(key)
            .setDisplayName(name)
            .setDescription(description)
            .setAuthConfig(authConfig)
            .setPermissions(permissions);
    }

    onUserStoreNamed(listener: (event: UserStoreNamedEvent)=>void) {
        this.userStoreNamedListeners.push(listener);
    }

    notifyUserStoreNamed(userStore: UserStore) {
        this.userStoreNamedListeners.forEach((listener: (event: UserStoreNamedEvent)=>void)=> {
            listener.call(this, new UserStoreNamedEvent(this, userStore));
        });
    }

    private listenToUserItemEvents() {

        let principalCreatedHandler = (event: api.security.UserItemCreatedEvent) => {
            if (!this.getPersistedItem()) { // skip if user store is not persisted yet
                return;
            }

            let principal = event.getPrincipal();
            let isCreatedInCurrentUserStore = !!principal && (principal.isUser() || principal.isGroup())
                                              && event.getUserStore().getKey().equals(this.getPersistedItem().getKey());

            if (isCreatedInCurrentUserStore) {
                this.wizardActions.getDeleteAction().setEnabled(false);
            }
        };

        let principalDeletedHandler = (event: api.security.UserItemDeletedEvent) => {
            // skip if user store is not persisted yet or if anything except users or roles was deleted
            if (!this.getPersistedItem() || !event.getPrincipals()) {
                return;
            }

            this.getPersistedItem().isDeletable().then((result: boolean) => {
                this.wizardActions.getDeleteAction().setEnabled(result);
            });
        };

        api.security.UserItemCreatedEvent.on(principalCreatedHandler);
        api.security.UserItemDeletedEvent.on(principalDeletedHandler);

        this.onClosed(() => {
            api.security.UserItemCreatedEvent.un(principalCreatedHandler);
            api.security.UserItemDeletedEvent.un(principalDeletedHandler);
        });

    }

    protected updateHash() {
        if (this.getPersistedItem()) {
            Router.setHash("edit/" + this.getPersistedItem().getKey());
        } else {
            Router.setHash("new/");
        }
    }

}
