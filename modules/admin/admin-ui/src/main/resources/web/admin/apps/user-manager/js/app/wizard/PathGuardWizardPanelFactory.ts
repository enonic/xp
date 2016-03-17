module app.wizard {

    import PathGuard = api.security.PathGuard;

    export class PathGuardWizardPanelFactory {

        private creatingForNew: boolean;

        private pathGuardKey: api.security.PathGuardKey;

        private appBarTabId: api.app.bar.AppBarTabId;

        private pathGuardToEdit: PathGuard;

        setPathGuardKey(value: api.security.PathGuardKey): PathGuardWizardPanelFactory {
            this.pathGuardKey = value;
            return this;
        }

        setAppBarTabId(value: api.app.bar.AppBarTabId): PathGuardWizardPanelFactory {
            this.appBarTabId = value;
            return this;
        }

        createForNew(): wemQ.Promise<PathGuardWizardPanel> {

            this.creatingForNew = true;

            return this.newPathGuardWizardPanelForNew();
        }

        createForEdit(): wemQ.Promise<PathGuardWizardPanel> {

            this.creatingForNew = false;

            return this.loadPathGuardToEdit().then((loadedPathGuardToEdit: PathGuard) => {
                this.pathGuardToEdit = loadedPathGuardToEdit;
                return this.newPathGuardWizardPanelForEdit();
            });
        }

        private loadPathGuardToEdit(): wemQ.Promise<PathGuard> {
            return new api.security.GetPathGuardByKeyRequest(this.pathGuardKey).sendAndParse();
        }

        private newPathGuardWizardPanelForNew(): wemQ.Promise<app.wizard.PathGuardWizardPanel> {

            var deferred = wemQ.defer<app.wizard.PathGuardWizardPanel>();

            var wizardParams = new app.wizard.PathGuardWizardPanelParams().
                setPathGuardKey(this.pathGuardKey).
                setPersistedPath("/guards/").
                setAppBarTabId(this.appBarTabId);

            this.resolvePathGuardWizardPanel(deferred, wizardParams);

            return deferred.promise;
        }

        private newPathGuardWizardPanelForEdit(): wemQ.Promise<PathGuardWizardPanel> {

            var deferred = wemQ.defer<PathGuardWizardPanel>();

            var wizardParams = new PathGuardWizardPanelParams().
                setPathGuardKey(this.pathGuardKey).
                setPathGuard(this.pathGuardToEdit).
                setPersistedPath("/guards/").
                setAppBarTabId(this.appBarTabId);

            this.resolvePathGuardWizardPanel(deferred, wizardParams);

            return deferred.promise;
        }

        private resolvePathGuardWizardPanel(deferred: wemQ.Deferred<PathGuardWizardPanel>, wizardParams: PathGuardWizardPanelParams) {
            new PathGuardWizardPanel(wizardParams, (wizard: PathGuardWizardPanel) => {
                deferred.resolve(wizard);
            });

        }
    }

}