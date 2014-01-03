module api.module {

    export class ModuleImportedEvent extends api.event.Event {

        constructor() {
            super( 'ModuleImportedEvent' );
        }

        static on( handler: (event: ModuleImportedEvent ) => void ) {
            api.event.onEvent( 'ModuleImportedEvent', handler );
        }

    }
}