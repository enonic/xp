module api.macro.resource {

    export class GetPreviewRequest extends MacroResourceRequest<MacroPreviewJson, MacroPreview> {

        private data: api.data.PropertyTree;

        private path: api.content.ContentPath;

        private macroKey: api.macro.MacroKey;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setData(data: api.data.PropertyTree): GetPreviewRequest {
            this.data = data;
            return this;
        }

        setPath(path: api.content.ContentPath): GetPreviewRequest {
            this.path = path;
            return this;
        }

        setMacroKey(macroKey: api.macro.MacroKey): GetPreviewRequest {
            this.macroKey = macroKey;
            return this;
        }

        getParams(): Object {
            return {
                form: this.data.toJson(),
                contentPath: !!this.path ? this.path.toString() : "",
                macroKey: this.macroKey.getRefString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "preview");
        }

        sendAndParse(): wemQ.Promise<MacroPreview> {
            return this.send().then((response: api.rest.JsonResponse<MacroPreviewJson>) => {
                return MacroPreview.create().fromJson(response.getResult()).build();
            });
        }
    }
}