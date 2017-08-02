/**
 * Common functions.
 *
 * @example
 * var common = require('/lib/xp/common');
 *
 * @module common
 */

var NamePrettyfier = Java.type('com.enonic.xp.name.NamePrettyfier');

/**
 * Transform a text string so that it can be used in cases where the range of accepted characters is restricted.
 *
 * Some usage examples are: as an XP content or node name, as a principal name, in a URL or in a filesystem path.
 *
 * The following changes will be applied to the input text:
 * - convert characters to lowercase (according to the rules of the default locale)
 * - replace punctuation symbols and blank spaces with the hyphen character ('-')
 * - remove some unsafe and invisible Unicode characters
 * - strip duplicated hyphen characters
 * - remove diacritic characters
 * - map letters to the English alphabet
 *
 * @example-ref examples/common/prettify.js
 *
 * @param {string} text Text string to prettify.
 * @returns {string} Prettified text.
 */
exports.prettify = function (text) {
    if (text == null || text === '') {
        return '';
    }
    return NamePrettyfier.create(text);
};
