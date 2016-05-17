module api.macro.resource {

    import ApplicationEvent = api.application.ApplicationEvent;
    import ApplicationEventType = api.application.ApplicationEventType;

    export class MacrosLoader extends api.util.loader.BaseLoader<MacrosJson, MacroDescriptor> {

        private getMacrosRequest: GetMacrosRequest;

        private hasRelevantData: boolean;

        constructor() {
            this.getMacrosRequest = new GetMacrosRequest();
            this.hasRelevantData = false;
            super(this.getMacrosRequest);

            ApplicationEvent.on((event: ApplicationEvent) => {
                if (event.getEventType() == ApplicationEventType.STARTED || event.getEventType() == ApplicationEventType.STOPPED ||
                    event.getEventType() == ApplicationEventType.UPDATED) {
                    this.invalidate();
                }
            });
        }

        private invalidate() {
            this.hasRelevantData = false;
        }

        load(): wemQ.Promise<MacroDescriptor[]> {

            this.notifyLoadingData();

            if (this.hasRelevantData) {
                this.notifyLoadedData(this.getResults());
                return wemQ(this.getResults());
            }

            return this.sendRequest()
                .then((macros: MacroDescriptor[]) => {
                    this.notifyLoadedData(macros);
                    this.hasRelevantData = true;
                    this.setResults(macros);
                    return macros;
                });
        }

        filterFn(macro: MacroDescriptor) {
            return macro.getDisplayName().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

    }


}