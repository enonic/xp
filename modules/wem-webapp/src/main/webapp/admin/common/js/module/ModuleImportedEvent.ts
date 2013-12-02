module api_module {

    export class ModuleImportedEvent extends api_event.Event {

        private module: Module;

        constructor(module: Module) {
            super( 'ModuleImportedEvent' );
            this.module = module;
        }

        getModule():Module {
            return this.module;
        }

        static on( handler: (event: ModuleImportedEvent ) => void ) {
            api_event.onEvent( 'ModuleImportedEvent', handler );
        }

    }
}