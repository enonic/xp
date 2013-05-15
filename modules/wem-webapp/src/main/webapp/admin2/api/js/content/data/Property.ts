module API.content.data{

    export class Property extends Data {

        private value:string;

        private type:string;

        constructor(json) {
            super(json.name);
            this.value = json.value;
            this.type = json.type;
        }

        getValue():string {
            return this.value;
        }

        getType():string {
            return this.type;
        }
    }
}