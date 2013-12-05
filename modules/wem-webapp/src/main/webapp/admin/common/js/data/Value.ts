module api_data {

    export class Value {

        private value: Object;

        private type: ValueType;

        constructor(value: Object, type: ValueType) {
            api_util.assertNotNull(value, "value of a Value cannot be null");
            api_util.assertNotNull(type, "type of a Value cannot be null");
            this.value = value;
            this.type = type;
        }

        asString(): string {
            return <string>this.value;
        }

        isRootDataSet(): boolean {
            return ValueTypes.DATA.toString() == this.type.toString();
        }

        asRootDataSet(): RootDataSet {
            api_util.assert(this.value instanceof RootDataSet, "Expected value to be a RootDataSet: " + api_util.getClassName(this.value));
            return <RootDataSet>this.value;
        }

        getType(): ValueType {
            return this.type;
        }

        setValue(value: Object) {
            this.value = value;
        }

        equals(value: Value): boolean {
            return this.value == value.value && this.type.toString() == value.getType().toString();
        }
    }
}