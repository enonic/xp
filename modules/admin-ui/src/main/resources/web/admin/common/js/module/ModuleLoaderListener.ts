module api.module {
    export class ModuleLoaderListener {
        onLoading: () => void;

        onLoaded: (modules: api.module.Application[]) => void;
    }
}