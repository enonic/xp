module api.util.loader.event {

    export class LoadedDataEvent<V> extends LoaderEvent {

        private data: V[];

        private postLoad: boolean;

        constructor(data: V[], postLoad?: boolean) {
            super();
            this.data = data;
            this.postLoad = postLoad;
        }

        getData(): V[] {
            return this.data;
        }

        isPostLoaded(): boolean {
            return this.postLoad;
        }
    }
}