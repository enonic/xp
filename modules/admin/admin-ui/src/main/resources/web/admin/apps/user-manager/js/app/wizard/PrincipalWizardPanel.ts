import "../../api.ts";
import {UserItemWizardPanel} from "./UserItemWizardPanel";
import {PrincipalWizardPanelParams} from "./PrincipalWizardPanelParams";
import {UserItemWizardActions} from "./action/UserItemWizardActions";
import {Router} from "../Router";
import {PrincipalWizardToolbar} from "./PrincipalWizardToolbar";
import {PrincipalWizardDataLoader} from "./PrincipalWizardDataLoader";

import Principal = api.security.Principal;
import PrincipalType = api.security.PrincipalType;
import PrincipalNamedEvent = api.security.PrincipalNamedEvent;
import UserStore = api.security.UserStore;
import UserStoreKey = api.security.UserStoreKey;
import PrincipalKey = api.security.PrincipalKey;

import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
import WizardStep = api.app.wizard.WizardStep;
import SecurityResourceRequest = api.security.SecurityResourceRequest;
import StringHelper = api.util.StringHelper;
import PrincipalJson = api.security.PrincipalJson;

export class PrincipalWizardPanel extends UserItemWizardPanel<Principal> {

    protected params: PrincipalWizardPanelParams;

    protected principalNamedListeners: {(event: PrincipalNamedEvent): void}[];

    public static debug: boolean = false;

    constructor(params: PrincipalWizardPanelParams) {
        super(params);

        this.principalNamedListeners = [];
    }

    protected getParams(): PrincipalWizardPanelParams {
        return this.params;
    }

    protected doLoadData(): Q.Promise<Principal> {
        if (PrincipalWizardPanel.debug) {
            console.debug("PrincipalWizardPanel.doLoadData");
        }
        if (!this.getPersistedItem()) {
            if (PrincipalWizardPanel.debug) {
                console.debug("PrincipalWizardPanel.doLoadData: loading data...");
            }
            // don't call super.doLoadData to prevent saving new entity
            return new PrincipalWizardDataLoader().loadData(this.getParams())
                .then((loader) => {
                    if (PrincipalWizardPanel.debug) {
                        console.debug("PrincipalWizardPanel.doLoadData: loaded data", loader);
                    }
                    if (loader.principal) {
                        this.formState.setIsNew(false);
                        this.setPersistedItem(loader.principal);
                    }
                    return loader.principal;
                });
        } else {
            var equitable = this.getPersistedItem();
            if (PrincipalWizardPanel.debug) {
                console.debug("PrincipalWizardPanel.doLoadData: data present, skipping load...", equitable);
            }
            return wemQ(equitable);
        }
    }

    protected createWizardActions(): UserItemWizardActions<Principal> {
        return new UserItemWizardActions(this);
    }

    protected createMainToolbar(): PrincipalWizardToolbar {
        return new PrincipalWizardToolbar({
            saveAction: this.wizardActions.getSaveAction(),
            deleteAction: this.wizardActions.getDeleteAction()
        });
    }

    public getMainToolbar(): PrincipalWizardToolbar {
        return <PrincipalWizardToolbar>super.getMainToolbar();
    }

