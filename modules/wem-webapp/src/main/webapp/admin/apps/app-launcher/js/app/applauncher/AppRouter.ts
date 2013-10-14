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
                var appRoutPattern = application.getName() + '/:p1:/:p2:/:p3:'; // optional parameters in URL: action, id
                crossroads.addRoute(appRoutPattern, (p1:string, p2:string, p3:string) => {
                    this.appLauncher.loadApplication(application);


                    var path:api_rest.Path = new api_rest.Path(<string[]>this.arrayWithoutNulls(Array.prototype.slice.call(arguments)));

                    var intervalId = setInterval(() => {
                        if (this.runAction(application, path)) {
                            clearInterval(intervalId);
                        }
                    }, 200);
                });
            });
        }

        private runAction(app:Application, path:api_rest.Path):boolean {
            if (app.isLoaded()) {
                if (app.getWindow().route) {
                    app.getWindow().route(path);

                    return true;
                }
            }
            return false;
        }

        private setupHomeRouting() {
            crossroads.addRoute(AppRouter.HOME_HASH_ID, () => {
                this.appLauncher.showLauncherScreen();
            });
        }

        private arrayWithoutNulls(array:any[]):any[] {
            var arrayWithoutNulls:any[] = [];
            for (var i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    arrayWithoutNulls.push(array[i]);
                }
            }
            return arrayWithoutNulls;
        }
    }

}
