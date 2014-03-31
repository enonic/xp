module api.data {

    export class ValueType {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        toString() {
            return this.name;
        }

        equals(valueType: ValueType): boolean {
            return this.name == valueType.name;
        }

        valueToString(value: Value): string {
            return <string>value.asObject();
        }
    }
}