module api.app.bar.event {

    export class ShowAppLauncherEvent extends api.event.Event {

        private application: api.app.Application;

        constructor(application: api.app.Application) {
            super();
            this.application = application;
        }

        getApplication(): api.app.Application {
            return this.application;
        }

        static on(handler: (event: ShowAppLauncherEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

    }
}