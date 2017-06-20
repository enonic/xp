module api.issue.event {

    import NodeServerChange = api.event.NodeServerChange;
    import NodeEventJson = api.event.NodeEventJson;
    import NodeServerChangeItem = api.event.NodeServerChangeItem;
    import NodeEventNodeJson = api.event.NodeEventNodeJson;
    import NodeServerChangeType = api.event.NodeServerChangeType;

    export class IssueServerChangeItem extends NodeServerChangeItem<string> {

        issueId: string;

        constructor(path: string, branch: string, issueId: string) {
            super(path, branch);
            this.issueId = issueId;
        }

        getIssueId(): string {
            return this.issueId;
        }

        static fromJson(node: NodeEventNodeJson): IssueServerChangeItem {
            return new IssueServerChangeItem(node.path.substr('/issue'.length), node.branch, node.id);
        }
    }

    export class IssueServerChange extends NodeServerChange<string> {
        constructor(type: NodeServerChangeType, changeItems: IssueServerChangeItem[], newPrincipalPaths?: string[]) {
            super(type, changeItems, newPrincipalPaths);
        }

        toString(): string {
            return NodeServerChangeType[this.type] + ': <' +
                   this.changeItems.map((item) => item.getPath()).join(', ') + !!this.newNodePaths
                ? this.newNodePaths.join(', ')
                : '' +
                  '>';
        }

        static fromJson(nodeEventJson: NodeEventJson): IssueServerChange {

            let changedItems = nodeEventJson.data.nodes.filter((node) => node.path.indexOf('/issue') === 0).map(
                (node: NodeEventNodeJson) => IssueServerChangeItem.fromJson(node));

            if (changedItems.length === 0) {
                return null;
            }

            let principalEventType = this.getNodeServerChangeType(nodeEventJson.type);
            return new IssueServerChange(principalEventType, changedItems);
        }
    }
}
