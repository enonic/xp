module api.macro.resource {

    export class GetPreviewStringRequest extends PreviewRequest<MacroPreviewStringJson, string> {

        constructor(data: api.data.PropertyTree, macroKey: api.macro.MacroKey) {
            super(data, macroKey);
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "previewString");
        }

        sendAndParse(): wemQ.Promise<string> {
            return this.send().then((response: api.rest.JsonResponse<MacroPreviewStringJson>) => {
                return response.getResult().macro;
            });
        }
    }
}
