module api.form{

    export class FormItem {

        private name:string;

        private parent:FormItem;

        constructor(name:string) {
            this.name = name;
        }

        setParent(parent:FormItem) {
            if( !(parent instanceof FormItemSet || parent instanceof FieldSet) ) {
                throw new Error("A parent FormItem must either be a FormItemSet or a FieldSet");
            }

            this.parent = parent;
        }

        getName():string {
            return this.name;
        }

        getPath():FormItemPath {
            return this.resolvePath();
        }

        private resolvePath():FormItemPath{
            return FormItemPath.fromParent( this.resolveParentPath(), FormItemPathElement.fromString(this.name) );
        }

        private resolveParentPath():FormItemPath {

            if ( this.parent == null )
            {
                return FormItemPath.ROOT;
            }
            else
            {
                return this.parent.getPath();
            }
        }

        public toFormItemJson():api.form.json.FormItemTypeWrapperJson {

            if (this instanceof Input) {
                return (<Input>this).toInputJson();
            }
            else if (this instanceof FormItemSet) {
                return (<FormItemSet>this).toFormItemSetJson();
            }
            else if (this instanceof Layout) {
                return (<Layout>this).toLayoutJson();
            }
            else {
                throw new Error("Unsupported FormItem: " + this);
            }
        }

        public static formItemsToJson(formItems:FormItem[]):api.form.json.FormItemTypeWrapperJson[] {

            var formItemArray:api.form.json.FormItemTypeWrapperJson[] = [];
            formItems.forEach((formItem:FormItem) => {
                formItemArray.push(formItem.toFormItemJson());
            });
            return formItemArray;
        }
    }
}