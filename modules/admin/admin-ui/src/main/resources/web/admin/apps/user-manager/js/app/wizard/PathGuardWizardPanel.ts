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
        private pathGuardWizardStepForm: PathGuardWizardStepForm;
        private pathGuardMappingWizardStepForm: PathGuardMappingWizardStepForm;

        private persistedPathGuardKey: api.security.PathGuardKey;

        isPathGuardFormValid: boolean;
        pathGuardPath: string;

        pathGuardNamedListeners: {(event: PathGuardNamedEvent): void}[];

        constructor(params: PathGuardWizardPanelParams, callback: (wizard: PathGuardWizardPanel) => void) {

            this.pathGuardWizardStepForm = new PathGuardWizardStepForm();
            this.pathGuardMappingWizardStepForm = new PathGuardMappingWizardStepForm();

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
                        app.Router.setHash("edit/guard:" + this.getPersistedItem().getKey());
                    } else {
                        app.Router.setHash("new/guard");
                    }

                    responsiveItem.update();
                });

                this.constructing = false;

                callback(this);
            });

            this.formIcon.addClass("icon-shield");
        }

        getUserItemType(): string {
            return "Guard";
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

            steps.push(new WizardStep("Guard", this.pathGuardWizardStepForm));
            steps.push(new WizardStep("Protected Resources", this.pathGuardMappingWizardStepForm));

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

            this.wizardHeader.initNames("", "", false);
            this.pathGuardWizardStepForm.layout(null);
            this.pathGuardMappingWizardStepForm.layout(null);

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

            this.wizardHeader.initNames(existing.getDisplayName(), existing.getKey().toString(), false);
            this.pathGuardWizardStepForm.layout(existing.clone());
            this.pathGuardMappingWizardStepForm.layout(existing.clone());

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
                       this.pathGuardWizardStepForm.getDescription() != null ||
                       this.pathGuardWizardStepForm.getUserStoreKey() != null ||
                       this.pathGuardWizardStepForm.isPassive() ||
                       this.pathGuardMappingWizardStepForm.getPaths().length > 0;
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

        getPersistedItemKey(): api.security.PathGuardKey {
            return this.persistedPathGuardKey;
        }

        private assembleViewedPathGuard(): PathGuard {
            return new PathGuardBuilder().
                setKey(this.getPersistedItem().getKey()).
                setDisplayName(this.wizardHeader.getDisplayName()).
                setDescription(this.pathGuardWizardStepForm.getDescription()).
                setUserStoreKey(this.pathGuardWizardStepForm.getUserStoreKey()).
                setPassive(this.pathGuardWizardStepForm.isPassive()).
                setPaths(this.pathGuardMappingWizardStepForm.getPaths()).
                build();
        }

        private produceCreatePathGuardRequest(): CreatePathGuardRequest {
            var key = api.security.PathGuardKey.fromString(this.wizardHeader.getName()),
                name = this.wizardHeader.getDisplayName(),
                description = this.pathGuardWizardStepForm.getDescription(),
                userStoreKey = this.pathGuardWizardStepForm.getUserStoreKey(),
                passive = this.pathGuardWizardStepForm.isPassive(),
                paths = this.pathGuardMappingWizardStepForm.getPaths();
            return new CreatePathGuardRequest().
                setKey(key).
                setDisplayName(name).
                setDescription(description).
                setUserStoreKey(userStoreKey).
                setPassive(passive).
                setPaths(paths);
        }

        private produceUpdatePathGuardRequest(viewedPathGuard: PathGuard): UpdatePathGuardRequest {
            var key = this.getPersistedItem().getKey(),
                name = viewedPathGuard.getDisplayName(),
                description = viewedPathGuard.getDescription(),
                userStoreKey = viewedPathGuard.getUserStoreKey(),
                passive = viewedPathGuard.isPassive(),
                paths = viewedPathGuard.getPaths();

            return new UpdatePathGuardRequest().
                setKey(key).
                setDisplayName(name).
                setDescription(description).
                setUserStoreKey(userStoreKey).
                setPassive(passive).
                setPaths(paths);
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
