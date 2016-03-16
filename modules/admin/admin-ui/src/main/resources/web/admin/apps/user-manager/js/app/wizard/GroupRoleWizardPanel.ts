module app.wizard {

    import Principal = api.security.Principal;

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import WizardStep = api.app.wizard.WizardStep;

    export class GroupRoleWizardPanel extends PrincipalWizardPanel {

        private descriptionWizardStepForm: PrincipalDescriptionWizardStepForm;
        private membersWizardStepForm: PrincipalMembersWizardStepForm;

        constructor(membersWizardStepForm: PrincipalMembersWizardStepForm,
                    params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            this.descriptionWizardStepForm = new PrincipalDescriptionWizardStepForm();
            this.membersWizardStepForm = membersWizardStepForm;

            super(params, () => {
                this.addClass("group-role-wizard-panel");
                callback(this);
            });
        }

        getDescriptionWizardStepForm(): PrincipalDescriptionWizardStepForm {
            return this.descriptionWizardStepForm;
        }

        getMembersWizardStepForm(): PrincipalMembersWizardStepForm {
            return this.membersWizardStepForm;
        }

        giveInitialFocus() {
            this.wizardHeader.giveFocus();
            this.startRememberFocus();
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

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedPrincipal: Principal): wemQ.Promise<void> {
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();
                var viewedPrincipal = this.assembleViewedItem();

                if (!this.isPersistedEqualsViewed()) {

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
            } else {
                return this.doLayoutPersistedItem(persistedPrincipal.clone());
            }
        }

        hasUnsavedChanges(): boolean {
            var persistedPrincipal = this.getPersistedItem();
            if (persistedPrincipal == undefined) {
                return this.wizardHeader.getName() !== "" ||
                    this.wizardHeader.getDisplayName() !== "" ||
                    this.membersWizardStepForm.getMembers().length !== 0;
            } else {
                return !this.isPersistedEqualsViewed();
            }
        }

        isPersistedEqualsViewed(): boolean {
            throw new Error("Must be implemented by inheritors");
        }

        show() {
            setTimeout(() => {
                super.show();
            }, 0);

        }
    }
}