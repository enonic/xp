module app.wizard.event {

    export class ShowSiteTemplateFormEvent extends api.event.Event {

        constructor() {
            super('showSiteTemplateForm');
        }

        static on(handler:(event:ShowSiteTemplateFormEvent) => void) {
            api.event.onEvent('showSiteTemplateForm', handler);
        }

    }

}