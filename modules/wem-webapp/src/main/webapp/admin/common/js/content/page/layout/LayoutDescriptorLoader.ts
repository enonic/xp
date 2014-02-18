module api.content.page.layout {

    export class LayoutDescriptorLoader implements api.util.Loader {

        private request: LayoutDescriptorsResourceRequest;

        private isLoading: boolean;

        private descriptors: LayoutDescriptor[];

        private listeners: LayoutDescriptorLoaderListener[] = [];

        constructor(request: LayoutDescriptorsResourceRequest) {
            this.isLoading = false;
            this.request = request;

            this.isLoading = true;
            this.notifyLoading();

            this.doRequest(this.request).
                done((descriptors: LayoutDescriptor[]) => {
                    this.descriptors = descriptors;
                    this.isLoading = false;
                    this.notifyLoaded(this.descriptors);
                });
        }

        doRequest(request: LayoutDescriptorsResourceRequest): Q.Promise<LayoutDescriptor[]> {
            var deferred = Q.defer<LayoutDescriptor[]>();

            request.sendAndParse()
                .done((descriptors: LayoutDescriptor[]) => {
                    deferred.resolve(descriptors)
                });
            return deferred.promise;
        }

        search(searchString: string) {

            if (this.descriptors) {
                var filtered = this.descriptors.filter((descriptor: LayoutDescriptor) => {
                    return descriptor.getDisplayName().toString().indexOf(searchString.toLowerCase()) != -1;
                });
                this.notifyLoaded(filtered);
            }
        }

        addListener(listener: LayoutDescriptorLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove: LayoutDescriptorLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private notifyLoading() {
            this.listeners.forEach((listener: LayoutDescriptorLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(descriptors: LayoutDescriptor[]) {
            this.listeners.forEach((listener: LayoutDescriptorLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(descriptors);
                }
            });
        }
    }
}