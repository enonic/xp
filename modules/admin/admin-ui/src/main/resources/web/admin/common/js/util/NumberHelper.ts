module api.util {

    export class NumberHelper {

        private static MAX_SAFE_INTEGER: number = 9007199254740991;

        private static MIN_SAFE_INTEGER: number = -9007199254740991;

        static isWholeNumber(value: any): boolean {
            return NumberHelper.isNumber(value) && (<number>value) % 1 === 0;
        }

        static isNumber(value: any): boolean {
            return typeof value === 'number' && !isNaN(value) && isFinite(value) && value >= NumberHelper.MIN_SAFE_INTEGER &&
                   value <= NumberHelper.MAX_SAFE_INTEGER;
        }

        static randomBetween(from: number, to: number): number {
            return from + Math.round(Math.random() * (to - from));
        }

        static toNumber(value: string): number {
            if(value != null && value != undefined && value.trim().length > 0) {
                const result = Number(value);

                return !isNaN(result) ? result : null;
            }

            return null;
        }
    }
}
