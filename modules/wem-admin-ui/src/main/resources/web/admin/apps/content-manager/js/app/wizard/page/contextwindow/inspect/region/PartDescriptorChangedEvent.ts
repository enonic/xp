module app.wizard.page.contextwindow.inspect.region {

    import PartComponentView = api.liveedit.part.PartComponentView;

    export class PartDescriptorChangedEvent {

        private partView: PartComponentView;

        private descriptor: api.content.page.part.PartDescriptor;

        constructor(partView: PartComponentView, descriptor: api.content.page.part.PartDescriptor) {
            this.partView = partView;
            this.descriptor = descriptor;
        }

        getPartComponentView(): PartComponentView {
            return this.partView;
        }

        getDescriptor(): api.content.page.part.PartDescriptor {
            return this.descriptor;
        }
    }
}
