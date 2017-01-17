import '../../api.ts';
import {PrincipalWizardPanel} from './PrincipalWizardPanel';
import {UserEmailWizardStepForm} from './UserEmailWizardStepForm';
import {UserPasswordWizardStepForm} from './UserPasswordWizardStepForm';
import {UserMembershipsWizardStepForm} from './UserMembershipsWizardStepForm';
import {PrincipalWizardPanelParams} from './PrincipalWizardPanelParams';

import User = api.security.User;
import UserBuilder = api.security.UserBuilder;
import CreateUserRequest = api.security.CreateUserRequest;
import UpdateUserRequest = api.security.UpdateUserRequest;

import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
import UserStoreKey = api.security.UserStoreKey;
import GetPrincipalByKeyRequest = api.security.GetPrincipalByKeyRequest;

import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
import WizardStep = api.app.wizard.WizardStep;

export class UserWizardPanel extends PrincipalWizardPanel {

    private userEmailWizardStepForm: UserEmailWizardStepForm;
    private userPasswordWizardStepForm: UserPasswordWizardStepForm;
    private userMembershipsWizardStepForm: UserMembershipsWizardStepForm;

    constructor(params: PrincipalWizardPanelParams) {

        super(params);

        this.addClass('user-wizard-panel');
    }

    saveChanges(): wemQ.Promise<Principal> {
        if (!this.isRendered() ||
            (this.userEmailWizardStepForm.isValid()
             && (this.getPersistedItem() || this.userPasswordWizardStepForm.isValid()))) {

            return super.saveChanges();
        } else {
            this.showErrors();

            return wemQ<Principal>(null);
        }
    }

    createSteps(principal?: Principal): WizardStep[] {
        let steps: WizardStep[] = [];

        this.userEmailWizardStepForm = new UserEmailWizardStepForm(this.getParams().userStore.getKey());
        this.userPasswordWizardStepForm = new UserPasswordWizardStepForm();
        this.userMembershipsWizardStepForm = new UserMembershipsWizardStepForm();

        steps.push(new WizardStep('User', this.userEmailWizardStepForm));
        steps.push(new WizardStep('Authentication', this.userPasswordWizardStepForm));
        steps.push(new WizardStep('Groups & Roles', this.userMembershipsWizardStepForm));

        return steps;
    }

    doLayout(persistedPrincipal: Principal): wemQ.Promise<void> {

        return super.doLayout(persistedPrincipal).then(() => {

            if (this.isRendered()) {

                let viewedPrincipal = this.assembleViewedItem();
                if (!this.isPersistedEqualsViewed()) {

                    console.warn(`Received Principal from server differs from what's viewed:`);
                    console.warn(' viewedPrincipal: ', viewedPrincipal);
                    console.warn(' persistedPrincipal: ', persistedPrincipal);

                    const msg = 'Received Principal from server differs from what you have. Would you like to load changes from server?';

                    ConfirmationDialog.get()
                        .setQuestion(msg)
                        .setYesCallback(() => this.doLayoutPersistedItem(persistedPrincipal.clone()))
                        .setNoCallback(() => { /* empty */})
                        .show();
                }

                return wemQ<void>(null);
            } else {
                return this.doLayoutPersistedItem(persistedPrincipal ? persistedPrincipal.clone() : null);
            }

        });
    }

    protected doLayoutPersistedItem(principal: Principal): wemQ.Promise<void> {

        return super.doLayoutPersistedItem(principal).then(() => {
            if (!!principal) {
                this.userEmailWizardStepForm.layout(principal);
                this.userPasswordWizardStepForm.layout(principal);
                this.userMembershipsWizardStepForm.layout(principal);
            }
        });
    }

    persistNewItem(): wemQ.Promise<Principal> {
        return this.produceCreateUserRequest().sendAndParse().then((principal: Principal) => {

            new api.security.UserItemCreatedEvent(principal, this.getUserStore(), this.isParentOfSameType()).fire();

            api.notify.showFeedback('User was created!');
            this.notifyPrincipalNamed(principal);

            return principal;
        });
    }

    produceCreateUserRequest(): CreateUserRequest {
        let wizardHeader = this.getWizardHeader();
        let login = wizardHeader.getName();
        let key = PrincipalKey.ofUser(this.getUserStore().getKey(), login);
        let name = wizardHeader.getDisplayName();
        let email = this.userEmailWizardStepForm.getEmail();
        let password = this.userPasswordWizardStepForm.getPassword();
        let memberships = this.userMembershipsWizardStepForm.getMemberships().map((el) => {
            return el.getKey();
        });
        return new CreateUserRequest()
            .setKey(key)
            .setDisplayName(name)
            .setEmail(email)
            .setLogin(login)
            .setPassword(password)
            .setMemberships(memberships);
    }

