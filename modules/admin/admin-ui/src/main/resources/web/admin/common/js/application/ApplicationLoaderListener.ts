module api.application {
    export class ApplicationLoaderListener {
        onLoading: () => void;

        onLoaded: (applications: api.application.Application[]) => void;
    }
}