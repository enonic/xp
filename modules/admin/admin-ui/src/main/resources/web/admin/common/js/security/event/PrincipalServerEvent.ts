module api.security.event {

    import NodeServerChangeType = api.event.NodeServerChangeType;

    export class PrincipalServerEvent extends api.event.NodeServerEvent {

        constructor(change: PrincipalServerChange) {
            super(change);
        }

        getType() : NodeServerChangeType {
            return this.getNodeChange() ? this.getNodeChange().getChangeType() : null;
        }

        getNodeChange(): PrincipalServerChange {
            return <PrincipalServerChange>super.getNodeChange();
        }

        static is(eventJson: api.event.NodeEventJson): boolean {
            return eventJson.data.nodes.some(node => node.path.indexOf('/identity') == 0);
        }

        static fromJson(nodeEventJson: api.event.NodeEventJson): PrincipalServerEvent {
            let change = PrincipalServerChange.fromJson(nodeEventJson);
            return new PrincipalServerEvent(change);
        }
    }
}
