module app.wizard {

    import Role = api.security.Role;
    import RoleBuilder = api.security.RoleBuilder;
    import CreateRoleRequest = api.security.CreateRoleRequest;
    import UpdateRoleRequest = api.security.UpdateRoleRequest;

    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;

    import WizardStep = api.app.wizard.WizardStep;

    export class RoleWizardPanel extends GroupRoleWizardPanel {

        constructor(params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            super(new RoleMembersWizardStepForm(), params, () => {
                this.addClass("role-wizard-panel");
                callback(this);
            });
        }

        createSteps(): wemQ.Promise<any[]> {
            var deferred = wemQ.defer<WizardStep[]>();

            var steps: WizardStep[] = [];

            steps.push(new WizardStep("Role", this.getDescriptionWizardStepForm()));
            steps.push(new WizardStep("Grants", this.getMembersWizardStepForm()));

            this.setSteps(steps);

            deferred.resolve(steps);
            return deferred.promise;
        }

        doLayoutPersistedItem(principal: Principal): wemQ.Promise<void> {

            var parallelPromises: wemQ.Promise<any>[] = [
                // Load attachments?
                this.createSteps()
            ];

            return wemQ.all(parallelPromises).
                spread<void>(() => {
                    this.getDescriptionWizardStepForm().layout(principal);
                    this.getMembersWizardStepForm().layout(principal);
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
            var key = PrincipalKey.ofRole(Math.random().toString(36).slice(2)),
                name = this.principalWizardHeader.getDisplayName(),
                members = this.getMembersWizardStepForm().getMembers().map((el) => { return el.getKey(); });
            return new CreateRoleRequest().setKey(key).setDisplayName(name).setMembers(members);
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

        assembleViewedPrincipal(): Principal {
            return new RoleBuilder(this.getPersistedItem().asRole()).
                setDisplayName(this.principalWizardHeader.getDisplayName()).
                setMembers(this.getMembersWizardStepForm().getMembers().map((el) => { return el.getKey(); })).
                build();
        }

        isPersistedEqualsViewed(): boolean {
            var persistedPrincipal = this.getPersistedItem().asRole();
            var viewedPrincipal = this.assembleViewedPrincipal().asRole();
            // Group/User order can be different for viewed and persisted principal
            viewedPrincipal.getMembers().sort((a,b) => { return a.getId().localeCompare(b.getId()); });
            persistedPrincipal.getMembers().sort((a,b) => { return a.getId().localeCompare(b.getId()); });

            return viewedPrincipal.equals(persistedPrincipal);
        }
    }
}
