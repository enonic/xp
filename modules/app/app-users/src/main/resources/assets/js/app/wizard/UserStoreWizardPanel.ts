import '../../api.ts';
import {UserItemWizardPanel} from './UserItemWizardPanel';
import {SecurityWizardStepForm} from './SecurityWizardStepForm';
import {UserStoreWizardPanelParams} from './UserStoreWizardPanelParams';
import {UserStoreWizardStepForm} from './UserStoreWizardStepForm';
import {Router} from '../Router';
import {UserStoreWizardDataLoader} from './UserStoreWizardDataLoader';

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
import i18n = api.util.i18n;

export class UserStoreWizardPanel extends UserItemWizardPanel<UserStore> {

    private userStoreWizardStepForm: UserStoreWizardStepForm;

    private permissionsWizardStepForm: SecurityWizardStepForm;

    private defaultUserStore: UserStore;

    public static debug: boolean = false;

    constructor(params: UserStoreWizardPanelParams) {

        super(params);

        this.listenToUserItemEvents();
    }

    protected doLoadData(): Q.Promise<UserStore> {
        if (UserStoreWizardPanel.debug) {
            console.debug('UserStoreWizardPanel.doLoadData');
        }
        // don't call super.doLoadData to prevent saving new entity
        return new UserStoreWizardDataLoader().loadData(this.getParams())
            .then((loader) => {
                if (UserStoreWizardPanel.debug) {
                    console.debug('UserStoreWizardPanel.doLoadData: loaded data', loader);
                }
                if (loader.userStore) {
                    this.formState.setIsNew(false);
                    this.setPersistedItem(loader.userStore);
                    this.establishDeleteActionState(loader.userStore.getKey());
                }
                this.defaultUserStore = loader.defaultUserStore;
                return loader.userStore;
            });
    }

    protected createFormIcon(): api.app.wizard.FormIcon {
        let iconUrl = api.dom.ImgEl.PLACEHOLDER;
        let formIcon = new FormIcon(iconUrl, 'icon');
        formIcon.addClass('icon-xlarge icon-address-book');
        return formIcon;
    }

    doRenderOnDataLoaded(rendered: boolean): Q.Promise<boolean> {
        return super.doRenderOnDataLoaded(rendered).then((nextRendered) => {
            if (UserStoreWizardPanel.debug) {
                console.debug('UserStoreWizardPanel.doRenderOnDataLoaded');
            }
            this.addClass('principal-wizard-panel');

            this.getFormIcon().addClass('icon-address-book');

            return nextRendered;
        });
    }

    protected getPersistedItemPath(): string {
        return `/${this.getPersistedItem().getKey().toString()}`;
    }

    getUserItemType(): string {
        return i18n('field.userStore');
    }

    createSteps(persistedItem: UserStore): WizardStep[] {
        let steps: WizardStep[] = [];

        this.userStoreWizardStepForm = new UserStoreWizardStepForm();
        this.permissionsWizardStepForm = new SecurityWizardStepForm();

        steps.push(new WizardStep(i18n('field.userStore'), this.userStoreWizardStepForm));
        steps.push(new WizardStep(i18n('field.permissions'), this.permissionsWizardStepForm));

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
            this.userStoreWizardStepForm.layout(this.defaultUserStore);
            this.permissionsWizardStepForm.layoutReadOnly(this.defaultUserStore);
        }

        return wemQ<void>(null);
    }

    persistNewItem(): wemQ.Promise<UserStore> {
        return this.produceCreateUserStoreRequest().sendAndParse().then((userStore: UserStore) => {

            api.notify.showFeedback('User store was created');
            new api.security.UserItemCreatedEvent(null, userStore).fire();

            return userStore;
        });
    }

    postPersistNewItem(userStore: UserStore): wemQ.Promise<UserStore> {
        Router.setHash('edit/' + userStore.getKey());

        return wemQ(userStore);
    }

    updatePersistedItem(): wemQ.Promise<UserStore> {
        return this.produceUpdateUserStoreRequest(this.assembleViewedUserStore()).sendAndParse().then((userStore: UserStore) => {
            api.notify.showFeedback('User store was updated');
            new api.security.UserItemUpdatedEvent(null, userStore).fire();

            return userStore;
        });
    }

    hasUnsavedChanges(): boolean {
        let persistedUserStore: UserStore = this.getPersistedItem();
        if (persistedUserStore == null) {
            let wizardHeader = this.getWizardHeader();
            return wizardHeader.getName() !== '' ||
                   wizardHeader.getDisplayName() !== '' ||
                   !this.permissionsWizardStepForm.getPermissions().equals(this.defaultUserStore.getPermissions());
        } else {
            let viewedUserStore = this.assembleViewedUserStore();
            return !this.getPersistedItem().equals(viewedUserStore);
        }
    }

    private assembleViewedUserStore(): UserStore {
        return <UserStore>new UserStoreBuilder().setAuthConfig(
            this.userStoreWizardStepForm.getAuthConfig()).setPermissions(this.permissionsWizardStepForm.getPermissions()).setKey(
            this.getPersistedItem().getKey().toString()).setDisplayName(this.getWizardHeader().getDisplayName()).setDescription(
            this.userStoreWizardStepForm.getDescription()).build();
    }

    private produceCreateUserStoreRequest(): CreateUserStoreRequest {
        let header = this.getWizardHeader();
        let key = new UserStoreKey(header.getName());
        let name = header.getDisplayName();
        let description = this.userStoreWizardStepForm.getDescription();
        let authConfig = this.userStoreWizardStepForm.getAuthConfig();
        let permissions = this.permissionsWizardStepForm.getPermissions();

        return new CreateUserStoreRequest()
            .setDisplayName(name)
            .setKey(key)
            .setDescription(description)
            .setAuthConfig(authConfig)
            .setPermissions(permissions);
    }

    private produceUpdateUserStoreRequest(viewedUserStore: UserStore): UpdateUserStoreRequest {
        let key = this.getPersistedItem().getKey();
        let name = viewedUserStore.getDisplayName();
        let description = viewedUserStore.getDescription();
        let authConfig = viewedUserStore.getAuthConfig();
        let permissions = viewedUserStore.getPermissions();

        return new UpdateUserStoreRequest()
            .setKey(key)
            .setDisplayName(name)
            .setDescription(description)
            .setAuthConfig(authConfig)
            .setPermissions(permissions);
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
            Router.setHash('edit/' + this.getPersistedItem().getKey());
        } else {
            Router.setHash('new/');
        }
    }

    private establishDeleteActionState(key: api.security.UserStoreKey) {
        if (key) {
            UserStore.checkOnDeletable(key).then((result: boolean) => {
                this.wizardActions.getDeleteAction().setEnabled(result);
            });
        }
    }

}
