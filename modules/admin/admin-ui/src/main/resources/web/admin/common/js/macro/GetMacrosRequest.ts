module api.macro {

    export class GetMacrosRequest extends MacroResourceRequest<MacrosJson, MacroDescriptor[]> {

        constructor() {
            super();
        }

        getParams(): Object {
            return {}
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): wemQ.Promise<MacroDescriptor[]> {
            return this.send().then((response: api.rest.JsonResponse<MacrosJson>) => {
                return this.toMacroDescriptors(response.getResult());
            });
        }

        toMacroDescriptors(macrosJson: MacrosJson): MacroDescriptor[] {
            var result: MacroDescriptor[] = [];
            for (var i = 0; i < macrosJson.macros.length; i++) {
                result.push(MacroDescriptor.create().fromJson(macrosJson.macros[i]).build());
            }
            return result;
        }
    }
}