    updatePersistedItem(): wemQ.Promise<Principal> {
        return super.updatePersistedItem().then((principal:Principal) => {
            //remove after users event handling is configured and layout is updated on receiving upd from server
            this.userMembershipsWizardStepForm.layout(principal);
            return principal;
        });
    }

    produceUpdateRequest(viewedPrincipal:Principal):UpdateUserRequest {
        let user = viewedPrincipal.asUser();
        let key = user.getKey();
        let displayName = user.getDisplayName();
        let email = user.getEmail();
        let login = user.getLogin();
        let oldMemberships = this.getPersistedItem().asUser().getMemberships().map(el => el.getKey());
        let oldMembershipsIds = oldMemberships.map(el => el.getId());
        let newMemberships = user.getMemberships().map(el => el.getKey());
        let newMembershipsIds = newMemberships.map(el => el.getId());
        let addMemberships = newMemberships.filter(el => oldMembershipsIds.indexOf(el.getId()) < 0);
        let removeMemberships = oldMemberships.filter(el => newMembershipsIds.indexOf(el.getId()) < 0);

        return new UpdateUserRequest().setKey(key).setDisplayName(displayName).setEmail(email).setLogin(login).addMemberships(
            addMemberships).removeMemberships(removeMemberships);
    }

    assembleViewedItem(): Principal {
        let wizardHeader = this.getWizardHeader();
        return new UserBuilder(!!this.getPersistedItem() ? this.getPersistedItem().asUser() : null).setEmail(
            this.userEmailWizardStepForm.getEmail()).setLogin(wizardHeader.getName()).setMemberships(
            this.userMembershipsWizardStepForm.getMemberships()).setDisplayName(
            wizardHeader.getDisplayName()).// setDisabled().
        build();
    }

    isPersistedEqualsViewed(): boolean {
        let persistedPrincipal = this.getPersistedItem().asUser();
        let viewedPrincipal = this.assembleViewedItem().asUser();
        // Group/User order can be different for viewed and persisted principal
        viewedPrincipal.getMemberships().sort((a, b) => {
            return a.getKey().toString().localeCompare(b.getKey().toString());
        });
        persistedPrincipal.getMemberships().sort((a, b) => {
            return a.getKey().toString().localeCompare(b.getKey().toString());
        });

        // #hack - The newly added members will have different modifiedData
        let viewedMembershipsKeys = viewedPrincipal.getMemberships().map((el) => {
            return el.getKey();
        });
        let persistedMembershipsKeys = persistedPrincipal.getMemberships().map((el) => {
            return el.getKey();
        });

        if (api.ObjectHelper.arrayEquals(viewedMembershipsKeys, persistedMembershipsKeys)) {
            viewedPrincipal.setMemberships(persistedPrincipal.getMemberships());
        }

        return viewedPrincipal.equals(persistedPrincipal);
    }

    hasUnsavedChanges(): boolean {
        let persistedPrincipal = this.getPersistedItem();
        let email = this.userEmailWizardStepForm.getEmail();
        let memberships = this.userMembershipsWizardStepForm.getMemberships();
        if (persistedPrincipal == undefined) {
            let wizardHeader = this.getWizardHeader();
            return wizardHeader.getName() !== '' ||
                   wizardHeader.getDisplayName() !== '' ||
                   (!!email && email !== '') ||
                   (!!memberships && memberships.length !== 0);
        } else {
            return !this.isPersistedEqualsViewed();
        }
    }

    private showErrors() {
        if (!this.userEmailWizardStepForm.isValid()) {
            this.showEmailErrors();
        }

        if (!(this.getPersistedItem() || this.userPasswordWizardStepForm.isValid())) {
            this.showPasswordErrors();
        }
    }

    private showEmailErrors() {
        let formEmail = this.userEmailWizardStepForm.getEmail();
        if (api.util.StringHelper.isEmpty(formEmail)) {
            api.notify.showError('E-mail can not be empty.');
        } else if (!this.userEmailWizardStepForm.isValid()) {
            api.notify.showError('E-mail is invalid.');
        }

    }

    private showPasswordErrors() {
        let password = this.userPasswordWizardStepForm.getPassword();
        if (api.util.StringHelper.isEmpty(password)) {
            api.notify.showError('Password can not be empty.');
        } else if (!this.userEmailWizardStepForm.isValid()) {
            api.notify.showError('Password is invalid.');
        }
    }
}
