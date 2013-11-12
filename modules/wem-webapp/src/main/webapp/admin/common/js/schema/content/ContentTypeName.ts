module api_schema_content{

    export class ContentTypeName {

        private value:string;

        constructor(name:string) {
            this.value = name
        }

        toString():string {
            return this.value;
        }
    }
}