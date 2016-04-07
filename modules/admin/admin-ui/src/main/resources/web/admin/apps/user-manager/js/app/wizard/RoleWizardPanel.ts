module app.wizard {

    import Role = api.security.Role;
    import RoleBuilder = api.security.RoleBuilder;
    import CreateRoleRequest = api.security.CreateRoleRequest;
    import UpdateRoleRequest = api.security.UpdateRoleRequest;

    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import RoleKeys = api.security.RoleKeys;

    import UserTreeGridItem = app.browse.UserTreeGridItem;
    import UserTreeGridItemBuilder = app.browse.UserTreeGridItemBuilder;
    import UserTreeGridItemType = app.browse.UserTreeGridItemType;

    import WizardStep = api.app.wizard.WizardStep;

    export class RoleWizardPanel extends GroupRoleWizardPanel {

        constructor(params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            super(new RoleMembersWizardStepForm(), params, () => {
                this.addClass("role-wizard-panel");
                callback(this);
            });
        }

        createSteps(principal?: Principal): wemQ.Promise<any[]> {
            var deferred = wemQ.defer<WizardStep[]>();

            var steps: WizardStep[] = [];

            steps.push(new WizardStep("Role", this.getDescriptionWizardStepForm()));

            var principalKey: PrincipalKey = principal ? principal.getKey() : undefined;
            if (!RoleKeys.EVERYONE.equals(principalKey)) {
                steps.push(new WizardStep("Grants", this.getMembersWizardStepForm()));
            }

            this.setSteps(steps);

            deferred.resolve(steps);
            return deferred.promise;
        }

        doLayoutPersistedItem(principal: Principal): wemQ.Promise<void> {
            var parallelPromises: wemQ.Promise<any>[] = [
                this.createSteps(principal)
            ];

            return wemQ.all(parallelPromises).spread<void>(() => {
                this.wizardHeader.setDisplayName(principal.getDisplayName());
                this.getDescriptionWizardStepForm().layout(principal);
                this.getMembersWizardStepForm().layout(principal);

                return wemQ<void>(null);
            });
        }

        persistNewItem(): wemQ.Promise<Principal> {
            return this.produceCreateRoleRequest().sendAndParse().
                then((principal: Principal) => {
                    this.getPrincipalWizardHeader().disableNameInput();
                    this.wizardHeader.setAutoGenerationEnabled(false);
                    api.notify.showFeedback('Role was created!');
                    new api.security.UserItemCreatedEvent(principal, this.getUserStore(), this.isParentOfSameType()).fire();
                    this.notifyPrincipalNamed(principal);

                    return principal;
                });
        }

        produceCreateRoleRequest(): CreateRoleRequest {
            var key = PrincipalKey.ofRole(this.wizardHeader.getName()),
                name = this.wizardHeader.getDisplayName(),
                members = this.getMembersWizardStepForm().getMembers().map((el) => {
                    return el.getKey();
                }),
                description = this.getDescriptionWizardStepForm().getDescription();
            return new CreateRoleRequest().setKey(key).setDisplayName(name).setMembers(members).setDescription(description);
        }

        updatePersistedItem(): wemQ.Promise<Principal> {
            return this.produceUpdateRoleRequest(this.assembleViewedItem()).
                sendAndParse().
                then((principal: Principal) => {
                    if (!this.getPersistedItem().getDisplayName() && !!principal.getDisplayName()) {
                        this.notifyPrincipalNamed(principal);
                    }
                    api.notify.showFeedback('Role was updated!');
                    new api.security.UserItemUpdatedEvent(principal, this.getUserStore()).fire();

                    return principal;
                });
        }

        produceUpdateRoleRequest(viewedPrincipal: Principal): UpdateRoleRequest {
            var role = viewedPrincipal.asRole(),
                key = role.getKey(),
                displayName = role.getDisplayName(),
                description = role.getDescription(),
                oldMembers = this.getPersistedItem().asRole().getMembers(),
                oldMembersIds = oldMembers.map((el) => {
                    return el.getId();
                }),
                newMembers = role.getMembers(),
                newMembersIds = newMembers.map((el) => {
                    return el.getId();
                }),
                addMembers = newMembers.filter((el) => {
                    return oldMembersIds.indexOf(el.getId()) < 0;
                }),
                removeMembers = oldMembers.filter((el) => {
                    return newMembersIds.indexOf(el.getId()) < 0;
                });

            return new UpdateRoleRequest().
                setKey(key).
                setDisplayName(displayName).addMembers(addMembers).removeMembers(removeMembers).setDescription(description);
        }

        assembleViewedItem(): Principal {
            return new RoleBuilder(this.getPersistedItem().asRole()).
                setMembers(this.getMembersWizardStepForm().getMembers().map((el) => {
                    return el.getKey();
            })).setDisplayName(this.wizardHeader.getDisplayName()).setDescription(this.getDescriptionWizardStepForm().getDescription()).
                build();
        }

        isPersistedEqualsViewed(): boolean {
            var persistedPrincipal = this.getPersistedItem().asRole();
            var viewedPrincipal = this.assembleViewedItem().asRole();
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
