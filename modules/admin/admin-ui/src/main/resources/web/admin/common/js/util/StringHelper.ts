module api.util {

    export class StringHelper {

        static EMPTY_STRING: string = '';

        static SAVE_CHAR_CODES: Object = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;',
            '/': '&#x2F;'
        };

        static limit(str: string, length: number, ending: string = '\u2026'): string {
            return StringHelper.isEmpty(str) ? StringHelper.EMPTY_STRING : str.substring(0, length) + ending;
        }

        static capitalize(str: string): string {
            return StringHelper.isEmpty(str) ? StringHelper.EMPTY_STRING : str.charAt(0).toUpperCase() + str.slice(1);
        }

        static capitalizeAll(str: string): string {
            return StringHelper.isEmpty(str) ? StringHelper.EMPTY_STRING : str.replace(/(?:^|\s)\S/g, function (ch: string) {
                return ch.toUpperCase();
            });
        }

        static escapeHtml(str: string): string {
            return StringHelper.isEmpty(str) ? StringHelper.EMPTY_STRING : str.replace(/[&<>"'\/]/g,((char: string) => {
                return StringHelper.SAVE_CHAR_CODES[char];
            }));
        }

        static isUpperCase(str: string): boolean {
            if (StringHelper.isEmpty(str)) {
                return false;
            }
            return str.toUpperCase() == str;
        }

        static isLowerCase(str: string): boolean {
            if (StringHelper.isEmpty(str)) {
                return false;
            }
            return str.toLowerCase() == str;
        }

        static isMixedCase(str: string): boolean {
            if (StringHelper.isEmpty(str)) {
                return false;
            }
            return !StringHelper.isLowerCase(str) && !StringHelper.isUpperCase(str);
        }

        static isEmpty(str: string): boolean {
            return !str || str.length == 0;
        }

        static isBlank(str: string): boolean {
            return StringHelper.isEmpty(str) || str.trim().length == 0;
        }

        /**
         * Removes carriage characters '\r' from string.
         *
         * Carriage chars could appear before '\n' in multiline strings depending on browser and OS.
         * Useful to clean up value obtained from <textarea>.
         *
         * @param str string to be cleaned up.
         * @returns {string} string without '\r' characters.
         */
        static removeCarriageChars(str: string): string {
            return StringHelper.isEmpty(str) ? StringHelper.EMPTY_STRING : str.replace(/\r/g, '');
        }

        static removeWhitespaces(str: string): string {
            return StringHelper.isEmpty(str) ? StringHelper.EMPTY_STRING : str.replace(/\s/g, '');
        }

        static removeEmptyStrings(elements: string[]): string[] {
            return !elements ? [] : elements.filter((element: string) => {
                return !StringHelper.isEmpty(element);
            });
        }

        static substringBetween(str: string, left: string, right: string): string {
            if (StringHelper.isEmpty(str) || StringHelper.isEmpty(left) || StringHelper.isEmpty(right)) {
                return StringHelper.EMPTY_STRING;
            }
            let start = str.indexOf(left);
            if (start !== -1) {
                let end = str.indexOf(right, start + left.length);
                if (end !== -1) {
                    return str.substring(start + left.length, end);
                }
            }
            return StringHelper.EMPTY_STRING;
        }

        static testRegex(regex:string, target:string): boolean {
            return new RegExp(regex).test(target);
        }

        /**
         * Replaces given tokens in given string.
         * @param str
         * @param tokens
         * @returns {string}
         */
        static format(str: string, ...tokens: any[]): string {
            const regex: RegExp = /\{\{|\}\}|\{(\d+)\}/g;
            return StringHelper.isEmpty(str) ? StringHelper.EMPTY_STRING : str.replace(regex, function (m: string, n: number) {
                if (m == '{{') { return '{'; }
                if (m == '}}') { return '}'; }
                return tokens[n];
            });
        }

    }

}
