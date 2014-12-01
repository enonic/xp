module api.data2 {

    export class ValueTypeDateTime extends ValueType {

        constructor() {
            super("DateTime");
        }

        isValid(value: any): boolean {
            return typeof value === 'string';
        }

        valueEquals(a: string, b: string): boolean {
            return api.ObjectHelper.stringEquals(a, b);
        }

    }
}