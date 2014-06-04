module app.wizard.page.contextwindow.inspect {

    import LayoutView = api.liveedit.layout.LayoutView;

    export class LayoutDescriptorChangedEvent {

        private layoutView: LayoutView;

        private descriptor: api.content.page.layout.LayoutDescriptor;

        constructor(layoutView: LayoutView, descriptor: api.content.page.layout.LayoutDescriptor) {
            this.layoutView = layoutView;
            this.descriptor = descriptor;
        }

        getLayoutView(): LayoutView {
            return this.layoutView;
        }

        getDescriptor(): api.content.page.layout.LayoutDescriptor {
            return this.descriptor;
        }
    }
}
