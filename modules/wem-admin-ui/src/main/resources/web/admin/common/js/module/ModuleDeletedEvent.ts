module api.module {

    export class ModuleDeletedEvent extends api.event.Event {

        private moduleKey: ModuleKey;

        constructor(moduleKey: ModuleKey) {
            super( 'ModuleDeletedEvent' );
            this.moduleKey = moduleKey;
        }

        getModuleKey():ModuleKey {
            return this.moduleKey;
        }

        static on( handler: (event: ModuleDeletedEvent ) => void ) {
            api.event.onEvent( 'ModuleDeletedEvent', handler );
        }

    }
}