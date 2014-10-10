module app.wizard.page.contextwindow.inspect {

    export class PageControllerChangedEvent {

        private descriptor: api.content.page.PageDescriptor;

        constructor(descriptor: api.content.page.PageDescriptor) {
            this.descriptor = descriptor;
        }

        getPageDescriptor(): api.content.page.PageDescriptor {
            return this.descriptor;
        }
    }
}