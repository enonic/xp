module api_form{

    export class FormItemSet extends FormItem {

        private label:string;

        private formItems:FormItem[] = [];

        private formItemByName:{[name:string] : FormItem; } = {};

        private immutable:boolean;

        private occurrences:Occurrences;

        private customText:string;

        private helpText:string;

        constructor(formItemSetJson:api_form_json.FormItemSetJson) {
            super(formItemSetJson.name);
            this.label = formItemSetJson.label;
            this.immutable = formItemSetJson.immutable;
            this.occurrences = new Occurrences(formItemSetJson.occurrences);
            this.customText = formItemSetJson.customText;
            this.helpText = formItemSetJson.helpText;

            if (formItemSetJson.items != null) {
                formItemSetJson.items.forEach((formItemJson:api_form_json.FormItemJson) => {
                    this.addFormItem( FormItemFactory.createFormItem(formItemJson) );
                });
            }
        }

        addFormItem(formItem:FormItem) {
            if (this.formItemByName[name]) {
                throw new Error("FormItem already added: " + name);
            }
            this.formItemByName[formItem.getName()] = formItem;
            this.formItems.push(formItem);
        }

        getFormItems():FormItem[] {
            return this.formItems;
        }

        getFormItemByName(name:string):FormItem {
            return this.formItemByName[name];
        }

        getInputByName(name:string):Input {
            return <Input>this.formItemByName[name];
        }

        getLabel():string {
            return this.label;
        }

        isImmutable():boolean {
            return this.immutable;
        }

        getCustomText():string {
            return this.customText;
        }

        getHelpText():string {
            return this.helpText;
        }

        getOccurrences():Occurrences {
            return this.occurrences;
        }

        public toFormItemSetJson():api_form_json.FormItemTypeWrapperJson {

            return <api_form_json.FormItemTypeWrapperJson>{FormItemSet: <api_form_json.FormItemSetJson>{
                name: this.getName(),
                customText : this.getCustomText(),
                helpText : this.getHelpText(),
                immutable : this.isImmutable(),
                items : FormItem.formItemsToJson(this.getFormItems()),
                label : this.getLabel(),
                occurrences : this.getOccurrences().toJson(),
            }};
        }
    }
}