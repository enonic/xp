module api.content{

    export class ContentId {

        private value:string;

        constructor(value:string)
        {
            if (!ContentId.isValidContentId(value)) {
                throw new Error("Invalid content id")
            }
            this.value = value;
        }

        toString():string {
            return this.value;
        }

        static isValidContentId(id:string):boolean {
            return !api.util.isStringEmpty(id) && !api.util.isStringBlank(id);
        }
    }
}
