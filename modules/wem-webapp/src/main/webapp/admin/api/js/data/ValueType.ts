module api_data{

    export class ValueType {

        private name:string;

        constructor(name:string){
            this.name = name;
        }

        toString() {
            return this.name;
        }
    }
}