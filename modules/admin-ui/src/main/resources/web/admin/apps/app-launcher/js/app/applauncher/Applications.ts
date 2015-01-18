module app.launcher {

    export class Applications {

        private static DESCR1 = 'Vel eius tation id, duo principes inciderint mediocritatem ut. Utroque ponderum duo ei. Cu cum choro delenit, est elitr utroque scripserit te. Mea ad lorem munere epicuri, clita omnes evertitur sed an. Eu aliquid ornatus principes vel. An eam justo malis debitis, ignota vocibus periculis in sit, alia adolescens ei has.';
        private static DESCR2 = 'Ius nibh voluptua lobortis ut, ex nec hinc vitae. Eu qui reque movet, tota vivendum postulant ea mea, his oporteat consetetur te. Deserunt vituperatoribus cum ut, cu pri euismod expetenda adipiscing. Facilisi assueverit ad his, at mel posidonium neglegentur consequuntur. Sapientem complectitur usu te, errem platonem ad eam, ne vis assum fastidii.';
        private static DESCR3 = 'Doctus recteque intellegat duo ut, cu vidit neglegentur duo, has tritani verterem id. Feugiat omnesque intellegam ut sea, elitr tractatos et mel, pri paulo definiebas liberavisse ea. Eos diceret electram no, ad liber dictas vel. Vix solum tation veritus eu.';

        private static apps: api.app.Application[];

        static getAllApps(): api.app.Application[] {
            if (!Applications.apps) {
                Applications.apps = Applications.initApps();
            }
            return Applications.apps;
        }

        static getAppById(id: string): api.app.Application {
            var app: api.app.Application = null;
            Applications.getAllApps().forEach((currentApp: api.app.Application) => {
                if (currentApp.getId() == id) {
                    app = currentApp;
                }
            });
            return app;
        }

        private static initApps(): api.app.Application[] {
            return [
                new api.app.Application('content-manager', 'Content Manager', 'CM', 'database', Applications.DESCR1),
                new api.app.Application('user-manager', 'User Manager', 'UM', 'users', Applications.DESCR2),
                new api.app.Application('module-manager', 'Modules', 'MD', 'puzzle', Applications.DESCR3)
            ];
        }
    }
}