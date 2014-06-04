module app.wizard.page.contextwindow.inspect {

    export class LayoutDescriptorChangedEvent {

        private layoutView: api.liveedit.layout.LayoutView;
        private descriptor: api.content.page.layout.LayoutDescriptor;

        constructor(layoutView: api.liveedit.layout.LayoutView, descriptor: api.content.page.layout.LayoutDescriptor) {
            this.layoutView = layoutView;
            this.descriptor = descriptor;
        }

        getLayoutView(): api.liveedit.layout.LayoutView {
            return this.layoutView;
        }

        getDescriptor(): api.content.page.layout.LayoutDescriptor {
            return this.descriptor;
        }
    }
}
