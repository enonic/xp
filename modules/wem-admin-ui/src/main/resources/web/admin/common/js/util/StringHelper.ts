module api.util {

    export function limitString(str: string, length: number, ending: string = "…"): string {
        str = str.substring(0, length) + ending;
        return str;
    }

    export function capitalize(str: string) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    export function capitalizeAll(str: string) {
        return str.replace(/(?:^|\s)\S/g, function (ch) {
            return ch.toUpperCase();
        });
    }

    export function removeInvalidChars(str: string) {
        return str.replace(/\s/g, "");
    }

    export function isStringEmpty(str: string): boolean {
        return !str;
    }

    export function isStringBlank(str: string) {

        return (!str || /^\s*$/.test(str));
    }

    export function isStringsEqual(str1: string, str2: string): boolean {
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
    export function removeCarriageChars(str: string): string {
        return str.replace(/\r/g, "");
    }

    export function removeEmptyStringElements(elements: string[]): string[] {
        var filteredElements: string[] = [];
        elements.forEach((element: string) => {
            if (element.length > 0) {
                filteredElements.push(element);
            }
        });
        return filteredElements;
    }

    export function substringBetween(str: string, left: string, right: string) {
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
    export function replaceTokens(str: string, tokens: Object) {

        var result = str.replace(/%\w+%/g, function (all) {
            return tokens[all] || all;
        });
        return result;
    }

}
