module api.content.event {

    export class ContentServerEvent extends api.event.NodeServerEvent {

        constructor(change: ContentServerChange) {
            super(change);
        }

        getNodeChange(): ContentServerChange {
            return <ContentServerChange>super.getNodeChange();
        }

        static is(eventJson: api.event.NodeEventJson): boolean {
            return eventJson.data.nodes.some(node => node.path.indexOf("/content") == 0);
        }

        static fromJson(nodeEventJson: api.event.NodeEventJson): ContentServerEvent {
            let change = ContentServerChange.fromJson(nodeEventJson);
            return new ContentServerEvent(change);
        }
    }
}
