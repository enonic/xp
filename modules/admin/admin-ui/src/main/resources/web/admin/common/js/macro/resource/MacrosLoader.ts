module api.macro.resource {

    import ApplicationEvent = api.application.ApplicationEvent;
    import ApplicationEventType = api.application.ApplicationEventType;
    import ApplicationKey = api.application.ApplicationKey;

    export class MacrosLoader extends api.util.loader.BaseLoader<MacrosJson, MacroDescriptor> {

        protected request: GetMacrosRequest;
        private hasRelevantData: boolean;

        constructor() {
            super();

            this.hasRelevantData = false;

            ApplicationEvent.on((event: ApplicationEvent) => {
                if (event.getEventType() == ApplicationEventType.STARTED || event.getEventType() == ApplicationEventType.STOPPED ||
                    event.getEventType() == ApplicationEventType.UPDATED) {
                    this.invalidate();
                }
            });
        }

        setApplicationKeys(applicationKeys: ApplicationKey[]) {
            this.getRequest().setApplicationKeys(applicationKeys);
        }

        protected createRequest(): GetMacrosRequest {
            return new GetMacrosRequest();
        }

        protected getRequest(): GetMacrosRequest {
            return this.request;
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

        search(searchString: string): wemQ.Promise<MacroDescriptor[]> {
            if (this.hasRelevantData) {
                return super.search(searchString);
            } else {
                return this.load().then(() => {
                    return super.search(searchString);
                });
            }
        }

        filterFn(macro: MacroDescriptor) {
            return macro.getDisplayName().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

    }

}
