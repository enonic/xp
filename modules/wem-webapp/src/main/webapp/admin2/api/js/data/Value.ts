module api_data{

    export class Value {

        private value:string;

        private type:ValueType;

        constructor(value:string, type:ValueType) {
            this.value = value;
            this.type = type;
        }

        asString():string {
            return this.value;
        }

        getType():ValueType {
            return this.type;
        }

        setValue(value:string) {
            this.value = value;
        }
    }
}