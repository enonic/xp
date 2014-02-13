module api.schema.content{

    export class ContentTypeName {

        private value:string;

        constructor(name:string) {
            this.value = name
        }

        toString():string {
            return this.value;
        }

        equals(other: ContentTypeName):boolean {
            return this.value == other.toString();
        }
    }
}