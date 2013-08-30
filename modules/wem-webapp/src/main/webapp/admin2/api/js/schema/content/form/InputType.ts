module api_schema_content_form{

    export class InputType {

        private name:string;

        private builtIn:boolean;

        constructor(json:any) {
            this.name = json.name;
            this.builtIn = json.builtIn;
        }

        getName():string {
            return this.name;
        }

        isBuiltIn():boolean {
            return this.builtIn;
        }
    }
}
