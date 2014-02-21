module api.util.loader {

    import LoaderEvents = api.util.loader.event.LoaderEvents;
    import LoaderEvent = api.util.loader.event.LoaderEvent;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class BaseLoader<T, V> {

        private request: api.rest.ResourceRequest<T>;

        private isLoading: boolean;

        private results: V[];

        private searchString: string;

        private listeners: {[eventName:string]:{(event: LoaderEvent):void}[]} = {};

        constructor(request: api.rest.ResourceRequest<T>, autoLoad: boolean = true) {
            this.listeners[LoaderEvents.LoadedData] = [];
            this.listeners[LoaderEvents.LoadingData] = [];
            this.isLoading = false;
            this.setRequest(request);
            if (autoLoad) {
                this.load();
            }
        }

        doRequest(): Q.Promise<V[]> {
            var deferred = Q.defer<V[]>();

            this.request.sendAndParse().done((results: V[]) => {
                deferred.resolve(results);
            });

            return deferred.promise;
        }

        load(): void {
            this.isLoading = true;
            this.notifyLoadingData(new LoadingDataEvent());
            this.doRequest().done((results: V[]) => {
                this.results = results;
                this.isLoading = false;
                this.notifyLoadedData(new LoadedDataEvent(results));
            });
        }

        loading(isLoading?:boolean):boolean {
            if (typeof isLoading == 'boolean' ) {
                this.isLoading = isLoading;
            }
            return this.isLoading;
        }

        setRequest(request: api.rest.ResourceRequest<T>) {
            this.request = request;
        }

        getRequest(): api.rest.ResourceRequest<T> {
            return this.request;
        }

        search(searchString: string) {

            this.searchString = searchString;
            if (this.results) {
                var filtered = this.results.filter(this.filterFn, this);
                this.notifyLoadedData(new LoadedDataEvent<V>(this.results));
            }
        }

        getSearchString(): string {
            return this.searchString;
        }

        filterFn(result: V): boolean {
            throw Error("must be implemented");
        }

        private addListener(eventName: LoaderEvents, listener: (event: LoaderEvent) => void) {
            this.listeners[eventName].push(listener);
        }

        private removeListener(eventName: LoaderEvents, listener: (event: LoaderEvent) => void) {
            this.listeners[eventName].filter((currentListener: (event: LoaderEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyListeners(eventName: LoaderEvents, event: LoaderEvent) {
            this.listeners[eventName].forEach((listener: (event: LoaderEvent)=>void) => {
                listener(event);
            });
        }

        notifyLoadedData(event: LoadedDataEvent<V>) {
            this.notifyListeners(LoaderEvents.LoadedData, event);
        }

        notifyLoadingData(event: LoadingDataEvent) {
            this.notifyListeners(LoaderEvents.LoadingData, event);
        }

        onLoadedData(listener: (event: LoadedDataEvent<V>) => void) {
            this.addListener(LoaderEvents.LoadedData, listener);
        }

        onLoadingData(listener: (event: LoadingDataEvent) => void) {
            this.addListener(LoaderEvents.LoadingData, listener);
        }

        unLoadedData(listener: (event: LoadedDataEvent<V>) => void) {
            this.removeListener(LoaderEvents.LoadedData, listener);
        }

        unLoadingData(listener: (event: LoadingDataEvent) => void) {
            this.removeListener(LoaderEvents.LoadingData, listener);
        }

    }
}