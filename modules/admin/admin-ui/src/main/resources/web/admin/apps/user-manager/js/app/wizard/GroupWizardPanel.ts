import "../../api.ts";
import {GroupRoleWizardPanel} from "./GroupRoleWizardPanel";
import {PrincipalWizardPanelParams} from "./PrincipalWizardPanelParams";
import {GroupMembersWizardStepForm} from "./GroupMembersWizardStepForm";

import Group = api.security.Group;
import GroupBuilder = api.security.GroupBuilder;
import CreateGroupRequest = api.security.CreateGroupRequest;
import UpdateGroupRequest = api.security.UpdateGroupRequest;

import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalLoader = api.security.PrincipalLoader;

import WizardStep = api.app.wizard.WizardStep;

export class GroupWizardPanel extends GroupRoleWizardPanel {

    constructor(params: PrincipalWizardPanelParams) {

        super(new GroupMembersWizardStepForm(), params);

        this.addClass("group-wizard-panel");
    }

    createSteps(principal?: Principal): WizardStep[] {
        let steps: WizardStep[] = [];

        let descriptionStep = this.getDescriptionWizardStepForm();
        let membersStep = this.getMembersWizardStepForm();

        steps.push(new WizardStep("Group", descriptionStep));
        steps.push(new WizardStep("Grants", membersStep));

        return steps;
    }

    persistNewItem(): wemQ.Promise<Principal> {

        return this.produceCreateGroupRequest().sendAndParse().then((principal: Principal) => {

            api.notify.showFeedback('Group was created!');
            new api.security.UserItemCreatedEvent(principal, this.getUserStore(), this.isParentOfSameType()).fire();
            this.notifyPrincipalNamed(principal);

            (<PrincipalLoader>this.getMembersWizardStepForm().getLoader()).skipPrincipal(principal.getKey());

            return principal;
        });
    }

    produceCreateGroupRequest(): CreateGroupRequest {
        let wizardHeader = this.getWizardHeader();
        let key = PrincipalKey.ofGroup(this.getUserStore().getKey(), wizardHeader.getName()),
            name = wizardHeader.getDisplayName(),
            members = this.getMembersWizardStepForm().getMembers().map((el) => {
                return el.getKey();
            }),
            description = this.getDescriptionWizardStepForm().getDescription();
        return new CreateGroupRequest()
            .setKey(key)
            .setDisplayName(name)
            .setMembers(members)
            .setDescription(description);
    }

    produceUpdateRequest(viewedPrincipal:Principal):UpdateGroupRequest {
        let group = viewedPrincipal.asGroup(),
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

        return new UpdateGroupRequest().setKey(key).setDisplayName(displayName).addMembers(addMembers).removeMembers(
            removeMembers).setDescription(description);
    }

    assembleViewedItem(): Principal {
        return new GroupBuilder(this.getPersistedItem().asGroup())
            .setMembers(this.getMembersWizardStepForm().getMembers().map((el) => {
                return el.getKey();
            }))
            .setDisplayName(this.getWizardHeader().getDisplayName())
            .setDescription(this.getDescriptionWizardStepForm().getDescription())
            .build();
    }

    isPersistedEqualsViewed(): boolean {
        let persistedPrincipal = this.getPersistedItem().asGroup();
        let viewedPrincipal = this.assembleViewedItem().asGroup();
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
