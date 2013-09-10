///<reference path='Application.ts' />

module app_model {

    var descr1 = 'Vel eius tation id, duo principes inciderint mediocritatem ut. Utroque ponderum duo ei. Cu cum choro delenit, est elitr utroque scripserit te. Mea ad lorem munere epicuri, clita omnes evertitur sed an. Eu aliquid ornatus principes vel. An eam justo malis debitis, ignota vocibus periculis in sit, alia adolescens ei has.';
    var descr2 = 'Ius nibh voluptua lobortis ut, ex nec hinc vitae. Eu qui reque movet, tota vivendum postulant ea mea, his oporteat consetetur te. Deserunt vituperatoribus cum ut, cu pri euismod expetenda adipiscing. Facilisi assueverit ad his, at mel posidonium neglegentur consequuntur. Sapientem complectitur usu te, errem platonem ad eam, ne vis assum fastidii.';
    var descr3 = 'Doctus recteque intellegat duo ut, cu vidit neglegentur duo, has tritani verterem id. Feugiat omnesque intellegam ut sea, elitr tractatos et mel, pri paulo definiebas liberavisse ea. Eos diceret electram no, ad liber dictas vel. Vix solum tation veritus eu.';
    var descr4 = 'Ei malis impedit expetendis quo. His id iusto nihil quando, qui facer equidem molestie ei, dolore possit eripuit ad eum. Dissentiet instructior no nec, blandit salutandi ea vel, legere essent quo at. At eos consul perpetua. Sea duis postea et, cum agam justo cu. Nulla numquam vim no.';
    var iconsPath = api_util.getAbsoluteUri('admin/resources/images/icons/metro/40x40/');
    var applications:app_model.Application[] = [
        new app_model.Application('Content Manager', '/admin2/apps/content-manager/index.html', iconsPath + 'database.png', descr1),
        new app_model.Application('Relationships', '', iconsPath + 'share.png', descr2),
        new app_model.Application('Space Admin', '/admin2/apps/space-manager/index.html', iconsPath + 'earth.png', descr3),
        new app_model.Application('Schema Manager', '/admin2/apps/schema-manager/index.html', iconsPath + 'signup.png', descr4),

        new app_model.Application('Store Manager', '', iconsPath + 'cart.png', descr4),
        new app_model.Application('Segment Builder', '', iconsPath + 'pie.png', descr3),
        new app_model.Application('Optimizer', '', iconsPath + 'target.png', descr2),
        new app_model.Application('Analytics', '', iconsPath + 'stats.png', descr1),

        new app_model.Application('Accounts', '', iconsPath + 'users.png', descr2),
        new app_model.Application('Modules', '', iconsPath + 'puzzle.png', descr1),
        new app_model.Application('Templates', '', iconsPath + 'insert-template.png', descr4),
        new app_model.Application('Diagnostics', '', iconsPath + 'aid.png', descr3)
    ];

    export class Applications {

        static getAllApps():app_model.Application[] {
            return applications;
        }
    }
}