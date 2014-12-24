module app.wizard {

    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;
    import UserStore = api.security.UserStore;

    export class PrincipalWizardPanelFactory {

        private creatingForNew: boolean;

        private principalKey: PrincipalKey;

        private appBarTabId: api.app.bar.AppBarTabId;

        private principalToEdit: Principal;

        private principalType: PrincipalType;

        private principalPath: string;

        private userStore: UserStore;

        private parentOfSameType: boolean;

        setPrincipalToEdit(value: PrincipalKey): PrincipalWizardPanelFactory {
            this.principalKey = value;
            return this;
        }

        setPrincipalType(value: PrincipalType): PrincipalWizardPanelFactory {
            this.principalType = value;
            return this;
        }

        setPrincipalPath(value: string): PrincipalWizardPanelFactory {
            this.principalPath = value;
            return this;
        }

        setUserStore(value: UserStore): PrincipalWizardPanelFactory {
            this.userStore = value;
            return this;
        }

        setParentOfSameType(value: boolean): PrincipalWizardPanelFactory {
            this.parentOfSameType = value;
            return this;
        }

        setAppBarTabId(value: api.app.bar.AppBarTabId): PrincipalWizardPanelFactory {
            this.appBarTabId = value;
            return this;
        }

        createForNew(): wemQ.Promise<PrincipalWizardPanel> {

            this.creatingForNew = true;

            return this.newPrincipalWizardPanelForNew();
        }

        createForEdit(): wemQ.Promise<PrincipalWizardPanel> {

            this.creatingForNew = false;

            return this.loadPrincipalToEdit().then((loadedPrincipalToEdit: Principal) => {
                this.principalToEdit = loadedPrincipalToEdit;
                return this.newPrincipalWizardPanelForEdit();
            });
        }

        private loadPrincipalToEdit(): wemQ.Promise<Principal> {
            return new api.security.GetPrincipalByKeyRequest(this.principalKey).sendAndParse();
        }

        private newPrincipalWizardPanelForNew(): wemQ.Promise<app.wizard.PrincipalWizardPanel> {

            var deferred = wemQ.defer<app.wizard.PrincipalWizardPanel>();

            var wizardParams = new app.wizard.PrincipalWizardPanelParams().
                setPrincipalType(this.principalType).
                setPrincipalPath(this.principalPath).
                setUserStore(this.userStore).
                setParentOfSameType(this.parentOfSameType).
                setAppBarTabId(this.appBarTabId);

            this.resolvePrincipalWizardPanel(deferred, wizardParams);

            return deferred.promise;
        }

        private newPrincipalWizardPanelForEdit(): wemQ.Promise<PrincipalWizardPanel> {

            var deferred = wemQ.defer<PrincipalWizardPanel>();

            var wizardParams = new PrincipalWizardPanelParams().
                setAppBarTabId(this.appBarTabId).
                setPrincipalType(this.principalType).
                setPrincipalPath(this.principalPath).
                setUserStore(this.userStore).
                setPersistedPrincipal(this.principalToEdit);

            this.resolvePrincipalWizardPanel(deferred, wizardParams);

            return deferred.promise;
        }

        private resolvePrincipalWizardPanel(deferred: wemQ.Deferred<PrincipalWizardPanel>, wizardParams: PrincipalWizardPanelParams) {
            switch (wizardParams.persistedType) {
            case PrincipalType.ROLE:
                new RoleWizardPanel(wizardParams, (wizard: PrincipalWizardPanel) => {
                    deferred.resolve(wizard);
                });
                break;
            case PrincipalType.USER:
                new UserWizardPanel(wizardParams, (wizard: PrincipalWizardPanel) => {
                    deferred.resolve(wizard);
                });
                break;
            case PrincipalType.GROUP:
                new GroupWizardPanel(wizardParams, (wizard: PrincipalWizardPanel) => {
                    deferred.resolve(wizard);
                });
                break;
            default:
                new PrincipalWizardPanel(wizardParams, (wizard: PrincipalWizardPanel) => {
                    deferred.resolve(wizard);
                });
            }
        }
    }
}