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


    export class UserItemWizardPanel<USER_ITEM_TYPE extends api.Equitable> extends api.app.wizard.WizardPanel<USER_ITEM_TYPE> {

        formIcon: FormIcon;

        wizardHeader: WizardHeaderWithDisplayNameAndName;

        wizardActions: app.wizard.action.UserItemWizardActions<USER_ITEM_TYPE>;

        constructing: boolean;

        toolbar: api.ui.toolbar.Toolbar;


        constructor(params: UserItemWizardPanelParams, callback: (wizard: UserItemWizardPanel<USER_ITEM_TYPE>) => void) {

            this.constructing = true;

            var iconUrl = api.dom.ImgEl.PLACEHOLDER;
            this.formIcon = new FormIcon(iconUrl, "Click to upload icon");
            this.formIcon.addClass("icon icon-xlarge");

            super({
                tabId: params.tabId,
                persistedItem: params.getPersistedItem(),
                formIcon: this.formIcon,
                mainToolbar: this.toolbar,
                header: this.wizardHeader,
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

                this.constructing = false;

                callback(this);
            });
        }

        getUserItemType(): string {
            throw new Error("Must be implemented by inheritors");
        }

        giveInitialFocus() {
            this.wizardHeader.giveFocus();
            this.startRememberFocus();
        }


        saveChanges(): wemQ.Promise<USER_ITEM_TYPE> {
            if (!this.wizardHeader.getName()) {
                var deferred = wemQ.defer<USER_ITEM_TYPE>();
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


        layoutPersistedItem(persistedItem: USER_ITEM_TYPE): wemQ.Promise<void> {

            throw new Error("Must be implemented by inheritors");
        }

        doLayoutPersistedItem(item: USER_ITEM_TYPE): wemQ.Promise<void> {

            var parallelPromises: wemQ.Promise<any>[] = [
                // Load attachments?
                this.createSteps()
            ];

            return wemQ.all(parallelPromises).
                spread<void>(() => {
            });
        }


        persistNewItem(): wemQ.Promise<USER_ITEM_TYPE> {
            throw new Error("Must be implemented by inheritors");
        }

        postPersistNewItem(persisted: USER_ITEM_TYPE): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        updatePersistedItem(): wemQ.Promise<USER_ITEM_TYPE> {
            throw new Error("Must be implemented by inheritors");
        }


        getCloseAction(): api.ui.Action {
            return this.wizardActions.getCloseAction();
        }


    }
}
