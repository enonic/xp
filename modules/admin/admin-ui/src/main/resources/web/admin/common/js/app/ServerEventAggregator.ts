module api.app {

    import BatchContentServerEvent = api.content.BatchContentServerEvent;

    export class ServerEventAggregator {

        private static AGGREGATION_TIMEOUT: number = 500;

        private events: api.content.ContentServerEvent[];

        private type: api.content.ContentServerChangeType;

        private batchReadyListeners: {(event):void}[] = [];

        private debounced;


        constructor() {
            this.debounced = api.util.AppHelper.debounce(() => {
                this.notifyBatchIsReady();
            }, ServerEventAggregator.AGGREGATION_TIMEOUT, false);
        }

        getEvents(): api.content.ContentServerEvent[] {
            return this.events;
        }

        resetEvents() {
            this.events = [];
        }

        appendEvent(event: api.content.ContentServerEvent) {
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

        getType(): api.content.ContentServerChangeType {
            return this.type;
        }

        private isTheSameTypeEvent(event: api.content.ContentServerEvent) {
            var changes = event.getContentChanges();
            var types = changes.map(change =>  change.getChangeType());

            if (types.some(type => this.type != type)) {
                return false;
            }

            return true;
        }

        private init(event: api.content.ContentServerEvent) {
            this.events = [event];
            this.type = event.getContentChanges().length > 0 ?
                        event.getContentChanges()[0].getChangeType() : null;
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
