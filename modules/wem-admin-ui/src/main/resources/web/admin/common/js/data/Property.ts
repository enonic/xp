module api.data {

    import ValueType = api.data.type.ValueType;
    import ValueTypes = api.data.type.ValueTypes;

    export class Property extends Data implements api.Equitable {

        private value: Value;

        constructor(name: string, value: Value) {
            api.util.assertNotNull(value, "value of a Property cannot be null");
            super(name);
            this.value = value;
        }

        getString(): string {
            return this.value.asString();
        }

        getBoolean(): boolean {
            return this.value.asBoolean();
        }

        setValue(value: Value) {
            api.util.assertNotNull(value, "value of a Property cannot be null");
            this.value = value;
        }

        getValue(): Value {
            return this.value;
        }

        getType(): ValueType {
            return this.value.getType();
        }

        toPropertyJson(): api.data.json.DataTypeWrapperJson {

            return <api.data.json.DataTypeWrapperJson>{ Property: {
                name: this.getName(),
                type: this.getType().toString(),
                value: this.getValue().getObject()
            }};
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Property)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <Property>o;

            if (!api.ObjectHelper.equals(this.value, other.value)) {
                return false;
            }

            return true;
        }

        clone(): Property {

            var clone = new Property(this.getName(), this.value.clone());
            clone.setArrayIndex(this.getArrayIndex());
            clone.setParent(this.getParent());
            return  clone;
        }

        prettyPrint(indent?: string) {
            var thisIndent = indent ? indent : "";
            var idAsString = this.getId().toString();
            var valueAsString = this.getValue().asString();
            console.log(thisIndent + idAsString + ": " + valueAsString);
        }

        static fromJson(json: api.data.json.PropertyJson) {

            var valueType: ValueType = ValueTypes.fromName(json.type);
            var value;
            if (valueType == ValueTypes.DATA) {
                var rootDataSet = DataFactory.createRootDataSet(<api.data.json.DataJson[]>json.set);
                value = new Value(rootDataSet, valueType);
            }
            else {
                value = valueType.newValue(json.value);
            }

            return new Property(json.name, value);
        }

        static fromNameValue(name: string, value: Value) {
            return new Property(name, value);
        }
    }
}