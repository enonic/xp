module api.data{

    export class Property extends Data {

        private value:Value;

        static fromJson(json) {

            var valueType:ValueType = ValueTypes.fromName( json.type );
            var value;
            if ( valueType == ValueTypes.DATA )
            {
                var rootDataSet = DataFactory.createRootDataSet( <api.data.json.DataJson[]>json.set );
                value = new Value( rootDataSet, valueType );
            }
            else
            {
                value = new Value( json.value, valueType );
            }

            return new Property(json.name, value);
        }

        static fromStrings(name:string, valueAsString:string, type:string) {
            return new Property(name, new Value(valueAsString, ValueTypes.fromName(type)));
        }

        static fromNameValue(name:string, value:Value) {
            return new Property(name, value);
        }

        constructor(name:string, value:Value) {
            api.util.assertNotNull( value, "value of a Property cannot be null" );
            super(name);
            this.value = value;
        }

        getString():string {
            return this.value.asString();
        }

        setValue(value:Value) {
            api.util.assertNotNull( value, "value of a Property cannot be null" );
            this.value = value;
        }

        getValue():Value {
            return this.value;
        }

        getType():ValueType {
            return this.value.getType();
        }

        toPropertyJson():api.data.json.DataTypeWrapperJson {

            return <api.data.json.DataTypeWrapperJson>{ Property:{
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