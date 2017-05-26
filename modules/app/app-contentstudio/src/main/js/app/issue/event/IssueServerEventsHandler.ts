import NodeServerChangeType = api.event.NodeServerChangeType;
import IssueServerEvent = api.issue.event.IssueServerEvent;
import IssueServerChangeItem = api.issue.event.IssueServerChangeItem;
import {GetIssuesRequest} from '../resource/GetIssuesRequest';
import {Issue} from '../Issue';

export class IssueServerEventsHandler {

    private static INSTANCE: IssueServerEventsHandler = new IssueServerEventsHandler();

    private issueCreatedListeners: {(issues: Issue[]): void}[] = [];

    private issueUpdatedListeners: {(issues: Issue[]): void}[] = [];

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
        new GetIssuesRequest(issueIds).sendAndParse().then((issues: Issue[]) => {
            setTimeout(() => { // giving a chance for backend to refresh indexes so we get correct results on requests
                this.notifyIssueCreated(issues);
            }, 1000);
        });
    }

    private handleIssueUpdate(issueIds: string[]) {
        new GetIssuesRequest(issueIds).sendAndParse().then((issues: Issue[]) => {
            setTimeout(() => { // giving a chance for backend to refresh indexes so we get correct results on requests
                this.notifyIssueUpdated(issues);
            }, 1000);
        });

    }

    onIssueCreated(listener: (issues: Issue[])=>void) {
        this.issueCreatedListeners.push(listener);
    }

    unIssueCreated(listener: (issues: Issue[])=>void) {
        this.issueCreatedListeners =
            this.issueCreatedListeners.filter((currentListener: (issues: Issue[])=>void) => {
                return currentListener !== listener;
            });
    }

    private notifyIssueCreated(issues: Issue[]) {
        this.issueCreatedListeners.forEach((listener: (issues: Issue[])=>void) => {
            listener(issues);
        });
    }

    onIssueUpdated(listener: (issues: Issue[])=>void) {
        this.issueUpdatedListeners.push(listener);
    }

    unIssueUpdated(listener: (issues: Issue[])=>void) {
        this.issueUpdatedListeners =
            this.issueUpdatedListeners.filter((currentListener: (issues: Issue[])=>void) => {
                return currentListener !== listener;
            });
    }

    private notifyIssueUpdated(issues: Issue[]) {
        this.issueUpdatedListeners.forEach((listener: (issues: Issue[])=>void) => {
            listener(issues);
        });
    }
}
