module api.data {

    export class ContentIdValueType extends ValueType {

        constructor(name: string) {
            super(name);
        }

        valueToString(value: Value): string {
            return (<api.content.ContentId>value.asObject()).toString();
        }
    }
}