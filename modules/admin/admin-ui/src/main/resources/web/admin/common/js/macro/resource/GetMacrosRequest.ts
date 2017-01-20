module api.macro.resource {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetMacrosRequest extends MacroResourceRequest<MacrosJson, MacroDescriptor[]> {

        private applicationKeys: ApplicationKey[];

        constructor() {
            super();
            super.setMethod('POST');
        }

        setApplicationKeys(applicationKeys: ApplicationKey[]) {
            this.applicationKeys = applicationKeys;
        }

        getParams(): Object {
            return {
                appKeys: ApplicationKey.toStringArray(this.applicationKeys)
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'getByApps');
        }

        sendAndParse(): wemQ.Promise<MacroDescriptor[]> {
            return this.send().then((response: api.rest.JsonResponse<MacrosJson>) => {
                return this.toMacroDescriptors(response.getResult());
            });
        }

        toMacroDescriptors(macrosJson: MacrosJson): MacroDescriptor[] {
            let result: MacroDescriptor[] = [];
            for (let i = 0; i < macrosJson.macros.length; i++) {
                result.push(MacroDescriptor.create().fromJson(macrosJson.macros[i]).build());
            }
            return result;
        }
    }
}
