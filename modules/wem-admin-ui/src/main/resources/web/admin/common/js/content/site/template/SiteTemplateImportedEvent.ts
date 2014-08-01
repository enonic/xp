module api.content.site.template {

    export class SiteTemplateImportedEvent extends api.event.Event {

        static on(handler: (event: SiteTemplateImportedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: SiteTemplateImportedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }

}