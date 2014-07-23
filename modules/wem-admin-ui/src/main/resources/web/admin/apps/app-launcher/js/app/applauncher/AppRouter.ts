module app.launcher {

    export class AppRouter {
        static HOME_HASH_ID = 'home';

        private applications: api.app.Application[];
        private appLauncher: AppLauncher;

        constructor(applications: api.app.Application[], appLauncher: AppLauncher) {
            this.applications = applications;
            this.appLauncher = appLauncher;
            this.initRouting();
            this.setupAppsRouting();
            this.setupHomeRouting();
            this.handleInitialUrl();
        }

        private handleInitialUrl() {
            var initialUrlHash = hasher.getHash();
            if (!initialUrlHash) {
                hasher.setHash(AppRouter.HOME_HASH_ID);
            } else {
                crossroads.parse(initialUrlHash);
            }
        }

        private initRouting() {
            //setup crossroads
            crossroads.routed.add(console.log, console);

            //setup hasher
            function parseHash(newHash, oldHash) {
                console.log('Routing, from "#' + oldHash + '" to "#' + newHash + '"');
                crossroads.parse(newHash);
            }

            hasher.changed.add(parseHash); //parse hash changes
            hasher.init(); //start listening for history change
        }

        private setupAppsRouting() {
            this.applications.forEach((application: api.app.Application, idx: number) => {
                var appRoutPattern = application.getId() + '/:p1:/:p2:/:p3:'; // optional parameters in URL: action, id
                crossroads.addRoute(appRoutPattern, (p1: string, p2: string, p3: string) => {

                    var path: api.rest.Path = new api.rest.Path(<string[]>this.arrayWithoutNulls(Array.prototype.slice.call(arguments)));
                    this.appLauncher.loadApplication(application.setPath(path));
                });
            });
        }

        private setupHomeRouting() {
            crossroads.addRoute(AppRouter.HOME_HASH_ID, () => {
                this.appLauncher.showLauncherScreen();
            });
        }

        private arrayWithoutNulls(array: any[]): any[] {
            var arrayWithoutNulls: any[] = [];
            for (var i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    arrayWithoutNulls.push(array[i]);
                }
            }
            return arrayWithoutNulls;
        }
    }

}
