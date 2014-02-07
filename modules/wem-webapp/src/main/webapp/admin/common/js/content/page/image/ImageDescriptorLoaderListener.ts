module api.content.page.image {

    export interface ImageDescriptorLoaderListener extends api.event.Listener {

        onLoading: () => void;

        onLoaded: (descriptors: ImageDescriptor[]) => void;
    }
}