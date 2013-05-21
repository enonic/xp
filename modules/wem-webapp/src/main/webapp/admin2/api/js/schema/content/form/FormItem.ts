module API_schema_content_form{

    export class FormItem {

        private name:string;

        constructor(name:string) {
            this.name = name;
        }

        getName():string {
            return this.name;
        }
    }
}