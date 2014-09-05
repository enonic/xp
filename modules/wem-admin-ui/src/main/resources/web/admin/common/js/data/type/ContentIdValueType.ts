module api.data.type {

    export class ContentIdValueType extends ValueType {

        constructor(name: string) {
            super(name);
        }

        newValue(value: string): Value {
            return new Value(new api.content.ContentId(value), this);
        }

        valueToString(value: Value): string {
            return (<api.content.ContentId>value.getObject()).toString();
        }
    }
}