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

        newValue(value: string): Value {
            if (!value) {
                return this.newNullValue();
            }
            return new Value(value, this);
        }

        toJsonValue(value: Value): any {
            return value.getString() ? value.getObject() : null;
        }
    }
}