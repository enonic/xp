module api.app {

    import ContentServerEvent = api.content.event.ContentServerEvent;
    import ContentServerChangeType = api.content.event.ContentServerChangeType;

    export class ServerEventAggregator {

        private static AGGREGATION_TIMEOUT: number = 500;

        private events: ContentServerEvent[];

        private type: ContentServerChangeType;

        private batchReadyListeners: {(event):void}[] = [];

        private debounced;

        constructor() {
            this.debounced = api.util.AppHelper.debounce(() => {
                this.notifyBatchIsReady();
            }, ServerEventAggregator.AGGREGATION_TIMEOUT, false);
        }

        getEvents(): ContentServerEvent[] {
            return this.events;
        }

        resetEvents() {
            this.events = [];
        }

        appendEvent(event: ContentServerEvent) {
            if (this.events == null || this.events.length == 0) {
                this.init(event);
            } else {
                if (this.isTheSameTypeEvent(event)) {
                    this.events.push(event);
                } else {
                    this.notifyBatchIsReady();
                    this.init(event);
                }
            }
            this.debounced();
        }

        getType(): ContentServerChangeType {
            return this.type;
        }

        private isTheSameTypeEvent(event: ContentServerEvent) {
            var change = event.getContentChange();

            if (this.type != change.getChangeType()) {
                return false;
            }

            return true;
        }

        private init(event: ContentServerEvent) {
            this.events = [event];
            this.type = !!event.getContentChange() ? event.getContentChange().getChangeType() : null;
        }

        onBatchIsReady(listener: (event)=>void) {
            this.batchReadyListeners.push(listener);
        }

        unBatchIsReady(listener: (event)=>void) {
            this.batchReadyListeners = this.batchReadyListeners.filter((currentListener: (event)=>void)=> {
                return listener != currentListener
            });
        }

        private notifyBatchIsReady() {
            this.batchReadyListeners.forEach((listener: (event)=>void)=> {
                listener.call(this);
            });
        }

    }
}
