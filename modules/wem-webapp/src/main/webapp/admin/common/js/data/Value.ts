module api.data {

    export class Value {

        private value: Object;

        private type: ValueType;

        constructor(value: Object, type: ValueType) {
            api.util.assertNotNull(value, "value of a Value cannot be null");
            api.util.assertNotNull(type, "type of a Value cannot be null");
            this.value = value;
            this.type = type;
        }

        asObject(): Object {
            return this.value;
        }

        asString(): string {

            return this.type.valueToString(this);
        }

        isRootDataSet(): boolean {
            return ValueTypes.DATA.toString() == this.type.toString();
        }

        asRootDataSet(): RootDataSet {
            api.util.assert(this.value instanceof RootDataSet, "Expected value to be a RootDataSet: " + api.util.getClassName(this.value));
            return <RootDataSet>this.value;
        }

        getType(): ValueType {
            return this.type;
        }

        setValue(value: Object) {
            this.value = value;
        }

        equals(value: Value): boolean {
            return this.value == value.value && this.type.toString() == value.type.toString();
        }
    }
}