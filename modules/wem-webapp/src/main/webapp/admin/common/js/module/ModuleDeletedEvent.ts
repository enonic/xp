module api_module {

    export class ModuleDeletedEvent extends api_event.Event {

        private moduleKey: ModuleKey;

        constructor(moduleKey: ModuleKey) {
            super( 'ModuleDeletedEvent' );
            this.moduleKey = moduleKey;
        }

        getModuleKey():ModuleKey {
            return this.moduleKey;
        }

        static on( handler: (event: ModuleDeletedEvent ) => void ) {
            api_event.onEvent( 'ModuleDeletedEvent', handler );
        }

    }
}