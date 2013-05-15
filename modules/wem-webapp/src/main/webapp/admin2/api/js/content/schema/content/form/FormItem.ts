module API.content.schema.content.form{

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