module api.macro {

    export class MacrosLoader extends api.util.loader.BaseLoader<MacrosJson, MacroDescriptor> {

        private getMacrosRequest: GetMacrosRequest;

        constructor() {
            this.getMacrosRequest = new GetMacrosRequest();
            super(this.getMacrosRequest);
        }

        load(): wemQ.Promise<MacroDescriptor[]> {

            this.notifyLoadingData();

            return this.sendRequest()
                .then((macros: MacroDescriptor[]) => {

                    this.notifyLoadedData(macros);
                    return macros;
                });
        }

    }


}