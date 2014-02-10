module api.content.page.image {

    export class ImageDescriptorLoader implements api.util.Loader {

        private request: ImageDescriptorsResourceRequest;

        private isLoading: boolean;

        private descriptors: ImageDescriptor[];

        private listeners: ImageDescriptorLoaderListener[] = [];

        constructor(request: ImageDescriptorsResourceRequest) {
            this.isLoading = false;
            this.request = request;

            this.isLoading = true;
            this.notifyLoading();

            this.doRequest(this.request).
                done((descriptors: ImageDescriptor[]) => {
                    this.descriptors = descriptors;
                    this.isLoading = false;
                    console.log("image descriptors", this.descriptors, this.descriptors[0].getKey());
                    this.notifyLoaded(this.descriptors);
                });
        }

        doRequest(request: ImageDescriptorsResourceRequest): Q.Promise<ImageDescriptor[]> {
            var deferred = Q.defer<ImageDescriptor[]>();

            request.sendAndParse()
                .done((descriptors: ImageDescriptor[]) => {
                    deferred.resolve(descriptors)
                });
            return deferred.promise;
        }

        search(searchString: string) {

            if (this.descriptors) {
                var filtered = this.descriptors.filter((descriptor: ImageDescriptor) => {
                    return descriptor.getDisplayName().toString().indexOf(searchString.toLowerCase()) != -1;
                });
                this.notifyLoaded(filtered);
            }
        }

        addListener(listener: ImageDescriptorLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove: ImageDescriptorLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private notifyLoading() {
            this.listeners.forEach((listener: ImageDescriptorLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(descriptors: ImageDescriptor[]) {
            this.listeners.forEach((listener: ImageDescriptorLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(descriptors);
                }
            });
        }
    }
}