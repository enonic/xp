module api.form {

    export class FieldSet extends Layout {

        private label: string;

        private formItems: FormItem[] = [];

        constructor(fieldSetJson: api.form.json.FieldSetJson) {
            super(fieldSetJson.name);
            this.label = fieldSetJson.label;

            if (fieldSetJson.items != null) {
                fieldSetJson.items.forEach((formItemJson: api.form.json.FormItemJson) => {
                    this.addFormItem(FormItemFactory.createFormItem(formItemJson));
                });
            }
        }

        addFormItem(formItem: FormItem) {
            this.formItems.push(formItem);
        }

        getLabel(): string {
            return this.label;
        }

        getFormItems(): FormItem[] {
            return this.formItems;
        }

        public toFieldSetJson(): api.form.json.FormItemTypeWrapperJson {

            return <api.form.json.FormItemTypeWrapperJson>{ FieldSet: <api.form.json.FieldSetJson>{
                name: this.getName(),
                items: FormItem.formItemsToJson(this.getFormItems()),
                label: this.getLabel()
            }};
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, FieldSet)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <FieldSet>o;

            if (!api.ObjectHelper.stringEquals(this.label, other.label)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.formItems, other.formItems)) {
                return false;
            }

            return true;
        }
    }
}