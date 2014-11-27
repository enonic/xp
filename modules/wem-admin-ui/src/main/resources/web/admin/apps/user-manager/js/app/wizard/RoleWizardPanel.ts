module app.wizard {

    import Role = api.security.Role;
    import RoleBuilder = api.security.RoleBuilder;
    import CreateRoleRequest = api.security.CreateRoleRequest;
    import UpdateRoleRequest = api.security.UpdateRoleRequest;

    import Principal = api.security.Principal;

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import WizardStep = api.app.wizard.WizardStep;

    export class RoleWizardPanel extends PrincipalWizardPanel {

        private roleWizardStepForm: RoleWizardStepForm;
        private grantsWizardStepForm: GrantsWizardStepForm;

        constructor(params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            this.roleWizardStepForm = new RoleWizardStepForm();
            this.grantsWizardStepForm = new GrantsWizardStepForm();

            super(params, () => {
                this.addClass("role-wizard-panel");
                callback(this);
            });
        }

        createPrincipalPath(principal?: Principal): string {
            var path = principal.getKey().toString().split(":");
            path.pop();
            return path.length === 0 ? "/" : "/" + path.reverse().join("/") + "/";
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

        createSteps(): wemQ.Promise<any[]> {
            var deferred = wemQ.defer<WizardStep[]>();

            var steps: WizardStep[] = [];

            steps.push(new WizardStep("Role", this.roleWizardStepForm));
            steps.push(new WizardStep("Grants", this.grantsWizardStepForm));

            this.setSteps(steps);

            deferred.resolve(steps);
            return deferred.promise;
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

            this.principalWizardHeader.initNames(this.getPersistedItem().getDisplayName(), this.getPersistedItem().getKey().getId(), false);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedPrincipal: Principal): wemQ.Promise<void> {

            var viewedPrincipal;
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();

                viewedPrincipal = this.assembleViewedPrincipal();
                if (!viewedPrincipal.asRole().equals(persistedPrincipal.asRole())) {

                    console.warn("Received Principal from server differs from what's viewed:");
                    console.warn(" viewedPrincipal: ", viewedPrincipal);
                    console.warn(" persistedPrincipal: ", persistedPrincipal);

                    ConfirmationDialog.get().
                        setQuestion("Received Role from server differs from what you have. Would you like to load changes from server?").
                        setYesCallback(() => this.doLayoutPersistedItem(persistedPrincipal.asRole().clone())).
                        setNoCallback(() => {/* Do nothing */}).
                        show();
                }

                deferred.resolve(null);
                return deferred.promise;
            }
            else {
                return this.doLayoutPersistedItem(persistedPrincipal.asRole().clone());
            }
        }

        doLayoutPersistedItem(principal: Principal): wemQ.Promise<void> {

            var parallelPromises: wemQ.Promise<any>[] = [
                // Load attachments?
                this.createSteps()
            ];

            return wemQ.all(parallelPromises).
                spread<void>(() => {
                    this.roleWizardStepForm.layout(principal.asRole());
                    this.grantsWizardStepForm.layout(principal.asRole());
                return wemQ(null);
            });
        }

        persistNewItem(): wemQ.Promise<Principal> {
             return this.produceCreateRoleRequest().sendAndParse().
                then((principal: Principal) => {
                    api.notify.showFeedback('Role was created!');
                    return principal;
                });
        }

        produceCreateRoleRequest(): CreateRoleRequest {
            var role = this.assembleViewedPrincipal().asRole();
            return new CreateRoleRequest().setKey(role.getKey()).setDisplayName(role.getDisplayName()).setMembers(role.getMembers());
        }

        updatePersistedItem(): wemQ.Promise<Principal> {
             return this.produceUpdateRoleRequest(this.assembleViewedPrincipal()).
                 sendAndParse().
                 then((principal: Principal) => {
                     if (!this.getPersistedItem().getDisplayName() && !!principal.getDisplayName()) {
                        this.notifyPrincipalNamed(principal);
                     }
                     api.notify.showFeedback('Role was updated!');

                     return principal;
                });
        }

        produceUpdateRoleRequest(viewedPrincipal: Principal): UpdateRoleRequest {
            var role = viewedPrincipal.asRole(),
                key = role.getKey(),
                displayName = role.getDisplayName(),
                oldMembers = this.getPersistedItem().asRole().getMembers(),
                oldMembersIds = oldMembers.map((el) => { return el.getId(); }),
                newMembers = role.getMembers(),
                newMembersIds = newMembers.map((el) => { return el.getId(); });

            var addMembers = newMembers.filter((el) => { return oldMembersIds.indexOf(el.getId()) < 0; }),
                removeMembers = oldMembers.filter((el) => { return newMembersIds.indexOf(el.getId()) < 0; });

            return new UpdateRoleRequest().
                setKey(key).
                setDisplayName(displayName).
                addMembers(addMembers).
                removeMembers(removeMembers);
        }

        hasUnsavedChanges(): boolean {
            var persistedPrincipal = this.getPersistedItem();
            if (persistedPrincipal == undefined) {
                return true;
            } else {
                var viewedPrincipal = this.assembleViewedPrincipal();
                return !viewedPrincipal.asRole().equals(this.getPersistedItem().asRole());
            }
        }

        assembleViewedPrincipal(): Principal {
            return new RoleBuilder(this.getPersistedItem().asRole()).
                setDisplayName(this.principalWizardHeader.getDisplayName()).
                setMembers(this.grantsWizardStepForm.getMembers().map((el) => { return el.getKey(); })).
                build();
        }
    }
}
