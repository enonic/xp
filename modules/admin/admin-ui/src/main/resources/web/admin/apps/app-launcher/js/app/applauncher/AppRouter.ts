module app.launcher {

    export class AppRouter {
        static HOME_HASH_ID = 'home';

        private appRoutes: CrossroadsJs.Route[];
        private appLauncher: AppLauncher;

        constructor(appLauncher: AppLauncher) {
            this.appLauncher = appLauncher;
            this.appRoutes = [];
            this.initRouting();
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

        setAllowedApps(applications: api.app.Application[]) {
            this.appRoutes.forEach((appRoute: CrossroadsJs.Route) => {
                crossroads.removeRoute(appRoute);
            });
            this.appRoutes = [];
            applications.forEach((application: api.app.Application, idx: number) => {
                var appRoutPattern = application.getId() + '/:p1:/:p2:/:p3:'; // optional parameters in URL: action, id
                var appRoute: CrossroadsJs.Route = crossroads.addRoute(appRoutPattern, (p1: string, p2: string, p3: string) => {
                    var pathValues = [p1, p2, p3].filter((p)=> p != undefined);
                    var path: api.rest.Path = new api.rest.Path(pathValues);
                    this.appLauncher.showApplication(application.setPath(path));
                });
                this.appRoutes.push(appRoute);
            });
        }

        private setupHomeRouting() {
            crossroads.addRoute(AppRouter.HOME_HASH_ID, () => {
                this.appLauncher.showLauncherScreen();
            });
        }

    }

}
