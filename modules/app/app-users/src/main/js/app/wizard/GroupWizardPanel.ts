import '../../api.ts';
import {GroupRoleWizardPanel} from './GroupRoleWizardPanel';
import {PrincipalWizardPanelParams} from './PrincipalWizardPanelParams';
import {GroupMembersWizardStepForm} from './GroupMembersWizardStepForm';
import {MembershipsWizardStepForm, MembershipsType} from './MembershipsWizardStepForm';

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

    private membershipsWizardStepForm: MembershipsWizardStepForm;

    constructor(params: PrincipalWizardPanelParams) {

        super(new GroupMembersWizardStepForm(), params);

        this.addClass('group-wizard-panel');
    }

    createSteps(principal?: Principal): WizardStep[] {
        const steps: WizardStep[] = [];

        const descriptionStep = this.getDescriptionWizardStepForm();
        const membersStep = this.getMembersWizardStepForm();
        this.membershipsWizardStepForm = new MembershipsWizardStepForm(MembershipsType.ROLES);

        steps.push(new WizardStep(i18n('field.groups'), descriptionStep));
        steps.push(new WizardStep(i18n('field.grants'), membersStep));
        steps.push(new WizardStep(i18n('field.roles'), this.membershipsWizardStepForm));

        return steps;
    }

    protected doLayoutPersistedItem(principal: Principal): wemQ.Promise<void> {

        return super.doLayoutPersistedItem(principal).then(() => {
            if (principal) {
                this.membershipsWizardStepForm.layout(principal);
            }
        });
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
        const wizardHeader = this.getWizardHeader();
        const key = PrincipalKey.ofGroup(this.getUserStore().getKey(), wizardHeader.getName());
        const name = wizardHeader.getDisplayName();
        const members = this.getMembersWizardStepForm().getMembers().map(el => el.getKey());
        const description = this.getDescriptionWizardStepForm().getDescription();
        const memberships = this.membershipsWizardStepForm.getMemberships().map(el => el.getKey());
        return new CreateGroupRequest()
            .setKey(key)
            .setDisplayName(name)
            .setMembers(members)
            .setDescription(description);
        // .setMemberships(memberships);
    }

    updatePersistedItem(): wemQ.Promise<Principal> {
        return super.updatePersistedItem().then((principal: Principal) => {
            //remove after users event handling is configured and layout is updated on receiving upd from server
            this.membershipsWizardStepForm.layout(principal);
            return principal;
        });
    }

    produceUpdateRequest(viewedPrincipal:Principal):UpdateGroupRequest {
        const group = viewedPrincipal.asGroup();
        const key = group.getKey();
        const displayName = group.getDisplayName();
        const description = group.getDescription();
        const oldMembers = this.getPersistedItem().asGroup().getMembers();
        const oldMembersIds = oldMembers.map(el => el.getId());
        const newMembers = group.getMembers();
        const newMembersIds = newMembers.map(el => el.getId());
        const addMembers = newMembers.filter(el => oldMembersIds.indexOf(el.getId()) < 0);
        const removeMembers = oldMembers.filter(el => newMembersIds.indexOf(el.getId()) < 0);

        return new UpdateGroupRequest()
            .setKey(key)
            .setDisplayName(displayName)
            .addMembers(addMembers)
            .removeMembers(removeMembers)
            .setDescription(description);
    }

    assembleViewedItem(): Principal {
        return <Principal>new GroupBuilder(this.getPersistedItem().asGroup())
            .setMembers(this.getMembersWizardStepForm().getMembers().map(el => el.getKey()))
            .setMemberships(this.membershipsWizardStepForm.getMemberships())
            .setDisplayName(this.getWizardHeader().getDisplayName())
            .setDescription(this.getDescriptionWizardStepForm().getDescription())
            .build();
    }

    isPersistedEqualsViewed(): boolean {
        const persistedPrincipal = this.getPersistedItem().asGroup();
        const viewedPrincipal = this.assembleViewedItem().asGroup();
        // Group/User order can be different for viewed and persisted principal
        viewedPrincipal.getMembers().sort((a, b) => a.getId().localeCompare(b.getId()));
        persistedPrincipal.getMembers().sort((a, b) => a.getId().localeCompare(b.getId()));

        return viewedPrincipal.equals(persistedPrincipal);
    }
}
