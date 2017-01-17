module api.macro.resource {

    export class GetPreviewRequest extends PreviewRequest<MacroPreviewJson, MacroPreview> {

        protected path: api.content.ContentPath;

        constructor(data: api.data.PropertyTree, macroKey: api.macro.MacroKey, path: api.content.ContentPath) {
            super(data, macroKey);
            this.path = path;
        }

        getParams(): Object {
            return {
                form: this.data.toJson(),
                contentPath: !!this.path ? this.path.toString() : '',
                macroKey: this.macroKey.getRefString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'preview');
        }

        sendAndParse(): wemQ.Promise<MacroPreview> {
            return this.send().then((response: api.rest.JsonResponse<MacroPreviewJson>) => {
                return MacroPreview.create().fromJson(response.getResult()).build();
            });
        }
    }
}
