module api.util {

    export class NumberHelper {

        static isWholeNumber(value: any): boolean {
            return NumberHelper.isNumber(value) && (<number>value) % 1 == 0;
        }

        static isNumber(value: any): boolean {
            return typeof value === 'number' && !isNaN(value);
        }

        static randomBetween(from: number, to: number): number {
            return from + Math.round(Math.random() * (to - from));
        }
    }
}
