module app.wizard {

    import PathGuard = api.security.PathGuard;
    import PathGuardNamedEvent = api.security.PathGuardNamedEvent;
    import CreatePathGuardRequest = api.security.CreatePathGuardRequest;
    import UpdatePathGuardRequest = api.security.UpdatePathGuardRequest;
    import PathGuardBuilder = api.security.PathGuardBuilder;

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import WizardStep = api.app.wizard.WizardStep;
    import FormIcon = api.app.wizard.FormIcon;
    import WizardHeaderWithDisplayNameAndName = api.app.wizard.WizardHeaderWithDisplayNameAndName;
    import WizardHeaderWithDisplayNameAndNameBuilder = api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder;

    export class PathGuardWizardPanel extends UserItemWizardPanel<PathGuard> {

        private descriptionWizardStepForm: PrincipalDescriptionWizardStepForm;

        private authApplicationWizardStepForm: AuthApplicationWizardStepForm;

        private persistedPathGuardKey: string;

        isPathGuardFormValid: boolean;
        pathGuardPath: string;

        pathGuardNamedListeners: {(event: PathGuardNamedEvent): void}[];

        constructor(params: PathGuardWizardPanelParams, callback: (wizard: PathGuardWizardPanel) => void) {

            this.descriptionWizardStepForm = new PrincipalDescriptionWizardStepForm();
            this.authApplicationWizardStepForm = new AuthApplicationWizardStepForm();

            this.constructing = true;
            this.isPathGuardFormValid = false;
            this.pathGuardNamedListeners = [];

            this.pathGuardPath = params.persistedPath;
            if (params.pathGuard) {
                this.persistedPathGuardKey = params.pathGuard.getKey();
            }

            var iconUrl = api.dom.ImgEl.PLACEHOLDER;
            this.formIcon = new FormIcon(iconUrl, "Click to upload icon");
            this.formIcon.addClass("icon icon-xlarge");
            this.formIcon.addClass("icon-shield");

            this.wizardActions = new app.wizard.action.PathGuardWizardActions(this);
            this.toolbar = new PathGuardWizardToolbar({
                saveAction: this.wizardActions.getSaveAction(),
                deleteAction: this.wizardActions.getDeleteAction()
            });

            this.wizardHeader = new WizardHeaderWithDisplayNameAndNameBuilder().build();

            this.wizardHeader.setPath(this.pathGuardPath);

            if (params.pathGuard) {
                this.wizardHeader.disableNameInput();
                this.wizardHeader.setAutoGenerationEnabled(false);
            } else {
                this.getPathGuardWizardHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                    var updateStatus = event.getPropertyName() === "name" ||
                                       (this.getPathGuardWizardHeader().isAutoGenerationEnabled() &&
                                        event.getPropertyName() === "displayName");

                    if (updateStatus) {
                        this.wizardActions.getSaveAction().setEnabled(!!event.getNewValue());
                    }
                });
            }

