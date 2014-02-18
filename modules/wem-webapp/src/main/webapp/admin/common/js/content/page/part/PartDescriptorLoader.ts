module api.content.page.part {

    export class PartDescriptorLoader implements api.util.Loader {

        private request: PartDescriptorsResourceRequest;

        private isLoading: boolean;

        private descriptors: PartDescriptor[];

        private listeners: PartDescriptorLoaderListener[] = [];

        constructor(request: PartDescriptorsResourceRequest) {
            this.isLoading = false;
            this.request = request;

            this.isLoading = true;
            this.notifyLoading();

            this.doRequest(this.request).
                done((descriptors: PartDescriptor[]) => {
                    this.descriptors = descriptors;
                    this.isLoading = false;
                    this.notifyLoaded(this.descriptors);
                });
        }

        doRequest(request: PartDescriptorsResourceRequest): Q.Promise<PartDescriptor[]> {
            var deferred = Q.defer<PartDescriptor[]>();

            request.sendAndParse()
                .done((descriptors: PartDescriptor[]) => {
                    deferred.resolve(descriptors)
                });
            return deferred.promise;
        }

        search(searchString: string) {

            if (this.descriptors) {
                var filtered = this.descriptors.filter((descriptor: PartDescriptor) => {
                    return descriptor.getDisplayName().toString().indexOf(searchString.toLowerCase()) != -1;
                });
                this.notifyLoaded(filtered);
            }
        }

        addListener(listener: PartDescriptorLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove: PartDescriptorLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private notifyLoading() {
            this.listeners.forEach((listener: PartDescriptorLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(descriptors: PartDescriptor[]) {
            this.listeners.forEach((listener: PartDescriptorLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(descriptors);
                }
            });
        }
    }
}