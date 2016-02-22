module app.wizard {

    export class PathGuardWizardPanelParams extends UserItemWizardPanelParams {

        pathGuard: api.security.PathGuard;

        pathGuardKey: string;

        setPathGuard(value: api.security.PathGuard): PathGuardWizardPanelParams {
            this.pathGuard = value;
            return this;
        }

        setAppBarTabId(value: api.app.bar.AppBarTabId): PathGuardWizardPanelParams {
            this.tabId = value;
            return this;
        }

        setPathGuardKey(value: string): PathGuardWizardPanelParams {
            this.pathGuardKey = value;
            return this;
        }

        setPersistedPath(value: string): PathGuardWizardPanelParams {
            this.persistedPath = value;
            return this;
        }

        getPersistedItem() {
            return this.pathGuard;
        }

    }
}