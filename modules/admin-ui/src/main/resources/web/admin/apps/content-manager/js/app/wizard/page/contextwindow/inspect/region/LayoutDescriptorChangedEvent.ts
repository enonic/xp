module app.wizard.page.contextwindow.inspect.region {

    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;

    export class LayoutDescriptorChangedEvent {

        private layoutView: LayoutComponentView;

        private descriptor: api.content.page.region.LayoutDescriptor;

        constructor(layoutView: LayoutComponentView, descriptor: api.content.page.region.LayoutDescriptor) {
            this.layoutView = layoutView;
            this.descriptor = descriptor;
        }

        getLayoutComponentView(): LayoutComponentView {
            return this.layoutView;
        }

        getDescriptor(): api.content.page.region.LayoutDescriptor {
            return this.descriptor;
        }
    }
}
