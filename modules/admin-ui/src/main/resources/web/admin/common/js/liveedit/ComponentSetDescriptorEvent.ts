module api.liveedit {

    import Event = api.event.Event;
    import Descriptor = api.content.page.Descriptor;
    import ItemView = api.liveedit.ItemView;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;

    export class ComponentSetDescriptorEvent extends api.event.Event {

        private descriptor: Descriptor;

        private componentView: ComponentView<DescriptorBasedComponent>;

        constructor(descriptor: Descriptor, componentView: ComponentView<DescriptorBasedComponent>) {
            super();
            this.descriptor = descriptor;
            this.componentView = componentView;
        }

        getDescriptor(): Descriptor {
            return this.descriptor;
        }

        getComponentView(): ComponentView<DescriptorBasedComponent> {
            return this.componentView;
        }

        static on(handler: (event: ComponentSetDescriptorEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentSetDescriptorEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}