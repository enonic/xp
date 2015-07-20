module app.launcher {

    export class Applications {

        private static apps: api.app.Application[];

        private static appIndex: {[id:string]:api.app.Application};

        static getAllApps(): api.app.Application[] {
            if (!Applications.apps) {
                Applications.initApps();
            }
            return Applications.apps;
        }

        static initApps() {
            Applications.apps = Applications.createApps();
            Applications.appIndex = {};
            Applications.apps.forEach((currentApp: api.app.Application) => {
                Applications.appIndex[currentApp.getId()] = currentApp;
            });
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

        private static createApps(): api.app.Application[] {
            return [
                new api.app.Application('content-manager', 'Content Manager', 'CM', 'database'),
                new api.app.Application('user-manager', 'User Manager', 'UM', 'users'),
                new api.app.Application('module-manager', 'Applications', 'AM', 'puzzle')
            ];
        }
    }
}