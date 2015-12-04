module app.launcher {

    export class Applications {

        private static apps: api.app.Application[] = [];

        private static appIndex: {[id:string]:api.app.Application} = {};

        static getAllApps(): api.app.Application[] {
            return Applications.apps;
        }

        static getAppById(id: string): api.app.Application {
            return Applications.appIndex[id];
        }

        static getAppsByIds(ids: string[]): api.app.Application[] {
            var apps: api.app.Application[] = [];
            ids.forEach((appId: string) => {
                var app = Applications.getAppById(appId);
                if (app) {
                    apps.push(app);
                }
            });
            return apps;
        }

        static setApps(apps: api.app.Application[]) {
            this.apps = apps;
            this.appIndex = {};
            this.apps.forEach((currentApp: api.app.Application) => {
                this.appIndex[currentApp.getId()] = currentApp;
            });
        }
    }
}