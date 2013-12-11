module api_content_site_template {

    export class SiteTemplateImportedEvent extends api_event.Event {

        constructor() {
            super("SiteTemplateImportedEvent");
        }

        static on( handler: () => void ) {
            api_event.onEvent( "SiteTemplateImportedEvent", handler );
        }

    }

}