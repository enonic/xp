module api.util {

    let messages: Object = {};

    export function i18nInit(bundle: Object) {
        messages = bundle;
    }

    export function i18n(key: string, ...args: any[]): string {
        let message = key;

        if ((messages != null) && (messages[key] != null)) {
            message = messages[key];
        }

        return message.replace(/\${\d+}/g, function () {
            return args[arguments[1] - 1];
        });
    }

}
