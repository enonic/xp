module app.wizard {

    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;

    export class PrincipalWizardPanelFactory {

        private creatingForNew: boolean;

        private principalKey: PrincipalKey;

        private appBarTabId: api.app.bar.AppBarTabId;

        private principalToEdit: Principal;


        setPrincipalToEdit(value: PrincipalKey): PrincipalWizardPanelFactory {
            this.principalKey = value;
            return this;
        }

        setAppBarTabId(value: api.app.bar.AppBarTabId): PrincipalWizardPanelFactory {
            this.appBarTabId = value;
            return this;
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


        private newPrincipalWizardPanelForEdit(): wemQ.Promise<PrincipalWizardPanel> {

            var deferred = wemQ.defer<PrincipalWizardPanel>();

            var wizardParams = new PrincipalWizardPanelParams().
                setAppBarTabId(this.appBarTabId).
                setPersistedPrincipal(this.principalToEdit);

            switch (this.principalToEdit.getType()) {
            case PrincipalType.ROLE:
                new RoleWizardPanel(wizardParams, (wizard: PrincipalWizardPanel) => {
                    deferred.resolve(wizard);
                });
                break;
            case PrincipalType.USER:
                /* TODO: Replace with UserWizardPanel implementation. */
                new PrincipalWizardPanel(wizardParams, (wizard: PrincipalWizardPanel) => {
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

            return deferred.promise;
        }
    }
}