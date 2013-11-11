module api_content {

    export class ContentDeletedEvent extends api_event.Event {

        private contents:ContentSummary[];

        constructor( contents:ContentSummary[] ) {
            super( "ContentDeletedEvent" );
            this.contents = contents;
        }

        public getContents():ContentSummary[] {
            return this.contents;
        }

        static on( handler:( event:ContentDeletedEvent ) => void ) {
            api_event.onEvent( 'ContentDeletedEvent', handler );
        }

    }
}