module api_content{

    export class AttachmentName {

        private fileName:string;

        constructor(fileName:string) {
            this.fileName = fileName;
        }

        toString():string {
            return this.fileName;
        }
    }
}
