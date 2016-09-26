module api.form {

    export class FormItem implements api.Equitable {

        private name: string;

        private parent: FormItem;

        constructor(name: string) {
            this.name = name;
        }

        setParent(parent: FormItem) {
            if (!(api.ObjectHelper.iFrameSafeInstanceOf(parent, FormItemSet) ||
                  api.ObjectHelper.iFrameSafeInstanceOf(parent, FieldSet) ||
                  api.ObjectHelper.iFrameSafeInstanceOf(parent, api.form.optionset.FormOptionSet) ||
                  api.ObjectHelper.iFrameSafeInstanceOf(parent, api.form.optionset.FormOptionSetOption))) {
                throw new Error("A parent FormItem must either be a FormItemSet, FieldSet or a FormOptionSet");
            }

            this.parent = parent;
        }

        getName(): string {
            return this.name;
        }

        getPath(): FormItemPath {
            return this.resolvePath();
        }

        getParent(): FormItem {
            return this.parent;
        }

        private resolvePath(): FormItemPath {
            return FormItemPath.fromParent(this.resolveParentPath(), FormItemPathElement.fromString(this.name));
        }

        private resolveParentPath(): FormItemPath {

            if (this.parent == null) {
                return FormItemPath.ROOT;
            }
            else {
                return this.parent.getPath();
            }
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, FormItem)) {
                return false;
            }

            var other = <FormItem>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            return true;
        }

        public toFormItemJson(): api.form.json.FormItemTypeWrapperJson {

            if (api.ObjectHelper.iFrameSafeInstanceOf(this, Input)) {
                return (<Input>this).toInputJson();
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(this, FormItemSet)) {
                return (<FormItemSet>this).toFormItemSetJson();
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(this, Layout)) {
                return (<Layout>this).toLayoutJson();
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(this, api.form.optionset.FormOptionSet)) {
                return (<api.form.optionset.FormOptionSet>this).toFormOptionSetJson();
            }
            else {
                throw new Error("Unsupported FormItem: " + this);
            }
        }

        public static formItemsToJson(formItems: FormItem[]): api.form.json.FormItemTypeWrapperJson[] {

            var formItemArray: api.form.json.FormItemTypeWrapperJson[] = [];
            formItems.forEach((formItem: FormItem) => {
                formItemArray.push(formItem.toFormItemJson());
            });
            return formItemArray;
        }
    }
}