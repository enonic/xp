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

        hasNonNullValue(): boolean {
            return !this.value.isNull();
        }

        hasNullValue(): boolean {
            return this.value.isNull();
        }

        getString(): string {
            return this.value.asString();
        }

        getBoolean(): boolean {
            return this.value.asBoolean();
        }

        getDate(): Date {
            return this.value.getDate();
        }

        getData(): RootDataSet {
            return this.value.getData();
        }

        getGeoPoint(): api.util.GeoPoint {
            return this.value.getGeoPoint();
        }

        getLocalTime(): api.util.LocalTime {
            return this.value.getLocalTime();
        }

        getContentId(): api.content.ContentId {
            return this.value.getContentId();
        }

        getRootDataSet(): api.data.RootDataSet {
            return this.value.getRootDataSet();
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

            if (this.value.isRootDataSet()) {
                return <api.data.json.DataTypeWrapperJson>{ Property: {
                    name: this.getName(),
                    type: this.getType().toString(),
                    set: this.value.isNotNull() ? this.getType().toJsonValue(this.value) : null
                }};
            }
            else {
                return <api.data.json.DataTypeWrapperJson>{ Property: {
                    name: this.getName(),
                    type: this.getType().toString(),
                    value: this.value.isNotNull() ? this.getType().toJsonValue(this.value) : null
                }};
            }
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
                value = valueType.fromJsonValue(json.value);
            }

            return new Property(json.name, value);
        }

        static fromNameValue(name: string, value: Value) {
            return new Property(name, value);
        }
    }
}