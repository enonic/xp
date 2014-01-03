module api.content {

    export class ContentDeletedEvent extends api.event.Event {

        private contents:ContentSummary[];

        constructor( contents:ContentSummary[] ) {
            super( "ContentDeletedEvent" );
            this.contents = contents;
        }

        public getContents():ContentSummary[] {
            return this.contents;
        }

        static on( handler:( event:ContentDeletedEvent ) => void ) {
            api.event.onEvent( 'ContentDeletedEvent', handler );
        }

    }
}