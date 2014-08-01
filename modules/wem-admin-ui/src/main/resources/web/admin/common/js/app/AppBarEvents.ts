module api.app {

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
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

    }

    export class ShowBrowsePanelEvent extends api.event.Event {

        static on(handler: (event: ShowBrowsePanelEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowBrowsePanelEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }

}