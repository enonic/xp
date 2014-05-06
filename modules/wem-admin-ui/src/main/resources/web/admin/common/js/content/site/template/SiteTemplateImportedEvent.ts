module api.content.site.template {

    export class SiteTemplateImportedEvent extends api.event.Event {

        constructor() {
            super("SiteTemplateImportedEvent");
        }

        static on( handler: () => void ) {
            api.event.onEvent( "SiteTemplateImportedEvent", handler );
        }

    }

}