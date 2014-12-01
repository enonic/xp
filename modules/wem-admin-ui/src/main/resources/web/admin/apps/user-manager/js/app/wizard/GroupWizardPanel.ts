module app.wizard {

    import Group = api.security.Group;
    import GroupBuilder = api.security.GroupBuilder;
    import CreateGroupRequest = api.security.CreateGroupRequest;
    import UpdateGroupRequest = api.security.UpdateGroupRequest;

    import Principal = api.security.Principal;

    import WizardStep = api.app.wizard.WizardStep;

    export class GroupWizardPanel extends GroupRoleWizardPanel {

        constructor(params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            super(new GroupMembersWizardStepForm(), params, () => {
                this.addClass("group-wizard-panel");
                callback(this);
            });
        }

        createSteps(): wemQ.Promise<any[]> {
            var deferred = wemQ.defer<WizardStep[]>();

            var steps: WizardStep[] = [];

            steps.push(new WizardStep("Group", this.getDescriptionWizardStepForm()));
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
             return this.produceCreateGroupRequest().sendAndParse().
                then((principal: Principal) => {
                    api.notify.showFeedback('Group was created!');
                    return principal;
                });
        }

        produceCreateGroupRequest(): CreateGroupRequest {
            var group = this.assembleViewedPrincipal().asGroup();
            return new CreateGroupRequest().setKey(group.getKey()).setDisplayName(group.getDisplayName()).setMembers(group.getMembers());
        }

        updatePersistedItem(): wemQ.Promise<Principal> {
             return this.produceUpdateGroupRequest(this.assembleViewedPrincipal()).
                 sendAndParse().
                 then((principal: Principal) => {
                     if (!this.getPersistedItem().getDisplayName() && !!principal.getDisplayName()) {
                        this.notifyPrincipalNamed(principal);
                     }
                     api.notify.showFeedback('Group was updated!');

                     return principal;
                });
        }

        produceUpdateGroupRequest(viewedPrincipal: Principal): UpdateGroupRequest {
            var group = viewedPrincipal.asGroup(),
                key = group.getKey(),
                displayName = group.getDisplayName(),
                oldMembers = this.getPersistedItem().asGroup().getMembers(),
                oldMembersIds = oldMembers.map((el) => { return el.getId(); }),
                newMembers = group.getMembers(),
                newMembersIds = newMembers.map((el) => { return el.getId(); });

            var addMembers = newMembers.filter((el) => { return oldMembersIds.indexOf(el.getId()) < 0; }),
                removeMembers = oldMembers.filter((el) => { return newMembersIds.indexOf(el.getId()) < 0; });

            return new UpdateGroupRequest().
                setKey(key).
                setDisplayName(displayName).
                addMembers(addMembers).
                removeMembers(removeMembers);
        }

        assembleViewedPrincipal(): Principal {
            return new GroupBuilder(this.getPersistedItem().asGroup()).
                setDisplayName(this.principalWizardHeader.getDisplayName()).
                setMembers(this.getMembersWizardStepForm().getMembers().map((el) => { return el.getKey(); })).
                build();
        }

        isPersistedEqualsViewed(): boolean {
            var persistedPrincipal = this.getPersistedItem().asGroup();
            var viewedPrincipal = this.assembleViewedPrincipal().asGroup();
            // Group/User order can be different for viewed and persisted principal
            viewedPrincipal.getMembers().sort((a,b) => { return a.getId().localeCompare(b.getId()); });
            persistedPrincipal.getMembers().sort((a,b) => { return a.getId().localeCompare(b.getId()); });

            return viewedPrincipal.equals(persistedPrincipal);
        }
    }
}
