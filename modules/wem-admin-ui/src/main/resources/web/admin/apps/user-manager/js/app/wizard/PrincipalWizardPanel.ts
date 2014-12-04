module app.wizard {

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


    export class PrincipalWizardPanel extends api.app.wizard.WizardPanel<Principal> {

        formIcon: FormIcon;

        principalWizardHeader: WizardHeaderWithDisplayNameAndName;

        wizardActions: app.wizard.action.PrincipalWizardActions;

        isPrincipalFormValid: boolean;

        constructing: boolean;

        principalType: PrincipalType;

        principalPath: string;

        principalNamedListeners: {(event: PrincipalNamedEvent): void}[];

        constructor(params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            this.constructing = true;
            this.isPrincipalFormValid = false;
            this.principalNamedListeners = [];

            this.principalType = params.persistedType;
            this.principalPath = params.persistedPath;

            var iconUrl = api.dom.ImgEl.PLACEHOLDER;
            this.formIcon = new FormIcon(iconUrl, "Click to upload icon");
            this.formIcon.addClass("icon icon-xlarge");
            switch (this.principalType) {
            case PrincipalType.USER:
                this.formIcon.addClass("icon-user");
                break;
            case PrincipalType.GROUP:
                this.formIcon.addClass("icon-users");
                break;
            case PrincipalType.ROLE:
                this.formIcon.addClass("icon-user7");
                break;
            }

            this.wizardActions = new app.wizard.action.PrincipalWizardActions(this);
            var mainToolbar = new PrincipalWizardToolbar({
                saveAction: this.wizardActions.getSaveAction(),
                duplicateAction: this.wizardActions.getDuplicateAction(),
                deleteAction: this.wizardActions.getDeleteAction(),
                closeAction: this.wizardActions.getCloseAction()
            });

            this.principalWizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().build();

            this.principalWizardHeader.setPath(this.principalPath);

            if (params.persistedPrincipal) {
                this.principalWizardHeader.disableNameInput();
            }

            super({
                tabId: params.tabId,
                persistedItem: params.persistedPrincipal,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                header: this.principalWizardHeader,
                actions: this.wizardActions,
                livePanel: null,
                split: false
            }, () => {

                this.addClass("principal-wizard-panel");

                var responsiveItem = ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                    if (this.isVisible()) {
                        this.updateStickyToolbar();
                    }
                });

                this.onRemoved((event) => {
                    ResponsiveManager.unAvailableSizeChanged(this);
                });

                this.onShown((event: api.dom.ElementShownEvent) => {
                    if (this.getPersistedItem()) {
                        app.Router.setHash("edit/" + this.getPersistedItem().getKey());
                    } else {
                        app.Router.setHash("new/" + PrincipalType[this.principalType].toLowerCase());
                    }

                    responsiveItem.update();
                });

                this.constructing = false;

                callback(this);
            });
        }

        getPrincipalWizardHeader(): WizardHeaderWithDisplayNameAndName {
            return this.principalWizardHeader;
        }

        giveInitialFocus() {
            var newWithoutDisplayCameScript = this.isLayingOutNew();

            if (newWithoutDisplayCameScript) {
                this.principalWizardHeader.giveFocus();
            } else if (!this.principalWizardHeader.giveFocus()) {
                this.principalWizardHeader.giveFocus();
            }

            this.startRememberFocus();
        }

        saveChanges(): wemQ.Promise<Principal> {
            if (!this.principalWizardHeader.getName()) {
                var deferred = wemQ.defer<Principal>();
                api.notify.showError("Name can not be empty");
                // deferred.resolve(null);
                deferred.reject(new Error("Name can not be empty"));
                return deferred.promise;
            } else {
                return super.saveChanges();
            }

        }

        createSteps(): wemQ.Promise<any[]> {
            throw new Error("Must be implemented by inheritors");
        }

        preLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            // Ensure a nameless and empty content is persisted before rendering new
            this.saveChanges().
                then(() => {
                    deferred.resolve(null);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        postLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.principalWizardHeader.initNames("", this.principalPath, false);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedPrincipal: Principal): wemQ.Promise<void> {

            var viewedPrincipal;
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();

                viewedPrincipal = this.assembleViewedPrincipal();
                if (!viewedPrincipal.equals(persistedPrincipal)) {

                    console.warn("Received Principal from server differs from what's viewed:");
                    console.warn(" viewedPrincipal: ", viewedPrincipal);
                    console.warn(" persistedPrincipal: ", persistedPrincipal);

                    ConfirmationDialog.get().
                        setQuestion("Received Principal from server differs from what you have. Would you like to load changes from server?").
                        setYesCallback(() => this.doLayoutPersistedItem(persistedPrincipal.clone())).
                        setNoCallback(() => {/* Do nothing */}).
                        show();
                }

                deferred.resolve(null);
                return deferred.promise;
            }
            else {
                return this.doLayoutPersistedItem(persistedPrincipal.clone());
            }
        }

        doLayoutPersistedItem(principal: Principal): wemQ.Promise<void> {

            var parallelPromises: wemQ.Promise<any>[] = [
                // Load attachments?
                this.createSteps()
            ];

            return wemQ.all(parallelPromises).
                spread<void>(() => {
            });
        }

        postLayoutPersisted(existing: Principal): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.principalWizardHeader.initNames(existing.getDisplayName(), existing.getKey().getId(), false);

            deferred.resolve(null);
            return deferred.promise;
        }

        persistNewItem(): wemQ.Promise<Principal> {
            throw new Error("Must be implemented by inheritors");
        }

        postPersistNewItem(persistedPrincipal: Principal): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        updatePersistedItem(): wemQ.Promise<Principal> {
            throw new Error("Must be implemented by inheritors");
        }

        hasUnsavedChanges(): boolean {
            var persistedPrincipal: Principal = this.getPersistedItem();
            if (persistedPrincipal == undefined) {
                return true;
            } else {
                var viewedPrincipal = this.assembleViewedPrincipal();
                return !viewedPrincipal.equals(this.getPersistedItem());
            }
        }

        assembleViewedPrincipal(): Principal {
            var key = this.getPersistedItem().getKey(),
                displayName = this.principalWizardHeader.getDisplayName(),
                modifiedTime = this.getPersistedItem().getModifiedTime();

            return new Principal(key, displayName, modifiedTime);
        }

        resolvePrincipalNameForUpdateRequest(): string {
            if (api.util.StringHelper.isEmpty(this.principalWizardHeader.getName())) {
                return this.getPersistedItem().getDisplayName();
            } else {
                return this.principalWizardHeader.getName();
            }
        }

        getCloseAction(): api.ui.Action {
            return this.wizardActions.getCloseAction();
        }

        onPrincipalNamed(listener: (event: PrincipalNamedEvent)=>void) {
            this.principalNamedListeners.push(listener);
        }

        notifyPrincipalNamed(principal: Principal) {
            this.principalNamedListeners.forEach((listener: (event: PrincipalNamedEvent)=>void)=> {
                listener.call(this, new PrincipalNamedEvent(this, principal));
            });
        }
    }
}
