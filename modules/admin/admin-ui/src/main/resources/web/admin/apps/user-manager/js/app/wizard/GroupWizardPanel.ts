module app.wizard {

    import Group = api.security.Group;
    import GroupBuilder = api.security.GroupBuilder;
    import CreateGroupRequest = api.security.CreateGroupRequest;
    import UpdateGroupRequest = api.security.UpdateGroupRequest;

    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalLoader = api.security.PrincipalLoader;

    import UserTreeGridItem = app.browse.UserTreeGridItem;
    import UserTreeGridItemBuilder = app.browse.UserTreeGridItemBuilder;
    import UserTreeGridItemType = app.browse.UserTreeGridItemType;

    import WizardStep = api.app.wizard.WizardStep;

    export class GroupWizardPanel extends GroupRoleWizardPanel {

        constructor(params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            super(new GroupMembersWizardStepForm(), params, () => {
                this.addClass("group-wizard-panel");
                callback(this);
            });
        }

        createSteps(principal?: Principal): wemQ.Promise<any[]> {
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

            return wemQ.all(parallelPromises).spread<void>(() => {
                this.wizardHeader.setDisplayName(principal.getDisplayName());
                this.getDescriptionWizardStepForm().layout(principal);
                this.getMembersWizardStepForm().layout(principal);

                return wemQ(null);
            });
        }

        persistNewItem(): wemQ.Promise<Principal> {
            return this.produceCreateGroupRequest().sendAndParse().
                then((principal: Principal) => {
                    this.getPrincipalWizardHeader().disableNameInput();
                    this.wizardHeader.setAutoGenerationEnabled(false);
                    api.notify.showFeedback('Group was created!');
                    new api.security.UserItemCreatedEvent(principal, this.getUserStore(), this.isParentOfSameType()).fire();
                    this.notifyPrincipalNamed(principal);
                    (<PrincipalLoader>this.getMembersWizardStepForm().getLoader()).skipPrincipal(principal.getKey());

                    return principal;
                });
        }

        produceCreateGroupRequest(): CreateGroupRequest {
            var key = PrincipalKey.ofGroup(this.getUserStoreKey(), this.wizardHeader.getName()),
                name = this.wizardHeader.getDisplayName(),
                members = this.getMembersWizardStepForm().getMembers().map((el) => {
                    return el.getKey();
                }),
                description = this.getDescriptionWizardStepForm().getDescription();
            return new CreateGroupRequest().setKey(key).setDisplayName(name).setMembers(members).setDescription(description);
        }

        updatePersistedItem(): wemQ.Promise<Principal> {
            return this.produceUpdateGroupRequest(this.assembleViewedItem()).
                sendAndParse().
                then((principal: Principal) => {
                    if (!this.getPersistedItem().getDisplayName() && !!principal.getDisplayName()) {
                        this.notifyPrincipalNamed(principal);
                    }
                    api.notify.showFeedback('Group was updated!');
                    new api.security.UserItemUpdatedEvent(principal, this.getUserStore()).fire();

                    return principal;
                });
        }

        produceUpdateGroupRequest(viewedPrincipal: Principal): UpdateGroupRequest {
            var group = viewedPrincipal.asGroup(),
                key = group.getKey(),
                displayName = group.getDisplayName(),
                description = group.getDescription(),
                oldMembers = this.getPersistedItem().asGroup().getMembers(),
                oldMembersIds = oldMembers.map((el) => {
                    return el.getId();
                }),
                newMembers = group.getMembers(),
                newMembersIds = newMembers.map((el) => {
                    return el.getId();
                }),
                addMembers = newMembers.filter((el) => {
                    return oldMembersIds.indexOf(el.getId()) < 0;
                }),
                removeMembers = oldMembers.filter((el) => {
                    return newMembersIds.indexOf(el.getId()) < 0;
                });

            return new UpdateGroupRequest().
                setKey(key).
                setDisplayName(displayName).
                addMembers(addMembers).
                removeMembers(removeMembers).
                setDescription(description);
        }

        assembleViewedItem(): Principal {
            return new GroupBuilder(this.getPersistedItem().asGroup()).
                setMembers(this.getMembersWizardStepForm().getMembers().map((el) => {
                    return el.getKey();
                })).
                setDisplayName(this.wizardHeader.getDisplayName()).
                setDescription(this.getDescriptionWizardStepForm().getDescription()).
                build();
        }

        isPersistedEqualsViewed(): boolean {
            var persistedPrincipal = this.getPersistedItem().asGroup();
            var viewedPrincipal = this.assembleViewedItem().asGroup();
            // Group/User order can be different for viewed and persisted principal
            viewedPrincipal.getMembers().sort((a, b) => {
                return a.getId().localeCompare(b.getId());
            });
            persistedPrincipal.getMembers().sort((a, b) => {
                return a.getId().localeCompare(b.getId());
            });

            return viewedPrincipal.equals(persistedPrincipal);
        }
    }
}
