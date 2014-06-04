module app.launcher {

    export class Applications {

        private static PROFILE_DESC = 'Access and edit your profile';
        private static DESCR1 = 'Vel eius tation id, duo principes inciderint mediocritatem ut. Utroque ponderum duo ei. Cu cum choro delenit, est elitr utroque scripserit te. Mea ad lorem munere epicuri, clita omnes evertitur sed an. Eu aliquid ornatus principes vel. An eam justo malis debitis, ignota vocibus periculis in sit, alia adolescens ei has.';
        private static DESCR2 = 'Ius nibh voluptua lobortis ut, ex nec hinc vitae. Eu qui reque movet, tota vivendum postulant ea mea, his oporteat consetetur te. Deserunt vituperatoribus cum ut, cu pri euismod expetenda adipiscing. Facilisi assueverit ad his, at mel posidonium neglegentur consequuntur. Sapientem complectitur usu te, errem platonem ad eam, ne vis assum fastidii.';
        private static DESCR3 = 'Doctus recteque intellegat duo ut, cu vidit neglegentur duo, has tritani verterem id. Feugiat omnesque intellegam ut sea, elitr tractatos et mel, pri paulo definiebas liberavisse ea. Eos diceret electram no, ad liber dictas vel. Vix solum tation veritus eu.';
        private static DESCR4 = 'Ei malis impedit expetendis quo. His id iusto nihil quando, qui facer equidem molestie ei, dolore possit eripuit ad eum. Dissentiet instructior no nec, blandit salutandi ea vel, legere essent quo at. At eos consul perpetua. Sea duis postea et, cum agam justo cu. Nulla numquam vim no.';

        private static applications:api.app.Application[] = [
            new api.app.Application('profile', 'Profile', 'PF', api.util.getAdminUri('common/images/tsi-profil.jpg'), Applications.PROFILE_DESC, null, true),
            new api.app.Application('content-manager', 'Content Manager', 'CM', 'database', Applications.DESCR1),
            new api.app.Application('relationships', 'Relationships', 'RS', 'share2', Applications.DESCR2),
            new api.app.Application('schema-manager', 'Schema Manager', 'SC', 'signup', Applications.DESCR4),

            new api.app.Application('store-manager', 'Store Manager', 'ST', 'cart', Applications.DESCR4),
            new api.app.Application('segment-builder', 'Segment Builder', 'SB', 'pie2', Applications.DESCR3),
            new api.app.Application('optimizer', 'Optimizer', 'OP', 'target3', Applications.DESCR2),
            new api.app.Application('analytics', 'Analytics', 'AN', 'stats', Applications.DESCR1),

            new api.app.Application('accounts', 'Accounts', 'AC', 'users', Applications.DESCR2),
            new api.app.Application('module-manager', 'Modules', 'MD', 'puzzle', Applications.DESCR1),
            new api.app.Application('template-manager', 'Templates', 'TM', 'earth', Applications.DESCR4),
            new api.app.Application('diagnostics', 'Diagnostics', 'DI', 'aid', Applications.DESCR3)
        ];

        static getAllApps():api.app.Application[] {
            return Applications.applications;
        }

        static getAppById(id:string):api.app.Application {
            var app:api.app.Application = null;
            Applications.getAllApps().forEach((currentApp:api.app.Application) => {
               if (currentApp.getId() == id) {
                   app = currentApp;
               }
            });
            return app;
        }
    }
}