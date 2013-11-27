module api_data{

    export class Property extends Data {

        private value:Value;

        static fromJson(json) {
            var value = new Value(json.value, ValueTypes.fromName(json.type));
            return new Property(json.name, value);
        }

        static fromStrings(name:string, valueAsString:string, type:string) {
            return new Property(name, new Value(valueAsString, ValueTypes.fromName(type)));
        }

        static fromNameValue(name:string, value:Value) {
            return new Property(name, value);
        }

        constructor(name:string, value:Value) {
            super(name);
            this.value = value;
        }

        getString():string {
            return this.value.asString();
        }

        getValue():Value {
            return this.value;
        }

        getType():ValueType {
            return this.value.getType();
        }

        toPropertyJson():api_data_json.DataTypeWrapperJson {

            return <api_data_json.DataTypeWrapperJson>{ Property:{
                name: this.getName(),
                type: this.getType().toString(),
                value: this.getString()
            }};
        }

        equals(property:Property):boolean {
            return super.equals(property) && this.value.equals(property.getValue());
        }
    }
}