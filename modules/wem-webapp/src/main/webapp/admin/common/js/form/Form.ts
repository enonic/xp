module api.form {

    export class Form implements api.Equitable {

        private formItems: FormItem[] = [];

        private formItemByName: {[name:string] : FormItem; } = {};

        constructor(formJson: api.form.json.FormJson) {

            //TODO: this breaks "edit", cause formJson is empty. Commented out.
            formJson.formItems.forEach((formItemJson: api.form.json.FormItemJson) => {
                this.addFormItem(FormItemFactory.createFormItem(formItemJson));
            });
        }

        addFormItem(formItem: FormItem) {
            if (this.formItemByName[name]) {
                throw new Error("FormItem already added: " + name);
            }
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

        toJson(): api.form.json.FormJson {

            return <api.form.json.FormJson>{
                formItems: FormItem.formItemsToJson(this.getFormItems())
            }
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof Form)) {
                return false;
            }

            var other = <Form>o;

            if (this.formItems.length != other.formItems.length) {
                return false;
            }

            for (var i = 0; i < this.formItems.length; i++) {
                if (!this.formItems[i].equals(other.formItems[i])) {
                    return false;
                }
            }

            return true;
        }
    }
}