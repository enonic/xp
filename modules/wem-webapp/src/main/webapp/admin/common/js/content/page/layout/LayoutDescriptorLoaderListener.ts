module api.content.page.layout {

    export interface LayoutDescriptorLoaderListener extends api.event.Listener {

        onLoading: () => void;

        onLoaded: (descriptors: LayoutDescriptor[]) => void;
    }
}