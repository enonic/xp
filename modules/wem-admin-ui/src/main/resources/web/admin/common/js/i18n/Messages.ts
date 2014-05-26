module api.i18n {

    var messages: Object = {};

    var currentLocale: string = 'en';

    export function setLocale(locale: string) {
        currentLocale = locale;
    }

    export function addBundle(locale: string, bundle: Object) {
        if (messages[locale] == undefined) {
            messages[locale] = bundle;
        } else {
            wemjq.extend(messages[locale], messages[locale], bundle);
        }
    }

    export function message(key: string, args: any[]): string {

        var message = key;
        var current = messages[currentLocale];

        if ((current != undefined) && (current[key] != undefined)) {
            message = current[key];
        }

        return message.replace(/\$(\d+)/g, function () {
            return args[arguments[1] - 1];
        });
    }

}

// Global i18n message method
var _i18n = (key: string, ...args: any[]) => api.i18n.message(key, args);



