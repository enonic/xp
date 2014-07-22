module api.app {

    export class ShowAppLauncherEvent extends api.event.Event2 {

        private application: api.app.Application;

        constructor(application: api.app.Application) {
            super();
            this.application = application;
        }

        getApplication(): api.app.Application {
            return this.application;
        }

        static on(handler: (event: ShowAppLauncherEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

    }

    export class ShowBrowsePanelEvent extends api.event.Event {

        constructor() {
            super('showBrowsePanel');
        }

        static on(handler: (event: ShowBrowsePanelEvent) => void) {
            api.event.onEvent('showBrowsePanel', handler);
        }

    }

}