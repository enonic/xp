import '../../api.ts';
import {GroupRoleWizardPanel} from './GroupRoleWizardPanel';
import {PrincipalWizardPanelParams} from './PrincipalWizardPanelParams';
import {GroupMembersWizardStepForm} from './GroupMembersWizardStepForm';

import Group = api.security.Group;
import GroupBuilder = api.security.GroupBuilder;
import CreateGroupRequest = api.security.CreateGroupRequest;
import UpdateGroupRequest = api.security.UpdateGroupRequest;

import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import PrincipalLoader = api.security.PrincipalLoader;

import WizardStep = api.app.wizard.WizardStep;
import i18n = api.util.i18n;

export class GroupWizardPanel extends GroupRoleWizardPanel {

    constructor(params: PrincipalWizardPanelParams) {

        super(new GroupMembersWizardStepForm(), params);

        this.addClass('group-wizard-panel');
    }

    createSteps(principal?: Principal): wemQ.Promise<WizardStep[]> {
        let steps: WizardStep[] = [];

        let descriptionStep = this.getDescriptionWizardStepForm();
        let membersStep = this.getMembersWizardStepForm();

        steps.push(new WizardStep(i18n('field.groups'), descriptionStep));
        steps.push(new WizardStep(i18n('field.grants'), membersStep));

        return wemQ.resolve(steps);
    }

    persistNewItem(): wemQ.Promise<Principal> {

        return this.produceCreateGroupRequest().sendAndParse().then((principal: Principal) => {

            api.notify.showFeedback(i18n('notify.create.group'));
            new api.security.UserItemCreatedEvent(principal, this.getUserStore(), this.isParentOfSameType()).fire();
            this.notifyPrincipalNamed(principal);

            (<PrincipalLoader>this.getMembersWizardStepForm().getLoader()).skipPrincipal(principal.getKey());

            return principal;
        });
    }

    produceCreateGroupRequest(): CreateGroupRequest {
        let wizardHeader = this.getWizardHeader();
        let key = PrincipalKey.ofGroup(this.getUserStore().getKey(), wizardHeader.getName());
        let name = wizardHeader.getDisplayName();
        let members = this.getMembersWizardStepForm().getMembers().map(el => el.getKey());
        let description = this.getDescriptionWizardStepForm().getDescription();
        return new CreateGroupRequest()
            .setKey(key)
            .setDisplayName(name)
            .setMembers(members)
            .setDescription(description);
    }

    produceUpdateRequest(viewedPrincipal:Principal):UpdateGroupRequest {
        let group = viewedPrincipal.asGroup();
        let key = group.getKey();
        let displayName = group.getDisplayName();
        let description = group.getDescription();
        let oldMembers = this.getPersistedItem().asGroup().getMembers();
        let oldMembersIds = oldMembers.map(el => el.getId());
        let newMembers = group.getMembers();
        let newMembersIds = newMembers.map(el => el.getId());
        let addMembers = newMembers.filter(el => oldMembersIds.indexOf(el.getId()) < 0);
        let removeMembers = oldMembers.filter(el => newMembersIds.indexOf(el.getId()) < 0);

        return new UpdateGroupRequest().setKey(key).setDisplayName(displayName).addMembers(addMembers).removeMembers(
            removeMembers).setDescription(description);
    }

    assembleViewedItem(): Principal {
        return <Principal>new GroupBuilder(this.getPersistedItem().asGroup())
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
