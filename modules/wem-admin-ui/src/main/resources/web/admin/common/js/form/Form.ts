module api.form {

    export class FormBuilder {

        formItems: FormItem[] = [];

        addFormItem(formItem: FormItem): FormBuilder {
            this.formItems.push(formItem);
            return this;
        }

        fromJson(json: json.FormJson): FormBuilder {
            json.formItems.forEach((formItemJson: api.form.json.FormItemJson) => {
                this.addFormItem(FormItemFactory.createFormItem(formItemJson));
            });
            return this;
        }

        build(): Form {
            return new Form(this);
        }
    }

    export class Form implements api.Equitable {

        private formItems: FormItem[] = [];

        private formItemByName: {[name:string] : FormItem; } = {};

        static fromJson(json: json.FormJson) {
            var builder = new FormBuilder();
            builder.fromJson(json);
            return builder.build();
        }

        constructor(builder: FormBuilder) {
            builder.formItems.forEach((formItem: FormItem) => {
                this.addFormItem(formItem);
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

            if (!(api.ObjectHelper.iFrameSafeInstanceOf(o, Form))) {
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