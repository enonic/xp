module api.form {

    import FormOptionSetOptionJson = api.form.json.FormOptionSetOptionJson;

    export class FormOptionSetOption extends FormItem implements FormItemContainer, api.Equitable {

        private label: string;

        private defaultOption: boolean;

        private formItems: FormItem[] = [];

        private helpText: string;

        private helpTextIsOn: boolean = false;

        private formItemByName: {[name: string]: FormItem; } = {};

        constructor(optionJson: FormOptionSetOptionJson) {
            super(optionJson.name);
            this.label = optionJson.label;
            this.defaultOption = optionJson.defaultOption;
            this.helpText = optionJson.helpText;
            if (optionJson.items != null) {
                optionJson.items.forEach((formItemJson: api.form.json.FormItemJson) => {
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
                throw new Error(`FormItem already added: ${name}`);
            }
            formItem.setParent(this);
            this.formItemByName[formItem.getName()] = formItem;
            this.formItems.push(formItem);
        }

        getFormItems(): api.form.FormItem[] {
            return this.formItems;
        }

        public static fromJson(optionJson: FormOptionSetOptionJson): FormOptionSetOption {
            return new FormOptionSetOption(optionJson);
        }

        getLabel(): string {
            return this.label;
        }

        isDefaultOption(): boolean {
            return this.defaultOption;
        }

        getHelpText(): string {
            return this.helpText;
        }

        isHelpTextOn(): boolean {
            return this.helpTextIsOn;
        }

        public toFormOptionSetOptionJson(): api.form.json.FormOptionSetOptionJson {

            return {
                name: this.getName(),
                label: this.getLabel(),
                helpText: this.getHelpText(),
                defaultOption: this.isDefaultOption(),
                items: FormItem.formItemsToJson(this.getFormItems())
            };
        }

        public static optionsToJson(options: FormOptionSetOption[]): api.form.json.FormOptionSetOptionJson[] {
            let jsonArray: api.form.json.FormOptionSetOptionJson[] = [];
            options.forEach((option: FormOptionSetOption) => {
                jsonArray.push(option.toFormOptionSetOptionJson());
            });
            return jsonArray;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, FormOptionSetOption)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            const other: FormOptionSetOption = <FormOptionSetOption>o;

            if (!api.ObjectHelper.stringEquals(this.label, other.label)) {
                return false;
            }

            if (!api.ObjectHelper.booleanEquals(this.defaultOption, other.defaultOption)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.formItems, other.formItems)) {
                return false;
            }

            return true;
        }
    }
}
