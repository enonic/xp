declare global {
    interface XpLibraries {
        '/lib/xp/common': typeof import('./common');
    }
}

interface NamePrettyfier {
    create(text: string): string;
}

/**
 * Common functions.
 *
 * @example
 * var common = require('/lib/xp/common');
 *
 * @module common
 */

const NamePrettyfier: NamePrettyfier = __.newBean<NamePrettyfier>('com.enonic.xp.lib.common.NamePrettyfierHandler');

/**
 * Transform a text string so that it can be safely used in cases where the range of accepted characters is restricted.
 *
 * Some usage examples are: as an XP content or node name, as a principal name, in a URL or in a filesystem path.
 *
 * The following changes will be applied to the input text:
 * - convert characters to lowercase (according to the rules of the default locale)
 * - replace punctuation symbols and blank spaces with the hyphen character ('-')
 * - remove some unsafe and invisible Unicode characters
 * - strip duplicated hyphen characters
 * - remove diacritic characters
 * - map letters to the English alphabet (ASCII encoding)
 *
 * @example-ref examples/common/sanitize.js
 *
 * @param {string} text Text string to sanitize.
 * @returns {string} Sanitized text.
 */
export function sanitize(text: string): string {
    if (text == null || text === '') {
        return '';
    }
    return NamePrettyfier.create(text);
}
