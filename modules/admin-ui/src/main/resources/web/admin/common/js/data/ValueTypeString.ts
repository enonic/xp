module api.data {

    export class ValueTypeString extends ValueType {

        constructor() {
            super("String");
        }

        isValid(value: any): boolean {
            return typeof value === 'string';
        }

        valueEquals(a: string, b: string): boolean {
            return api.ObjectHelper.stringEquals(a, b);
        }
    }
}