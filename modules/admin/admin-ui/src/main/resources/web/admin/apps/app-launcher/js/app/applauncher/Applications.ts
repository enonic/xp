module app.launcher {

    export class Applications {

        private static _instance: Applications = null;

        private apps: api.app.Application[];

        private appIndex: {[id:string]:api.app.Application};

        constructor(applications: api.app.Application[]) {
            this.apps = applications;
            this.appIndex = {};
            this.apps.forEach((currentApp: api.app.Application) => {
                this.appIndex[currentApp.getId()] = currentApp;
            });
        }

        static init(): wemQ.Promise<Applications> {
            return new app.launcher.LauncherApplicationsRequest().
                sendAndParse().
                then((applications: api.app.Application[]) => {
                    Applications._instance = new Applications(applications);
                    return Applications._instance;
                });
        }

        static instance(): Applications {
            return Applications._instance;
        }

        getAllApps(): api.app.Application[] {
            return this.apps;
        }

        getAppById(id: string): api.app.Application {
            return this.appIndex[id];
        }

        getAppsByIds(ids: string[]): api.app.Application[] {
            var apps: api.app.Application[] = [];
            ids.forEach((appId: string) => {
                var app = this.getAppById(appId);
                if (app) {
                    apps.push(app);
                }
            });
            return apps;
        }
    }
}