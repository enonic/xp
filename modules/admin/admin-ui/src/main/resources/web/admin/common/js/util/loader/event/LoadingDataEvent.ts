module api.util.loader.event {

    export class LoadingDataEvent extends LoaderEvent {

        constructor(postLoad: boolean = false) {
            super(postLoad);
        }

    }
}
