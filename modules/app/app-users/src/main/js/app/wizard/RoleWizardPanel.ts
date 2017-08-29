import '../../api.ts';
import {GroupRoleWizardPanel} from './GroupRoleWizardPanel';
import {PrincipalWizardPanelParams} from './PrincipalWizardPanelParams';
import {RoleMembersWizardStepForm} from './RoleMembersWizardStepForm';

import Role = api.security.Role;
import RoleBuilder = api.security.RoleBuilder;
import CreateRoleRequest = api.security.CreateRoleRequest;
import UpdateRoleRequest = api.security.UpdateRoleRequest;

import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import RoleKeys = api.security.RoleKeys;

import WizardStep = api.app.wizard.WizardStep;
import i18n = api.util.i18n;

export class RoleWizardPanel extends GroupRoleWizardPanel {

    constructor(params: PrincipalWizardPanelParams) {

        super(new RoleMembersWizardStepForm(), params);

        this.addClass('role-wizard-panel');
    }

    createSteps(principal?: Principal): wemQ.Promise<WizardStep[]> {
        let steps: WizardStep[] = [];

        let descriptionStep = this.getDescriptionWizardStepForm();

        steps.push(new WizardStep(i18n('field.role'), descriptionStep));

        let principalKey: PrincipalKey = principal ? principal.getKey() : undefined;
        if (!RoleKeys.EVERYONE.equals(principalKey)) {
            let membersStep = this.getMembersWizardStepForm();
            steps.push(new WizardStep(i18n('field.grants'), membersStep));
        }

        return wemQ.resolve(steps);
    }

    persistNewItem(): wemQ.Promise<Principal> {
        return this.produceCreateRoleRequest().sendAndParse().then((principal: Principal) => {

            api.notify.showFeedback(i18n('notify.create.role'));
            new api.security.UserItemCreatedEvent(principal, this.getUserStore(), this.isParentOfSameType()).fire();
            this.notifyPrincipalNamed(principal);

            return principal;
        });
    }

    produceCreateRoleRequest(): CreateRoleRequest {
        let wizardHeader = this.getWizardHeader();
        let key = PrincipalKey.ofRole(wizardHeader.getName());
        let name = wizardHeader.getDisplayName();
        let members = this.getMembersWizardStepForm().getMembers().map(el => el.getKey());
        let description = this.getDescriptionWizardStepForm().getDescription();
        return new CreateRoleRequest().setKey(key).setDisplayName(name).setMembers(members).setDescription(description);
    }

    produceUpdateRequest(viewedPrincipal: Principal): UpdateRoleRequest {
        let role = viewedPrincipal.asRole();
        let key = role.getKey();
        let displayName = role.getDisplayName();
        let description = role.getDescription();
        let oldMembers = this.getPersistedItem().asRole().getMembers();
        let oldMembersIds = oldMembers.map(el => el.getId());
        let newMembers = role.getMembers();
        let newMembersIds = newMembers.map(el => el.getId());
        let addMembers = newMembers.filter(el => oldMembersIds.indexOf(el.getId()) < 0);
        let removeMembers = oldMembers.filter(el => newMembersIds.indexOf(el.getId()) < 0);

        return new UpdateRoleRequest().setKey(key).setDisplayName(displayName).addMembers(addMembers).removeMembers(
            removeMembers).setDescription(description);
    }

    assembleViewedItem(): Principal {
        return <Principal>new RoleBuilder(this.getPersistedItem().asRole()).setMembers(
            this.getMembersWizardStepForm().getMembers().map((el) => {
            return el.getKey();
        })).setDisplayName(this.getWizardHeader().getDisplayName()).setDescription(
            this.getDescriptionWizardStepForm().getDescription()).build();
    }

    isPersistedEqualsViewed(): boolean {
        let persistedPrincipal = this.getPersistedItem().asRole();
        let viewedPrincipal = this.assembleViewedItem().asRole();
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
