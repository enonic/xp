module api.util.loader.event {

    export class LoaderEvent {

        private postLoad: boolean;

        constructor(postLoad?: boolean) {
            this.postLoad = postLoad;
        }

        isPostLoad(): boolean {
            return this.postLoad;
        }
    }
}
