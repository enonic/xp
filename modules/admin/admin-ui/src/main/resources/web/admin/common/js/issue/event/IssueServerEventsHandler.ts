module api.issue.event {

    import NodeServerChangeType = api.event.NodeServerChangeType;

    export class IssueServerEventsHandler {

        private static INSTANCE: IssueServerEventsHandler = new IssueServerEventsHandler();

        private issueCreatedListeners: {(issueIds: string[]): void}[] = [];

        private issueUpdatedListeners: {(issueIds: string[]): void}[] = [];

        private static debug: boolean = false;

        private handler: (event: IssueServerEvent) => void;

        private constructor() {
            // to let lint bypass
        }

        public static getInstance(): IssueServerEventsHandler {
            return IssueServerEventsHandler.INSTANCE;
        }

        start() {
            if (!this.handler) {
                this.handler = this.issueServerEventHandler.bind(this);
            }
            IssueServerEvent.on(this.handler);
        }

        stop() {
            if (this.handler) {
                IssueServerEvent.un(this.handler);
                this.handler = null;
            }
        }

        private issueServerEventHandler(event: IssueServerEvent) {
            if (IssueServerEventsHandler.debug) {
                console.debug('PrincipalServerEventsHandler: received server event', event);
            }

            const issueIds: string[] = event.getNodeChange().getChangeItems().map(
                (changeItem: IssueServerChangeItem) => changeItem.getIssueId());

            if (event.getType() === NodeServerChangeType.CREATE) {
                this.handleIssueCreate(issueIds);
            }

            if (event.getType() === NodeServerChangeType.UPDATE) {
                this.handleIssueUpdate(issueIds);
            }
        }

        private handleIssueCreate(issueIds: string[]) {
            // currently no Issue object needed, but when needed then need to fetch those and pass Issue objects
            this.notifyIssueCreated(issueIds);
        }

        private handleIssueUpdate(issueIds: string[]) {
            // currently no Issue object needed, but when needed then need to fetch those and pass Issue objects
            this.notifyIssueUpdated(issueIds);
        }

        onIssueCreated(listener: (issueIds: string[])=>void) {
            this.issueCreatedListeners.push(listener);
        }

        unIssueCreated(listener: (issueIds: string[])=>void) {
            this.issueCreatedListeners =
                this.issueCreatedListeners.filter((currentListener: (issueIds: string[])=>void) => {
                    return currentListener !== listener;
                });
        }

        private notifyIssueCreated(issueIds: string[]) {
            this.issueCreatedListeners.forEach((listener: (issueIds: string[])=>void) => {
                listener(issueIds);
            });
        }

        onIssueUpdated(listener: (issueIds: string[])=>void) {
            this.issueUpdatedListeners.push(listener);
        }

        unIssueUpdated(listener: (issueIds: string[])=>void) {
            this.issueUpdatedListeners =
                this.issueUpdatedListeners.filter((currentListener: (issueIds: string[])=>void) => {
                    return currentListener !== listener;
                });
        }

        private notifyIssueUpdated(issueIds: string[]) {
            this.issueUpdatedListeners.forEach((listener: (issueIds: string[])=>void) => {
                listener(issueIds);
            });
        }
    }
}
