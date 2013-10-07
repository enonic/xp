module app_launcher {

    export class AppRouter {
        static HOME_HASH_ID = 'home';

        private applications:Application[];
        private appLauncher:AppLauncher;

        constructor(applications:Application[], appLauncher:AppLauncher) {
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
            this.applications.forEach((application:Application, idx:number) => {
                var appRoutPattern = application.getName() + '/:action:/:id:'; // optional parameters in URL: action, id
                crossroads.addRoute(appRoutPattern, (action:string, id:string) => {
                    if (action && id) {
                        console.log('Routing to app [' + application.getName() + '] ; Action: ' + action + ', Id=' + id);
                    }
                    this.appLauncher.loadApplication(application);
                });
            });
        }

        private setupHomeRouting() {
            crossroads.addRoute(AppRouter.HOME_HASH_ID, () => {
                this.appLauncher.showLauncherScreen();
            });
        }
    }

}
