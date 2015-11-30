module app.launcher {

    export class Applications {

        private static apps: api.app.Application[];

        private static appIndex: {[id:string]:api.app.Application};

        static init(): wemQ.Promise<api.app.Application[]> {
            return new app.launcher.LauncherApplicationsRequest().
                sendAndParse().
                then((applications: api.app.Application[]) => {
                    Applications.apps = applications;
                    Applications.appIndex = {};
                    Applications.apps.forEach((currentApp: api.app.Application) => {
                        Applications.appIndex[currentApp.getId()] = currentApp;
                    });
                    return Applications.apps;
                });
        }

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
    }
}