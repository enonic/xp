module api.i18n {

    let messages: Object = {};

    let currentLocale: string = 'en';

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

        let message = key;
        let current = messages[currentLocale];

        if ((current != undefined) && (current[key] != undefined)) {
            message = current[key];
        }

        return message.replace(/\$(\d+)/g, function () {
            return args[arguments[1] - 1];
        });
    }

    export function i18n(key: string, ...args: any[]): string {
        return message(key, args);
    }

}
