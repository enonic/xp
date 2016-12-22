module api.app {

    import NodeServerEvent = api.event.NodeServerEvent;
    import NodeServerChangeType = api.event.NodeServerChangeType;

    export class ServerEventAggregator {

        private static AGGREGATION_TIMEOUT: number = 500;

        private events: NodeServerEvent[];

        private type: NodeServerChangeType;

        private batchReadyListeners: {(event):void}[] = [];

        private debounced;

        constructor() {
            this.debounced = api.util.AppHelper.debounce(() => {
                this.notifyBatchIsReady();
            }, ServerEventAggregator.AGGREGATION_TIMEOUT, false);
        }

        getEvents(): NodeServerEvent[] {
            return this.events;
        }

        resetEvents() {
            this.events = [];
        }

        appendEvent(event: NodeServerEvent) {
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

        getType(): NodeServerChangeType {
            return this.type;
        }

        private isTheSameTypeEvent(event: NodeServerEvent) {
            var change = event.getNodeChange();

            if (this.type != change.getChangeType()) {
                return false;
            }

            return true;
        }

        private init(event: NodeServerEvent) {
            this.events = [event];
            this.type = !!event.getNodeChange() ? event.getNodeChange().getChangeType() : null;
        }

        onBatchIsReady(listener: (event)=>void) {
            this.batchReadyListeners.push(listener);
        }

        unBatchIsReady(listener: (event)=>void) {
            this.batchReadyListeners = this.batchReadyListeners.filter((currentListener: (event)=>void)=> {
                return listener != currentListener;
            });
        }

        private notifyBatchIsReady() {
            this.batchReadyListeners.forEach((listener: (event)=>void)=> {
                listener.call(this);
            });
        }

    }
}