    protected createWizardHeader(): WizardHeaderWithDisplayNameAndName {
        var wizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().build();

        let existing = this.getPersistedItem(),
            displayName = "",
            name = "";
        if (!!existing) {
            displayName = existing.getDisplayName();
            name = existing.getKey().getId();

            wizardHeader.disableNameInput();
            wizardHeader.setAutoGenerationEnabled(false);
        } else {

            wizardHeader.onPropertyChanged((event: api.PropertyChangedEvent) => {
                var updateStatus = event.getPropertyName() === "name" ||
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

    doRenderOnDataLoaded(rendered): Q.Promise<boolean> {
        return super.doRenderOnDataLoaded(rendered).then((rendered) => {
            if (PrincipalWizardPanel.debug) {
                console.debug("PrincipalWizardPanel.doRenderOnDataLoaded");
            }
            this.addClass("principal-wizard-panel");

            switch (this.getParams().persistedType) {
            case PrincipalType.USER:
                this.formIcon.addClass("icon-user");
                break;
            case PrincipalType.GROUP:
                this.formIcon.addClass("icon-users");
                break;
            case PrincipalType.ROLE:
                this.formIcon.addClass("icon-masks");
                break;
            }

            var deleteHandler = ((event: api.security.event.PrincipalDeletedEvent) => {
                event.getDeletedItems().forEach((path: string) => {
                    if (!!this.getPersistedItem() && this.getPersistedItem().getKey().toPath() == path) {
                        this.close();
                    }
                });
            });

            api.security.event.PrincipalDeletedEvent.on(deleteHandler);

            this.onRemoved((event) => {
                api.security.event.PrincipalDeletedEvent.un(deleteHandler);
            });

            return rendered;
        });
    }

    getUserItemType(): string {
        switch (this.getParams().persistedType) {
        case PrincipalType.USER:
            return "User";
        case PrincipalType.GROUP:
            return "Group";
        case PrincipalType.ROLE:
            return "Role";
        default:
            return "";
        }
    }

    isParentOfSameType(): boolean {
        return this.getParams().parentOfSameType;
    }

    getUserStore(): UserStore {
        return this.getParams().userStore;
    }

    createSteps(principal?: Principal): WizardStep[] {
        throw new Error("Must be implemented by inheritors");
    }

    doLayout(persistedPrincipal: Principal): wemQ.Promise<void> {

        return super.doLayout(persistedPrincipal).then(() => {

            var viewedPrincipal;
            if (this.isRendered()) {

                viewedPrincipal = this.assembleViewedItem();
                if (!viewedPrincipal.equals(persistedPrincipal)) {

                    console.warn("Received Principal from server differs from what's viewed:");
                    console.warn(" viewedPrincipal: ", viewedPrincipal);
                    console.warn(" persistedPrincipal: ", persistedPrincipal);

                    const msg = "Received Principal from server differs from what you have. Would you like to load changes from server?";
                    ConfirmationDialog.get()
                        .setQuestion(msg)
                        .setYesCallback(() => this.doLayoutPersistedItem(persistedPrincipal ? persistedPrincipal.clone() : null))
                        .setNoCallback(() => { /* empty */ })
                        .show();
                }

                return wemQ<void>(null);
            }
            else {
                return this.doLayoutPersistedItem(persistedPrincipal ? persistedPrincipal.clone() : null);
            }

        });
    }

    protected doLayoutPersistedItem(principal: Principal): Q.Promise<void> {
        if (principal) {
            this.getWizardHeader().setDisplayName(principal.getDisplayName());
        }

        return wemQ<void>(null);
    }

    postPersistNewItem(persistedPrincipal: Principal): wemQ.Promise<Principal> {
        Router.setHash("edit/" + persistedPrincipal.getKey());


        return wemQ(persistedPrincipal);
    }

    updatePersistedItem(): wemQ.Promise<Principal> {
        return this.produceUpdateRequest(this.assembleViewedItem()).sendAndParse().then((principal:Principal) => {
            if (!this.getPersistedItem().getDisplayName() && !!principal.getDisplayName()) {
                this.notifyPrincipalNamed(principal);
            }

            var principalTypeName = StringHelper.capitalize(PrincipalType[principal.getType()].toLowerCase());
            api.notify.showFeedback(principalTypeName + " '" + principal.getDisplayName() + "' was updated!");
            new api.security.UserItemUpdatedEvent(principal, this.getUserStore()).fire();

            return principal;
        });
    }

    hasUnsavedChanges(): boolean {
        var persistedPrincipal: Principal = this.getPersistedItem();
        if (persistedPrincipal == undefined) {
            return true;
        } else {
            var viewedPrincipal = this.assembleViewedItem();
            return !viewedPrincipal.equals(this.getPersistedItem());
        }
    }

    protected produceUpdateRequest(viewedPrincipal: Principal): SecurityResourceRequest<PrincipalJson, Principal> {
        throw new Error("Must be implemented by inheritors");
    }

    protected assembleViewedItem():Principal {
        throw new Error("Must be implemented by inheritors");
    }

    onPrincipalNamed(listener: (event: PrincipalNamedEvent)=>void) {
        this.principalNamedListeners.push(listener);
    }

    notifyPrincipalNamed(principal: Principal) {
        this.principalNamedListeners.forEach((listener: (event: PrincipalNamedEvent)=>void)=> {
            listener.call(this, new PrincipalNamedEvent(this, principal));
        });
    }

    protected updateHash() {
        if (this.getPersistedItem()) {
            Router.setHash("edit/" + this.getPersistedItem().getKey());
        } else {
            Router.setHash("new/" + PrincipalType[this.getParams().persistedType].toLowerCase());
        }
    }
}
