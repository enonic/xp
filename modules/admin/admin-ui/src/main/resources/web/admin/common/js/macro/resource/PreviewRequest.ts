module api.macro.resource {

    export class PreviewRequest<JSON_TYPE, PARSED_TYPE> extends MacroResourceRequest<JSON_TYPE, PARSED_TYPE> {

        protected data: api.data.PropertyTree;

        protected macroKey: api.macro.MacroKey;

        constructor(data: api.data.PropertyTree, macroKey: api.macro.MacroKey) {
            super();
            super.setMethod('POST');
            this.data = data;
            this.macroKey = macroKey;
        }

        getParams(): Object {
            return {
                form: this.data.toJson(),
                macroKey: this.macroKey.getRefString()
            };
        }
    }
}
