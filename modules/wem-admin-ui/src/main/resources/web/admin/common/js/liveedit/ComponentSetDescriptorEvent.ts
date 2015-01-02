module api.liveedit {

    import Event = api.event.Event;
    import Descriptor = api.content.page.Descriptor;
    import ItemView = api.liveedit.ItemView;
    import DescriptorBasedComponent = api.content.page.DescriptorBasedComponent;

    export class ComponentSetDescriptorEvent extends api.event.Event {

        private descriptor: Descriptor;

        private pageItemView: ComponentView<DescriptorBasedComponent>;

        constructor(descriptor: Descriptor, itemView: ComponentView<DescriptorBasedComponent>) {
            super();
            this.descriptor = descriptor;
            this.pageItemView = itemView;
        }

        getDescriptor(): Descriptor {
            return this.descriptor;
        }

        getPageComponentView(): ComponentView<DescriptorBasedComponent> {
            return this.pageItemView;
        }

        static on(handler: (event: ComponentSetDescriptorEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentSetDescriptorEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}