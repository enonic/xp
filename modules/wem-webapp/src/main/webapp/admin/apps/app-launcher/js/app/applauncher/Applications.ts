module app_launcher {

    export class Applications {

        private static DESCR1 = 'Vel eius tation id, duo principes inciderint mediocritatem ut. Utroque ponderum duo ei. Cu cum choro delenit, est elitr utroque scripserit te. Mea ad lorem munere epicuri, clita omnes evertitur sed an. Eu aliquid ornatus principes vel. An eam justo malis debitis, ignota vocibus periculis in sit, alia adolescens ei has.';
        private static DESCR2 = 'Ius nibh voluptua lobortis ut, ex nec hinc vitae. Eu qui reque movet, tota vivendum postulant ea mea, his oporteat consetetur te. Deserunt vituperatoribus cum ut, cu pri euismod expetenda adipiscing. Facilisi assueverit ad his, at mel posidonium neglegentur consequuntur. Sapientem complectitur usu te, errem platonem ad eam, ne vis assum fastidii.';
        private static DESCR3 = 'Doctus recteque intellegat duo ut, cu vidit neglegentur duo, has tritani verterem id. Feugiat omnesque intellegam ut sea, elitr tractatos et mel, pri paulo definiebas liberavisse ea. Eos diceret electram no, ad liber dictas vel. Vix solum tation veritus eu.';
        private static DESCR4 = 'Ei malis impedit expetendis quo. His id iusto nihil quando, qui facer equidem molestie ei, dolore possit eripuit ad eum. Dissentiet instructior no nec, blandit salutandi ea vel, legere essent quo at. At eos consul perpetua. Sea duis postea et, cum agam justo cu. Nulla numquam vim no.';
        private static ICONS_PATH = api_util.getAdminUri('common/images/icons/metro/40x40/');

        private static applications:Application[] = [
            new Application('content-manager', 'Content Manager',
                Applications.ICONS_PATH + 'database.png',
                Applications.DESCR1),
            new Application('relationships', 'Relationships', Applications.ICONS_PATH + 'share.png', Applications.DESCR2),
            new Application('space-manager', 'Space Admin',
                Applications.ICONS_PATH + 'earth.png',
                Applications.DESCR3),
            new Application('schema-manager', 'Schema Manager',
                Applications.ICONS_PATH + 'signup.png',
                Applications.DESCR4),

            new Application('store-manager', 'Store Manager', Applications.ICONS_PATH + 'cart.png', Applications.DESCR4),
            new Application('segment-builder', 'Segment Builder', Applications.ICONS_PATH + 'pie.png', Applications.DESCR3),
            new Application('optimizer', 'Optimizer', Applications.ICONS_PATH + 'target.png', Applications.DESCR2),
            new Application('analytics', 'Analytics', Applications.ICONS_PATH + 'stats.png', Applications.DESCR1),

            new Application('accounts', 'Accounts', Applications.ICONS_PATH + 'users.png', Applications.DESCR2),
            new Application('module-manager', 'Modules', Applications.ICONS_PATH + 'puzzle.png', Applications.DESCR1),
            new Application('templates', 'Templates', Applications.ICONS_PATH + 'insert-template.png', Applications.DESCR4),
            new Application('diagnostics', 'Diagnostics', Applications.ICONS_PATH + 'aid.png', Applications.DESCR3)
        ];

        static getAllApps():Application[] {
            return Applications.applications;
        }

        static getAppById(id:string):Application {
            var app:Application = null;
            Applications.getAllApps().forEach((currentApp:Application) => {
               if (currentApp.getId() == id) {
                   app = currentApp;
               }
            });
            return app;
        }
    }
}