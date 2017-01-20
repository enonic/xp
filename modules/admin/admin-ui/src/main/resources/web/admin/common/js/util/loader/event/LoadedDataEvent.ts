module api.util.loader.event {

    export class LoadedDataEvent<V> extends LoaderEvent {

        private data: V[];

        constructor(data: V[], postLoad?: boolean) {
            super(postLoad);
            this.data = data;
        }

        getData(): V[] {
            return this.data;
        }
    }
}
