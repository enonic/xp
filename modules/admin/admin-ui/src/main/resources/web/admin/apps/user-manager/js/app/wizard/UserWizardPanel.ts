module app.wizard {

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

        constructor(params: PrincipalWizardPanelParams, callback: (wizard: PrincipalWizardPanel) => void) {

            this.userEmailWizardStepForm = new UserEmailWizardStepForm(params.userStore ? params.userStore.getKey()  : null);
            this.userPasswordWizardStepForm = new UserPasswordWizardStepForm();
            this.userMembershipsWizardStepForm = new UserMembershipsWizardStepForm();

            super(params, () => {
                this.addClass("user-wizard-panel");
                callback(this);
            });
        }

        giveInitialFocus() {
            this.wizardHeader.giveFocus();
            this.startRememberFocus();
        }

        saveChanges(): wemQ.Promise<Principal> {
            if (this.userEmailWizardStepForm.isValid() && (this.getPersistedItem() || this.userPasswordWizardStepForm.isValid())) {
                return super.saveChanges();
            } else {
                this.showErrors();

                var deferred = wemQ.defer<Principal>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

        createSteps(principal?: Principal): wemQ.Promise<any[]> {
            var deferred = wemQ.defer<WizardStep[]>();

            var steps: WizardStep[] = [];

            steps.push(new WizardStep("User", this.userEmailWizardStepForm));
            steps.push(new WizardStep("Authentication", this.userPasswordWizardStepForm));
            steps.push(new WizardStep("Groups & Roles", this.userMembershipsWizardStepForm));

            this.setSteps(steps);

            deferred.resolve(steps);
            return deferred.promise;
        }

        preLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.doLayoutPersistedItem(null);

            deferred.resolve(null);

            return deferred.promise;
        }

        postLayoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.wizardHeader.initNames("", "", false);

            deferred.resolve(null);
            return deferred.promise;
        }

        layoutPersistedItem(persistedPrincipal: Principal): wemQ.Promise<void> {
            if (!this.constructing) {

                var deferred = wemQ.defer<void>();
                var viewedPrincipal = this.assembleViewedItem();

                if (!this.isPersistedEqualsViewed()) {

                    console.warn("Received Principal from server differs from what's viewed:");
                    console.warn(" viewedPrincipal: ", viewedPrincipal);
                    console.warn(" persistedPrincipal: ", persistedPrincipal);

                    ConfirmationDialog.get().
                        setQuestion("Received Principal from server differs from what you have. Would you like to load changes from server?").
                        setYesCallback(() => this.doLayoutPersistedItem(persistedPrincipal.clone())).
                        setNoCallback(() => {/* Do nothing */
                        }).
                        show();
                }
                deferred.resolve(null);
                return deferred.promise;
            } else {
                return this.doLayoutPersistedItem(persistedPrincipal.clone());
            }
        }

        doLayoutPersistedItem(principal: Principal): wemQ.Promise<void> {
            var parallelPromises: wemQ.Promise<any>[] = [
                // Load attachments?
                this.createSteps()
            ];

            if (!!principal) {
                parallelPromises.push(
                    new GetPrincipalByKeyRequest(this.getPersistedItem().getKey()).
                        includeUserMemberships(true).
                        sendAndParse().
                        then((p: Principal) => {
                            this.getPersistedItem().asUser();
                            this.getPersistedItem().asUser().setMemberships(p.asUser().getMemberships());
                            principal = this.getPersistedItem().asUser().clone();
                        })
                );
            }

            return wemQ.all(parallelPromises).spread<void>(() => {
                this.wizardHeader.setDisplayName(principal.getDisplayName());
                this.userEmailWizardStepForm.layout(principal);
                this.userPasswordWizardStepForm.layout(principal);
                this.userMembershipsWizardStepForm.layout(principal);

                return wemQ(null);
            });
        }

        persistNewItem(): wemQ.Promise<Principal> {
            return this.produceCreateUserRequest().sendAndParse().
                then((principal: Principal) => {
                    this.wizardHeader.disableNameInput();
                    this.wizardHeader.setAutoGenerationEnabled(false);
                    api.notify.showFeedback('User was created!');
                    new api.security.UserItemCreatedEvent(principal, this.getUserStore(), this.isParentOfSameType()).fire();
                    this.userPasswordWizardStepForm.updatePrincipal(principal);
                    this.notifyPrincipalNamed(principal);
                    return principal;
                });
        }

        produceCreateUserRequest(): CreateUserRequest {
            var key = PrincipalKey.ofUser(this.getUserStoreKey(), this.wizardHeader.getName()),
                name = this.wizardHeader.getDisplayName(),
                email = this.userEmailWizardStepForm.getEmail(),
                login = this.wizardHeader.getName(),
                password = this.userPasswordWizardStepForm.getPassword(),
                memberships = this.userMembershipsWizardStepForm.getMemberships().map((el) => {
                    return el.getKey();
                });
            return new CreateUserRequest().setKey(key).
                setDisplayName(name).
                setEmail(email).
                setLogin(login).
                setPassword(password).
                setMemberships(memberships);
        }

        updatePersistedItem(): wemQ.Promise<Principal> {
            return this.produceUpdateUserRequest(this.assembleViewedItem()).
                sendAndParse().
                then((principal: Principal) => {
                    if (!this.getPersistedItem().getDisplayName() && !!principal.getDisplayName()) {
                        this.notifyPrincipalNamed(principal);
                    }
                    this.userEmailWizardStepForm.layout(principal);
                    api.notify.showFeedback('User was updated!');
                    new api.security.UserItemUpdatedEvent(principal, this.getUserStore()).fire();

                    return principal;
                });
        }

        produceUpdateUserRequest(viewedPrincipal: Principal): UpdateUserRequest {
            var user = viewedPrincipal.asUser(),
                key = user.getKey(),
                displayName = user.getDisplayName(),
                email = user.getEmail(),
                login = user.getLogin(),
                oldMemberships = this.getPersistedItem().asUser().getMemberships().map((el) => {
                    return el.getKey();
                }),
                oldMembershipsIds = oldMemberships.map((el) => {
                    return el.getId();
                }),
                newMemberships = user.getMemberships().map((el) => {
                    return el.getKey();
                }),
                newMembershipsIds = newMemberships.map((el) => {
                    return el.getId();
                }),
                addMemberships = newMemberships.filter((el) => {
                    return oldMembershipsIds.indexOf(el.getId()) < 0;
                }),
                removeMemberships = oldMemberships.filter((el) => {
                    return newMembershipsIds.indexOf(el.getId()) < 0;
                });

            return new UpdateUserRequest().
                setKey(key).
                setDisplayName(displayName).
                setEmail(email).
                setLogin(login).
                addMemberships(addMemberships).
                removeMemberships(removeMemberships);
        }

        assembleViewedItem(): Principal {
            return new UserBuilder(!!this.getPersistedItem() ? this.getPersistedItem().asUser() : null).
                setEmail(this.userEmailWizardStepForm.getEmail()).
                setLogin(this.wizardHeader.getName()).
                setMemberships(this.userMembershipsWizardStepForm.getMemberships()).
                setDisplayName(this.wizardHeader.getDisplayName()).
                // setDisabled().
                build();
        }

        isPersistedEqualsViewed(): boolean {
            var persistedPrincipal = this.getPersistedItem().asUser();
            var viewedPrincipal = this.assembleViewedItem().asUser();
            // Group/User order can be different for viewed and persisted principal
            viewedPrincipal.getMemberships().sort((a, b) => {
                return a.getKey().getId().localeCompare(b.getKey().getId());
            });
            persistedPrincipal.getMemberships().sort((a, b) => {
                return a.getKey().getId().localeCompare(b.getKey().getId());
            });

            // #hack - The newly added members will have different modifiedData
            var viewedMembershipsKeys = viewedPrincipal.getMemberships().map((el) => {
                    return el.getKey()
                }),
                persistedMembershipsKeys = persistedPrincipal.getMemberships().map((el) => {
                    return el.getKey()
                });

            if (api.ObjectHelper.arrayEquals(viewedMembershipsKeys, persistedMembershipsKeys)) {
                viewedPrincipal.setMemberships(persistedPrincipal.getMemberships());
            }

            return viewedPrincipal.equals(persistedPrincipal);
        }

        hasUnsavedChanges(): boolean {
            var persistedPrincipal = this.getPersistedItem(),
                email = this.userEmailWizardStepForm.getEmail(),
                memberships = this.userMembershipsWizardStepForm.getMemberships();
            if (persistedPrincipal == undefined) {
                return this.wizardHeader.getName() !== "" ||
                       this.wizardHeader.getDisplayName() !== "" ||
                       (!!email && email !== "") ||
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
            var formEmail = this.userEmailWizardStepForm.getEmail();
            if (api.util.StringHelper.isEmpty(formEmail)) {
                api.notify.showError("E-mail can not be empty.");
            } else if (!this.userEmailWizardStepForm.isValid()) {
                api.notify.showError("E-mail is invalid.");
            }

        }

        private showPasswordErrors() {
            var password = this.userPasswordWizardStepForm.getPassword();
            if (api.util.StringHelper.isEmpty(password)) {
                api.notify.showError("Password can not be empty.");
            } else if (!this.userEmailWizardStepForm.isValid()) {
                api.notify.showError("Password is invalid.");
            }
        }
    }
}