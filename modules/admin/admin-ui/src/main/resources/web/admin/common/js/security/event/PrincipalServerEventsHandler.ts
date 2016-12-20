module api.security.event {

    import ContentPath = api.content.ContentPath;
    import NodeServerChangeType = api.event.NodeServerChangeType;

    /**
     * Class that listens to server events and fires UI events
     */
    export class PrincipalServerEventsHandler {

        private static instance: PrincipalServerEventsHandler = new PrincipalServerEventsHandler();

        private handler: (event: PrincipalServerEvent) => void;

        private principalDeletedListeners: {(paths: string[]):void}[] = [];

        private static debug: boolean = false;

        static getInstance(): PrincipalServerEventsHandler {
            return this.instance;
        }

        start() {
            if (!this.handler) {
                this.handler = this.principalServerEventHandler.bind(this);
            }
            PrincipalServerEvent.on(this.handler);
        }

        stop() {
            if (this.handler) {
                PrincipalServerEvent.un(this.handler);
                this.handler = null;
            }
        }


        private principalServerEventHandler(event: PrincipalServerEvent) {
            if (PrincipalServerEventsHandler.debug) {
                console.debug("PrincipalServerEventsHandler: received server event", event);
            }

            if (event.getType() == NodeServerChangeType.DELETE) {
               this.handleContentDeleted(this.extractContentPaths([event.getNodeChange()]));
            }
        }

        private handleContentDeleted(oldPaths: string[]) {
            if (PrincipalServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: deleted", oldPaths);
            }
            var contentDeletedEvent = new PrincipalDeletedEvent();

            oldPaths.filter((path) => {
                return !!path;        // not sure if this check is necessary
            }).forEach((path) => {
                contentDeletedEvent.addItem(path);
            });
            contentDeletedEvent.fire();

            this.notifyPrincipalDeleted(oldPaths);
        }

        private extractContentPaths(changes: PrincipalServerChange[], useNewPaths?: boolean): string[] {
            return changes.reduce<string[]>((prev, curr) => {
                return prev.concat(useNewPaths ?
                                   curr.getNewPaths() :
                                   curr.getChangeItems().map((changeItem: PrincipalServerChangeItem) => changeItem.getPath()));
            }, []);
        }

        onPrincipalDeleted(listener: (paths: string[])=>void) {
            this.principalDeletedListeners.push(listener);
        }

        unPrincipalDeleted(listener: (paths: string[])=>void) {
            this.principalDeletedListeners =
                this.principalDeletedListeners.filter((currentListener: (paths: string[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyPrincipalDeleted(paths: string[]) {
            this.principalDeletedListeners.forEach((listener: (paths: string[])=>void) => {
                listener(paths);
            });
        }
    }
}