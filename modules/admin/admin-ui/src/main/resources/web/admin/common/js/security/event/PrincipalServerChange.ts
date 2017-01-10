module api.security.event {

    import NodeEventJson = api.event.NodeEventJson;
    import NodeEventNodeJson = api.event.NodeEventNodeJson;
    import NodeServerChange = api.event.NodeServerChange;
    import NodeServerChangeType = api.event.NodeServerChangeType;
    import NodeServerChangeItem = api.event.NodeServerChangeItem;

    export class PrincipalServerChangeItem extends NodeServerChangeItem<string> {

        static fromJson(node: NodeEventNodeJson): PrincipalServerChangeItem {
            return new PrincipalServerChangeItem(node.path.substr("/identity".length), node.branch);
        }

    }

    export class PrincipalServerChange extends NodeServerChange<string> {

        constructor(type: NodeServerChangeType, changeItems: PrincipalServerChangeItem[], newPrincipalPaths?: string[]) {
            super(type, changeItems, newPrincipalPaths);
        }

        getChangeType(): NodeServerChangeType {
            return this.type;
        }

        toString(): string {
            return NodeServerChangeType[this.type] + ": <" +
                   this.changeItems.map((item) => item.getPath()).join(", ") + !!this.newNodePaths
                ? this.newNodePaths.join(", ")
                : "" +
                  ">";
        }

        static fromJson(nodeEventJson: NodeEventJson): PrincipalServerChange {

            let changedItems = nodeEventJson.data.nodes.
                filter((node) => node.path.indexOf("/identity") === 0).
                map((node: NodeEventNodeJson) => PrincipalServerChangeItem.fromJson(node));

            if (changedItems.length === 0) {
                return null;
            }

            let principalEventType = this.getNodeServerChangeType(nodeEventJson.type);
            return new PrincipalServerChange(principalEventType, changedItems);
        }
    }
}