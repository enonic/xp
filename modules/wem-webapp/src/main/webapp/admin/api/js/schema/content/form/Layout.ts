module api_schema_content_form{

    export class Layout extends FormItem {

        constructor(name:string) {
            super(name);
        }

        public toLayoutJson():api_schema_content_form_json.LayoutJson {

            if (this instanceof FieldSet) {
                return (<FieldSet>this).toFieldSetJson();
            }
            else {
                throw new Error("Unsupported Layout: " + this);
            }
        }
    }
}