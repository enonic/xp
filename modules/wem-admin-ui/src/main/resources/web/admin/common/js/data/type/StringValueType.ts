module api.data.type {

    export class StringValueType extends ValueType {

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