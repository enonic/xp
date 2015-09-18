module api.app {

    export class ShowAppLauncherEvent extends api.event.Event {

        private application: Application;

        private sessionExpired: boolean;

        constructor(application: Application, sessionExpired?: boolean) {
            super();
            this.application = application;
            this.sessionExpired = !!sessionExpired;
        }

        getApplication(): Application {
            return this.application;
        }

        isSessionExpired(): boolean {
            return this.sessionExpired;
        }

        static on(handler: (event: ShowAppLauncherEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

    }
}