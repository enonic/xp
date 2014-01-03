module api.form{

    export class Layout extends FormItem {

        constructor(name:string) {
            super(name);
        }

        public toLayoutJson():api.form.json.FormItemTypeWrapperJson {

            if (this instanceof FieldSet) {
                return (<FieldSet>this).toFieldSetJson();
            }
            else {
                throw new Error("Unsupported Layout: " + this);
            }
        }
    }
}