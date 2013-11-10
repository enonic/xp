module api_content{

    export class AttachmentName {

        private dataPath:api_data.DataPath;

        private fileName:string;

        constructor(dataPath:api_data.DataPath, fileName:string)
        {
            this.dataPath = dataPath;
            this.fileName = fileName;
        }

        getDataPath():api_data.DataPath {
            return this.dataPath;
        }

        getFileName():string {
            return this.fileName;
        }
    }
}
