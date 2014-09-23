module api.util {

    export class StringHelper {

        static limit(str: string, length: number, ending: string = "…"): string {
            str = str.substring(0, length) + ending;
            return str;
        }

        static capitalize(str: string): string {
            return str.charAt(0).toUpperCase() + str.slice(1);
        }

        static capitalizeAll(str: string): string {
            return str.replace(/(?:^|\s)\S/g, function (ch) {
                return ch.toUpperCase();
            });
        }

        static removeInvalidChars(str: string): string {
            return str.replace(/\s/g, "");
        }

        static isEmpty(str: string): boolean {
            return !str;
        }

        static isBlank(str: string): boolean {
            return (!str || /^\s*$/.test(str));
        }

        static isStringsEqual(str1: string, str2: string): boolean {
            return (!str1 && !str2) || (str1 == str2);
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
            return str.replace(/\r/g, "");
        }

        static removeEmptyStringElements(elements: string[]): string[] {
            var filteredElements: string[] = [];
            elements.forEach((element: string) => {
                if (element.length > 0) {
                    filteredElements.push(element);
                }
            });
            return filteredElements;
        }

        static substringBetween(str: string, left: string, right: string): string {
            if ((typeof str === "undefined") || (str === null) || (typeof left === "undefined") || (left === null) ||
                (typeof right === "undefined") || (right === null)) {
                return '';
            }
            var start = str.indexOf(left);
            if (start !== -1) {
                var end = str.indexOf(right, start + left.length);
                if (end !== -1) {
                    return str.substring(start + left.length, end);
                }
            }
            return '';
        }

        /**
         * Replaces given tokens in given string.
         * @param str
         * @param tokens
         * @returns {string}
         */
        static format(str: string, ...tokens: string[]): string {
            return str.replace(/\{\{|\}\}|\{(\d+)\}/g, function (m, n) {
                if (m == "{{") { return "{"; }
                if (m == "}}") { return "}"; }
                return tokens[n];
            });
        }

    }

}
