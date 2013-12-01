module api_schema_mixin{

    export class MixinName {

        private value:string;

        constructor(name:string) {
            this.value = name
        }

        toString():string {
            return this.value;
        }
    }
}