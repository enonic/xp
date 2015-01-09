module app.wizard.page.contextwindow.inspect.region {

    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;

    export class LayoutDescriptorChangedEvent {

        private layoutView: LayoutComponentView;

        private descriptor: api.content.page.layout.LayoutDescriptor;

        constructor(layoutView: LayoutComponentView, descriptor: api.content.page.layout.LayoutDescriptor) {
            this.layoutView = layoutView;
            this.descriptor = descriptor;
        }

        getLayoutComponentView(): LayoutComponentView {
            return this.layoutView;
        }

        getDescriptor(): api.content.page.layout.LayoutDescriptor {
            return this.descriptor;
        }
    }
}
