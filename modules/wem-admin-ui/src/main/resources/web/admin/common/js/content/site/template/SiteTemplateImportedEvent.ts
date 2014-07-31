module api.content.site.template {

    export class SiteTemplateImportedEvent extends api.event.Event2 {

        static on(handler: (event: SiteTemplateImportedEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: SiteTemplateImportedEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }

}