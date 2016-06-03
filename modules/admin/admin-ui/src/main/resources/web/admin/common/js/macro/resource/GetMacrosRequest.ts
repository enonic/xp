module api.macro.resource {

    export class GetMacrosRequest extends MacroResourceRequest<MacrosJson, MacroDescriptor[]> {

        appKey: api.application.ApplicationKey;

        constructor(appKey: api.application.ApplicationKey) {
            super();
            this.appKey = appKey;
        }

        getParams(): Object {
            return {
                appKey: this.appKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getByApp");
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