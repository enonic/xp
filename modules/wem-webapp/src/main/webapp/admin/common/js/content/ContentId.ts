module api.content{

    export class ContentId {

        private value:string;

        constructor(value:string)
        {
            this.value = value;
        }

        toString():string {
            return this.value;
        }
    }
}
