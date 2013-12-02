module api_module {

    export class ModuleImportedEvent extends api_event.Event {

        constructor() {
            super( 'ModuleImportedEvent' );
        }

        static on( handler: (event: ModuleImportedEvent ) => void ) {
            api_event.onEvent( 'ModuleImportedEvent', handler );
        }

    }
}