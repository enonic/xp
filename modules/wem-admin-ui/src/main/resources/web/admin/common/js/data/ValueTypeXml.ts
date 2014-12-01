module api.data {

    export class ValueTypeXml extends ValueType {

        constructor() {
            super("Xml");
        }

        isValid(value: any): boolean {
            return typeof value === 'string';
        }

        valueEquals(a: string, b: string): boolean {
            return api.ObjectHelper.stringEquals(a, b);
        }
    }
}