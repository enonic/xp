module api.util.loader.event {

    export class LoadedDataEvent<V> extends LoaderEvent {

        private data:V[];

        constructor(data:V[]) {
            super();
            this.data = data;
        }

        getData():V[] {
            return this.data;
        }
    }
}