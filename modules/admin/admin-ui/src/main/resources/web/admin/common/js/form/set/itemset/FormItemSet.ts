module api.form {

    /**
     * A set of [[FormItem]]s.
     *
     * The form items are kept in the order they where inserted.
     */
    export class FormItemSet extends FormSet implements FormItemContainer {

        private formItems: FormItem[] = [];

        private formItemByName: {[name: string]: FormItem; } = {};

        private immutable: boolean;

        private customText: string;

        constructor(formItemSetJson: api.form.json.FormItemSetJson) {
            super(formItemSetJson);
            this.immutable = formItemSetJson.immutable;
            this.customText = formItemSetJson.customText;

            if (formItemSetJson.items != null) {
                formItemSetJson.items.forEach((formItemJson: api.form.json.FormItemJson) => {
                    let formItem: FormItem = FormItemFactory.createFormItem(formItemJson);
                    if (formItem) {
                        this.addFormItem(formItem);
                    }
                });
            }
        }

        addFormItem(formItem: FormItem): void {
            const name: string = formItem.getName();
            if (this.formItemByName[name]) {
                throw new Error('FormItem already added: ' + name);
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

        isImmutable(): boolean {
            return this.immutable;
        }

        getCustomText(): string {
            return this.customText;
        }

        public toFormItemSetJson(): api.form.json.FormItemTypeWrapperJson {

            return <api.form.json.FormItemTypeWrapperJson>{FormItemSet: <api.form.json.FormItemSetJson>{
                name: this.getName(),
                customText: this.getCustomText(),
                helpText: this.getHelpText(),
                immutable: this.isImmutable(),
                items: FormItem.formItemsToJson(this.getFormItems()),
                label: this.getLabel(),
                occurrences: this.getOccurrences().toJson()
            }};
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, FormItemSet)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            let other: FormItemSet = <FormItemSet>o;

            if (!api.ObjectHelper.booleanEquals(this.immutable, other.immutable)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.customText, other.customText)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.formItems, other.formItems)) {
                return false;
            }

            return true;
        }

    }
}
