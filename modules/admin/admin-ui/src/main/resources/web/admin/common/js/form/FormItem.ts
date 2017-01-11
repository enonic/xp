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
                  api.ObjectHelper.iFrameSafeInstanceOf(parent, FormOptionSet) ||
                  api.ObjectHelper.iFrameSafeInstanceOf(parent, FormOptionSetOption))) {
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

            let other = <FormItem>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            return true;
        }

        public toFormItemJson(): api.form.json.FormItemTypeWrapperJson {

            if (api.ObjectHelper.iFrameSafeInstanceOf(this, Input)) {
                return (<Input><any>this).toInputJson();
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(this, FormItemSet)) {
                return (<api.form.FormItemSet><any>this).toFormItemSetJson();
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(this, FieldSet)) {
                return (<FieldSet><any>this).toFieldSetJson();
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(this, FormOptionSet)) {
                return (<api.form.FormOptionSet><any>this).toFormOptionSetJson();
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(this, FormOptionSetOption)) {
                return (<api.form.FormOptionSetOption><any>this).toFormOptionSetOptionJson();
            }
            else {
                throw new Error("Unsupported FormItem: " + this);
            }
        }

        public static formItemsToJson(formItems: FormItem[]): api.form.json.FormItemTypeWrapperJson[] {

            let formItemArray: api.form.json.FormItemTypeWrapperJson[] = [];
            formItems.forEach((formItem: FormItem) => {
                formItemArray.push(formItem.toFormItemJson());
            });
            return formItemArray;
        }
    }
}
