import '../../api.ts';
import {UserItemWizardActions} from './action/UserItemWizardActions';
import {UserItemWizardPanelParams} from './UserItemWizardPanelParams';
import {SaveBeforeCloseDialog} from './SaveBeforeCloseDialog';

import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalType = api.security.PrincipalType;
import PrincipalNamedEvent = api.security.PrincipalNamedEvent;
import UserStoreKey = api.security.UserStoreKey;

import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import FormIcon = api.app.wizard.FormIcon;
import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
import WizardStep = api.app.wizard.WizardStep;
import Toolbar = api.ui.toolbar.Toolbar;
import WizardActions = api.app.wizard.WizardActions;
import UserItem = api.security.UserItem;
import i18n = api.util.i18n;

export class UserItemWizardPanel<USER_ITEM_TYPE extends UserItem> extends api.app.wizard.WizardPanel<USER_ITEM_TYPE> {

    protected wizardActions: UserItemWizardActions<USER_ITEM_TYPE>;

    protected params: UserItemWizardPanelParams<USER_ITEM_TYPE>;

    constructor(params: UserItemWizardPanelParams<USER_ITEM_TYPE>) {

        super(params);

        this.loadData();
    }

    protected getParams(): UserItemWizardPanelParams<USER_ITEM_TYPE> {
        return this.params;
    }

    protected createWizardActions(): UserItemWizardActions<USER_ITEM_TYPE> {
        return new UserItemWizardActions(this);
    }

    protected createMainToolbar(): Toolbar {
        const toolbar: Toolbar = new Toolbar();

        toolbar.addAction(this.wizardActions.getSaveAction());
        toolbar.addAction(this.wizardActions.getDeleteAction());

        return toolbar;
    }

    protected createWizardHeader(): WizardHeaderWithDisplayNameAndName {
        let wizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().build();

        let existing = this.getPersistedItem();
        let displayName = '';
        let name = '';

        if (existing) {
            displayName = existing.getDisplayName();
            name = existing.getKey().getId();

            wizardHeader.disableNameInput();
            wizardHeader.setAutoGenerationEnabled(false);
        } else {

            wizardHeader.onPropertyChanged((event: api.PropertyChangedEvent) => {
                let updateStatus = event.getPropertyName() === 'name' ||
                                   (wizardHeader.isAutoGenerationEnabled()
                                    && event.getPropertyName() === 'displayName');

                if (updateStatus) {
                    this.wizardActions.getSaveAction().setEnabled(!!event.getNewValue());
                }
            });

        }

        wizardHeader.setPath(this.getParams().persistedPath);
        wizardHeader.initNames(displayName, name, false);

        return wizardHeader;
    }

    public getWizardHeader(): WizardHeaderWithDisplayNameAndName {
        return <WizardHeaderWithDisplayNameAndName> super.getWizardHeader();
    }

    protected createFormIcon(): FormIcon {
        let iconUrl = api.dom.ImgEl.PLACEHOLDER;
        let formIcon = new FormIcon(iconUrl, 'icon');
        formIcon.addClass('icon icon-xlarge');
        return formIcon;
    }

    public getFormIcon(): FormIcon {
        return <FormIcon> super.getFormIcon();
    }

    doRenderOnDataLoaded(rendered: boolean): Q.Promise<boolean> {

        return super.doRenderOnDataLoaded(rendered).then((nextRendered) => {
            this.addClass('principal-wizard-panel');

            const responsiveItem = ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (this.isVisible()) {
                    this.updateStickyToolbar();
                }
            });

            this.updateHash();
            this.onRemoved((event) => ResponsiveManager.unAvailableSizeChanged(this));

            this.onShown((event: api.dom.ElementShownEvent) => {
                this.updateHash();
                responsiveItem.update();
            });

            const deleteHandler = ((event: api.security.event.PrincipalDeletedEvent) => {
                event.getDeletedItems().forEach((path: string) => {
                    if (!!this.getPersistedItem() && this.getPersistedItemPath() === path) {
                        this.close();
                    }
                });
            });

            api.security.event.PrincipalDeletedEvent.on(deleteHandler);

            this.onRemoved(() => {
                api.security.event.PrincipalDeletedEvent.un(deleteHandler);
            });

            return nextRendered;
        });
    }

    protected getPersistedItemPath(): string {
        throw new Error('To be implemented by inheritors');
    }

    protected setPersistedItem(newPersistedItem: USER_ITEM_TYPE): void {
        super.setPersistedItem(newPersistedItem);

        if (this.wizardHeader) {
            (<WizardHeaderWithDisplayNameAndName>this.wizardHeader).disableNameInput();
        }
    }

    getUserItemType(): string {
        throw new Error('Must be implemented by inheritors');
    }

    getPersistedDisplayName(): string {
        return this.getParams().persistedDisplayName;
    }

    saveChanges(): wemQ.Promise<USER_ITEM_TYPE> {
        if (this.isRendered() && !this.getWizardHeader().getName()) {
            let deferred = wemQ.defer<USER_ITEM_TYPE>();
            api.notify.showError(i18n('notify.empty.name'));
            deferred.reject(new Error('Name can not be empty'));
            return deferred.promise;
        } else {
            return super.saveChanges();
        }

    }

    close(checkCanClose: boolean = false) {
        if (!checkCanClose || this.canClose()) {
            super.close(checkCanClose);
        }
    }

    canClose(): boolean {
        if (this.hasUnsavedChanges()) {
            new SaveBeforeCloseDialog(this).open();
            return false;
        } else {
            return true;
        }
    }

    createSteps(persistedItem: USER_ITEM_TYPE): WizardStep[] {
        throw new Error('Must be implemented by inheritors');
    }

    doLayout(persistedItem: USER_ITEM_TYPE): wemQ.Promise<void> {

        this.setSteps(this.createSteps(this.getPersistedItem()));

        return wemQ<void>(null);
    }

    protected doLayoutPersistedItem(persistedItem: USER_ITEM_TYPE): Q.Promise<void> {
        throw new Error('Must be implemented by inheritors');
    }

    persistNewItem(): wemQ.Promise<USER_ITEM_TYPE> {
        throw new Error('Must be implemented by inheritors');
    }

    updatePersistedItem(): wemQ.Promise<USER_ITEM_TYPE> {
        throw new Error('Must be implemented by inheritors');
    }

    getCloseAction(): api.ui.Action {
        return this.wizardActions.getCloseAction();
    }

    protected updateHash() {
        throw new Error('Must be implemented by inheritors');
    }
}
