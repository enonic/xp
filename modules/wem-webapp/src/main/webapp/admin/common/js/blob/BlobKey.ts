module api_blob{

    export class BlobKey {

        private value:string;

        constructor(value:string) {
            this.value = value;
        }

        toString():string {
            return this.value;
        }
    }
}
