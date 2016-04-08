module app.wizard {

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalNamedEvent = api.security.PrincipalNamedEvent;
    import UserStore = api.security.UserStore;
    import UserStoreKey = api.security.UserStoreKey;
    import PrincipalKey = api.security.PrincipalKey;

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import FormIcon = api.app.wizard.FormIcon;
    import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
    import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;
    import WizardStep = api.app.wizard.WizardStep;


    export class PrincipalWizardPanel extends UserItemWizardPanel<Principal> {

        protected principalType: PrincipalType;

        protected principalPath: string;

        protected principalNamedListeners: {(event: PrincipalNamedEvent): void}[];

        private parentOfSameType: boolean;

        private userStore: UserStore;

        private persistedPrincipalKey: PrincipalKey;

        constructor(params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            this.principalNamedListeners = [];

            this.principalType = params.persistedType;
            this.principalPath = params.persistedPath;
            if (params.persistedPrincipal) {
                this.persistedPrincipalKey = params.persistedPrincipal.getKey();
            }

            this.parentOfSameType = params.parentOfSameType;

            this.userStore = params.userStore;

            this.wizardActions = new app.wizard.action.UserItemWizardActions(this);
            this.toolbar = new PrincipalWizardToolbar({
                saveAction: this.wizardActions.getSaveAction(),
                deleteAction: this.wizardActions.getDeleteAction()
            });

            this.wizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().build();
            this.wizardHeader.setPath(this.principalPath);

            if (params.persistedPrincipal) {
                this.wizardHeader.disableNameInput();
                this.wizardHeader.setAutoGenerationEnabled(false);
            } else {
                this.getPrincipalWizardHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                    var updateStatus = event.getPropertyName() === "name" ||
                        (this.getPrincipalWizardHeader().isAutoGenerationEnabled() && event.getPropertyName() === "displayName");

                    if (updateStatus) {
                        this.wizardActions.getSaveAction().setEnabled(!!event.getNewValue());
                    }
                });
            }

            super(params, () => {

                this.addClass("principal-wizard-panel");

                switch (this.principalType) {
                case PrincipalType.USER:
                    this.formIcon.addClass("icon-user");
                    break;
                case PrincipalType.GROUP:
                    this.formIcon.addClass("icon-users");
                    break;
                case PrincipalType.ROLE:
                    this.formIcon.addClass("icon-shield");
                    break;
                }

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

        getUserItemType(): string {
            switch (this.principalType) {
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
            return this.parentOfSameType;
        }

        getUserStore(): UserStore {
            return this.userStore;
        }

        getUserStoreKey(): UserStoreKey {
            return !!this.userStore ? this.userStore.getKey() : null;
        }

        getPrincipalWizardHeader(): WizardHeaderWithDisplayNameAndName {
            return this.wizardHeader;
        }

        giveInitialFocus() {
            this.wizardHeader.giveFocus();
            this.startRememberFocus();
        }

        saveChanges(): wemQ.Promise<Principal> {
            if (!this.wizardHeader.getName()) {
                var deferred = wemQ.defer<Principal>();
                api.notify.showError("Name can not be empty");
                // deferred.resolve(null);
                deferred.reject(new Error("Name can not be empty"));
                return deferred.promise;
            } else {
                return super.saveChanges();
            }

        }

        createSteps(principal?: Principal): wemQ.Promise<any[]> {
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

            this.wizardHeader.initNames("", this.principalPath, false);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedPrincipal: Principal): wemQ.Promise<void> {

            var viewedPrincipal;
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();

                viewedPrincipal = this.assembleViewedItem();
                if (!viewedPrincipal.equals(persistedPrincipal)) {

                    console.warn("Received Principal from server differs from what's viewed:");
                    console.warn(" viewedPrincipal: ", viewedPrincipal);
                    console.warn(" persistedPrincipal: ", persistedPrincipal);

                    ConfirmationDialog.get().
                        setQuestion("Received Principal from server differs from what you have. Would you like to load changes from server?").
                        setYesCallback(() => this.doLayoutPersistedItem(persistedPrincipal.clone())).
                        setNoCallback(() => {/* Do nothing */
                        }).
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

            this.wizardHeader.initNames(existing.getDisplayName(), existing.getKey().getId(), false);

            deferred.resolve(null);
            return deferred.promise;
        }

        persistNewItem(): wemQ.Promise<Principal> {
            throw new Error("Must be implemented by inheritors");
        }

        postPersistNewItem(persistedPrincipal: Principal): wemQ.Promise<void> {
            app.Router.setHash("edit/" + persistedPrincipal.getKey());

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
                var viewedPrincipal = this.assembleViewedItem();
                return !viewedPrincipal.equals(this.getPersistedItem());
            }
        }

        getPersistedItemKey(): PrincipalKey {
            return this.persistedPrincipalKey;
        }

        assembleViewedItem(): Principal {
            var key = this.getPersistedItem().getKey(),
                displayName = this.wizardHeader.getDisplayName(),
                modifiedTime = this.getPersistedItem().getModifiedTime();

            var principal = Principal.create().setKey(key).setDisplayName(displayName).setModifiedTime(modifiedTime).build();
            return principal;
        }

        resolvePrincipalNameForUpdateRequest(): string {
            if (api.util.StringHelper.isEmpty(this.wizardHeader.getName())) {
                return this.getPersistedItem().getDisplayName();
            } else {
                return this.wizardHeader.getName();
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
