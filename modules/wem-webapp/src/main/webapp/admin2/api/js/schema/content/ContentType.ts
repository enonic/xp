module api_schema_content{

    export class ContentType {

        private name:string;

        private module:string;

        private displayName:string;

        // TODO:.... more fields of course

        constructor(json:any) {
            this.name = json.name;
            this.module = json.module;
            this.displayName = json.displayName;
        }

        getName():string{
            return this.name;
        }

        getModule():string{
            return this.module;
        }

        getDisplayName():string{
            return this.displayName;
        }

    }
}