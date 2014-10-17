module api.form {

    export class Layout extends FormItem {

        constructor(name: string) {
            super(name);
        }

        public toLayoutJson(): api.form.json.FormItemTypeWrapperJson {

            if (api.ObjectHelper.iFrameSafeInstanceOf(this, FieldSet)) {
                return (<FieldSet>this).toFieldSetJson();
            }
            else {
                throw new Error("Unsupported Layout: " + this);
            }
        }

        equals(o: api.Equitable): boolean {

            if (!(api.ObjectHelper.iFrameSafeInstanceOf(o, Layout))) {
                return false;
            }

            return super.equals(o);
        }
    }
}