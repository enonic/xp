module api.issue.event {

    import NodeServerChangeType = api.event.NodeServerChangeType;

    export class IssueServerEvent extends api.event.NodeServerEvent {

        constructor(change: IssueServerChange) {
            super(change);
        }

        getType(): NodeServerChangeType {
            return this.getNodeChange() ? this.getNodeChange().getChangeType() : null;
        }

        getNodeChange(): IssueServerChange {
            return <IssueServerChange>super.getNodeChange();
        }

        static is(eventJson: api.event.NodeEventJson): boolean {
            return eventJson.data.nodes.some(node => node.path.indexOf('/issue') === 0);
        }

        static fromJson(nodeEventJson: api.event.NodeEventJson): IssueServerEvent {
            let change = IssueServerChange.fromJson(nodeEventJson);
            return new IssueServerEvent(change);
        }
    }
}
