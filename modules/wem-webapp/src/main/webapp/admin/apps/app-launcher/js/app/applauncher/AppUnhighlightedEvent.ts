module app.launcher {

    export class AppUnhighlightedEvent {

        private application: Application;

        constructor(app: Application) {
            this.application = app;
        }

        getApplication(): Application {
            return this.application;
        }
    }
}