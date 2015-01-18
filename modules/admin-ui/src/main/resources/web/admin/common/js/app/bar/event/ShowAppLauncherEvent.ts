module api.app.bar.event {

    export class ShowAppLauncherEvent extends api.event.Event {

        private application: api.app.Application;

        private sessionExpired: boolean;

        constructor(application: api.app.Application, sessionExpired?: boolean) {
            super();
            this.application = application;
            this.sessionExpired = !!sessionExpired;
        }

        getApplication(): api.app.Application {
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