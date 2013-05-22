module API_schema_content_form{

    export class InputType {

        private name:string;

        constructor(json:any) {
            this.name = json.name;
        }

        getName():string {
            return this.name;
        }
    }
}