            super(params, () => {

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
                        app.Router.setHash("new/");
                    }

                    responsiveItem.update();
                });

                this.constructing = false;

                callback(this);
            });

            this.formIcon.addClass("icon-address-book");
        }

        getUserItemType(): string {
            return "User Store";
        }

        getPathGuardWizardHeader(): WizardHeaderWithDisplayNameAndName {
            return this.wizardHeader;
        }

        giveInitialFocus() {
            this.wizardHeader.giveFocus();
            this.startRememberFocus();
        }

        saveChanges(): wemQ.Promise<PathGuard> {
            if (!this.wizardHeader.getName()) {
                var deferred = wemQ.defer<PathGuard>();
                api.notify.showError("Name can not be empty");
                // deferred.resolve(null);
                deferred.reject(new Error("Name can not be empty"));
                return deferred.promise;
            } else {
                return super.saveChanges();
            }

        }

        createSteps(): wemQ.Promise<any[]> {
            var deferred = wemQ.defer<WizardStep[]>();

            var steps: WizardStep[] = [];

            steps.push(new WizardStep("PathGuard", this.descriptionWizardStepForm));
            steps.push(new WizardStep("Authentication", this.authApplicationWizardStepForm));

            this.setSteps(steps);

            deferred.resolve(steps);
            return deferred.promise;
        }

        preLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.doLayoutPersistedItem(null);

            deferred.resolve(null);

            return deferred.promise;
        }

        postLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.wizardHeader.initNames("", this.pathGuardPath, false);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedPathGuard: PathGuard): wemQ.Promise<void> {

            var viewedPathGuard;
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
            else {
                return this.doLayoutPersistedItem(persistedPathGuard.clone());
            }
        }

        doLayoutPersistedItem(pathGuard: PathGuard): wemQ.Promise<void> {

            var parallelPromises: wemQ.Promise<any>[] = [
                // Load attachments?
                this.createSteps()
            ];

            return wemQ.all(parallelPromises).
                spread<void>(() => {
            });
        }

        postLayoutPersisted(existing: PathGuard): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.wizardHeader.initNames(existing.getDisplayName(), existing.getKey(), false);
            this.authApplicationWizardStepForm.layout(existing.clone());

            deferred.resolve(null);
            return deferred.promise;
        }

        persistNewItem(): wemQ.Promise<PathGuard> {
            return this.produceCreatePathGuardRequest().sendAndParse().
                then((pathGuard: PathGuard) => {
                    this.wizardHeader.disableNameInput();
                    this.wizardHeader.setAutoGenerationEnabled(false);
                    api.notify.showFeedback('PathGuard was created!');
                    new api.security.UserItemCreatedEvent(null, null, pathGuard).fire();

                    return pathGuard;
                });
        }

        updatePersistedItem(): wemQ.Promise<PathGuard> {
            return this.produceUpdatePathGuardRequest(this.assembleViewedPathGuard()).
                sendAndParse().
                then((pathGuard: PathGuard) => {
                    if (!this.getPersistedItem().getDisplayName() && !!pathGuard.getDisplayName()) {
                        this.notifyPathGuardNamed(pathGuard);
                    }
                    api.notify.showFeedback('PathGuard was updated!');
                    new api.security.UserItemUpdatedEvent(null, null, pathGuard).fire();

                    return pathGuard;
                });
        }


        hasUnsavedChanges(): boolean {
            var persistedPathGuard: PathGuard = this.getPersistedItem();
            if (persistedPathGuard == undefined) {
                return this.wizardHeader.getName() !== "" ||
                       this.wizardHeader.getDisplayName() !== "" ||
                       this.authApplicationWizardStepForm.getAuthConfig() != null;
            } else {
                var viewedPathGuard = this.assembleViewedPathGuard();
                return !this.getPersistedItem().equals(viewedPathGuard);
            }
        }

        resolvePathGuardNameForUpdateRequest(): string {
            if (api.util.StringHelper.isEmpty(this.wizardHeader.getName())) {
                return this.getPersistedItem().getDisplayName();
            } else {
                return this.wizardHeader.getName();
            }
        }

        getPersistedItemKey(): string {
            return this.persistedPathGuardKey;
        }

        private assembleViewedPathGuard(): PathGuard {
            return new PathGuardBuilder().
                setDisplayName(this.wizardHeader.getDisplayName()).
                setKey(this.getPersistedItem().getKey().toString()).
                setAuthConfig(this.authApplicationWizardStepForm.getAuthConfig()).
                setPaths([]). //TODO
                build();
        }

        private produceCreatePathGuardRequest(): CreatePathGuardRequest {
            var key = this.wizardHeader.getName(),
                name = this.wizardHeader.getDisplayName(),
                authConfig = this.authApplicationWizardStepForm.getAuthConfig();
            return new CreatePathGuardRequest().
                setDisplayName(name).
                setKey(key).
                setAuthConfig(authConfig);
        }

        private produceUpdatePathGuardRequest(viewedPathGuard: PathGuard): UpdatePathGuardRequest {
            var key = this.getPersistedItem().getKey(),
                name = viewedPathGuard.getDisplayName(),
                authConfig = viewedPathGuard.getAuthConfig();

            return new UpdatePathGuardRequest().
                setKey(key).
                setDisplayName(name).
                setAuthConfig(authConfig);
        }


        onPathGuardNamed(listener: (event: PathGuardNamedEvent)=>void) {
            this.pathGuardNamedListeners.push(listener);
        }

        notifyPathGuardNamed(pathGuard: PathGuard) {
            this.pathGuardNamedListeners.forEach((listener: (event: PathGuardNamedEvent)=>void)=> {
                listener.call(this, new PathGuardNamedEvent(this, pathGuard));
            });
        }
    }
}
