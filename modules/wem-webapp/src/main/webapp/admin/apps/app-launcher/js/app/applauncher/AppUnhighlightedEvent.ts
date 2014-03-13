module app.launcher {

    export class AppUnhighlightedEvent {

        private application: api.app.Application;

        constructor(app: api.app.Application) {
            this.application = app;
        }

        getApplication(): api.app.Application {
            return this.application;
        }
    }
}