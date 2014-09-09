module api.util {

    export class NumberHelper {

        static isWholeNumber(value: any): boolean {
            return NumberHelper.isNumber(value) && (<number>value) % 1 == 0;
        }

        static isNumber(value: any): boolean {
            return typeof value === 'number' && !isNaN(value);
        }
    }
}
