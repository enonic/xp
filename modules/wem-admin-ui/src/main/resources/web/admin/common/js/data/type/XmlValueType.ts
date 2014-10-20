module api.data.type {

    export class XmlValueType extends ValueType {

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