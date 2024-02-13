declare global {
    interface XpLibraries {
        '/lib/xp/i18n': typeof import('./i18n');
    }
}

export interface LocalizeParams {
    key: string;
    locale?: string | string[];
    values?: string[];
    bundles?: string[];
    application?: string;
}

interface LocaleScriptBean {
    localize(key: string, locales: string[], values: ScriptValue, bundles?: string[] | null): string;

    getPhrases(locale: string[], bundles: string[]): Record<string, string>;

    getSupportedLocales(bundles: string[]): string[];

    setApplication(value?: string | null): void;
}

/**
 * Internationalization functions.
 *
 * @example
 * var i18nLib = require('/lib/xp/i18n');
 *
 * @module i18n
 */

/**
 * This function localizes a phrase.
 *
 * @example-ref examples/i18n/localize.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key The property key.
 * @param {string|string[]} [params.locale] A string-representation of a locale, or an array of locales in preferred order. If the locale is not set, the site language is used.
 * @param {string[]} [params.values] Optional placeholder values.
 * @param {string[]} [params.bundles] Optional list of bundle names.
 * @param {string} [params.application] Application key where to find resource bundles. Defaults to current application.
 *
 * @returns {string} The localized string.
 */
export function localize(params: LocalizeParams): string {
    const bean = __.newBean<LocaleScriptBean>('com.enonic.xp.lib.i18n.LocaleScriptBean');

    const {
        key,
        locale = [],
        values = [],
        bundles,
        application,
    } = params ?? {};

    const locales = ([] as string[]).concat(locale);
    bean.setApplication(__.nullOrValue(application));
    return bean.localize(
        key,
        locales,
        __.toScriptValue(values),
        __.nullOrValue(bundles),
    );
}

/**
 * This function returns all phrases for the given locale and bundles.
 *
 * @param {string|string[]} locale A string-representation of a locale, or an array of locales in preferred order.
 * @param {string[]} bundles List of bundle names.  Bundle names are specified as paths, relative to the `src/main/resources` folder.
 * @param {string} [application] Application key where to find resource bundles. Defaults to current application.
 *
 * @returns {object} An object of all phrases.
 *
 * @example
 * i18nLib.getPhrases('en', ['i18n/phrases'])
 */
export function getPhrases(locale: string | string[], bundles: string[], application?: string): Record<string, string> {
    const bean = __.newBean<LocaleScriptBean>('com.enonic.xp.lib.i18n.LocaleScriptBean');
    bean.setApplication(__.nullOrValue(application));
    const locales: string[] = ([] as string[]).concat(locale);
    return __.toNativeObject(bean.getPhrases(locales, bundles));
}

/**
 * This function returns the list of supported locale codes for the specified bundles.
 *
 * @param {string[]} bundles List of bundle names.
 * @param {string} application Application key where to find resource bundles. Defaults to current application.
 *
 * @returns {string[]} A list of supported locale codes for the specified bundles.
 */
export function getSupportedLocales(bundles: string[], application?: string): string[] {
    const bean = __.newBean<LocaleScriptBean>('com.enonic.xp.lib.i18n.LocaleScriptBean');
    bean.setApplication(__.nullOrValue(application));
    return __.toNativeObject(bean.getSupportedLocales(bundles));
}
