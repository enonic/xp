module api_schema_relationshiptype{

    export class RelationshipTypeName {

        private value:string;

        constructor(name:string) {
            this.value = name
        }

        toString():string {
            return this.value;
        }
    }
}