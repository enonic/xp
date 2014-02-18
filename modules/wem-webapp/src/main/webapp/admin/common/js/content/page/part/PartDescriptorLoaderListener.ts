module api.content.page.part {

    export interface PartDescriptorLoaderListener extends api.event.Listener {

        onLoading: () => void;

        onLoaded: (descriptors: PartDescriptor[]) => void;
    }
}