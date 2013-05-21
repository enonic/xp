module API_content_data{

    export class Property extends Data {

        private value:string;

        private type:string;

        static from( json ){
            return new Property( json.name, json.value, json.type );
        }

        constructor(name:string, value:string, type:string) {
            super(name);
            this.value = value;
            this.type = type;
        }

        getValue():string {
            return this.value;
        }

        getType():string {
            return this.type;
        }
    }
}