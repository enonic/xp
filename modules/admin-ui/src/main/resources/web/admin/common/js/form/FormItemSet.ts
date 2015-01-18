module api.form {

    export class FormItemSet extends FormItem {

        private label: string;

        private formItems: FormItem[] = [];

        private formItemByName: {[name:string] : FormItem; } = {};

        private immutable: boolean;

        private occurrences: Occurrences;

        private customText: string;

        private helpText: string;

        constructor(formItemSetJson: api.form.json.FormItemSetJson) {
            super(formItemSetJson.name);
            this.label = formItemSetJson.label;
            this.immutable = formItemSetJson.immutable;
            this.occurrences = Occurrences.fromJson(formItemSetJson.occurrences);
            this.customText = formItemSetJson.customText;
            this.helpText = formItemSetJson.helpText;

            if (formItemSetJson.items != null) {
                formItemSetJson.items.forEach((formItemJson: api.form.json.FormItemJson) => {
                    this.addFormItem(FormItemFactory.createFormItem(formItemJson));
                });
            }
        }

        addFormItem(formItem: FormItem) {
            if (this.formItemByName[name]) {
                throw new Error("FormItem already added: " + name);
            }
            formItem.setParent(this);
            this.formItemByName[formItem.getName()] = formItem;
            this.formItems.push(formItem);
        }

        getFormItems(): FormItem[] {
            return this.formItems;
        }

        getFormItemByName(name: string): FormItem {
            return this.formItemByName[name];
        }

        getInputByName(name: string): Input {
            return <Input>this.formItemByName[name];
        }

        getLabel(): string {
            return this.label;
        }

        isImmutable(): boolean {
            return this.immutable;
        }

        getCustomText(): string {
            return this.customText;
        }

        getHelpText(): string {
            return this.helpText;
        }

        getOccurrences(): Occurrences {
            return this.occurrences;
        }

        public toFormItemSetJson(): api.form.json.FormItemTypeWrapperJson {

            return <api.form.json.FormItemTypeWrapperJson>{FormItemSet: <api.form.json.FormItemSetJson>{
                name: this.getName(),
                customText: this.getCustomText(),
                helpText: this.getHelpText(),
                immutable: this.isImmutable(),
                items: FormItem.formItemsToJson(this.getFormItems()),
                label: this.getLabel(),
                occurrences: this.getOccurrences().toJson(),
            }};
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, FormItemSet)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <FormItemSet>o;

            if (!api.ObjectHelper.stringEquals(this.label, other.label)) {
                return false;
            }

            if (!api.ObjectHelper.booleanEquals(this.immutable, other.immutable)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.occurrences, other.occurrences)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.customText, other.customText)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.helpText, other.helpText)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.formItems, other.formItems)) {
                return false;
            }

            return true;
        }

    }
}