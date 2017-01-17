module api.event {

    export interface NodeEventJson extends EventJson {
        data: NodeEventDataJson;
    }

    export interface NodeEventDataJson {
        nodes: NodeEventNodeJson[];
    }

    export interface NodeEventNodeJson {
        id: string;
        path: string;
        newPath: string;
        branch: string;
    }

    export class NodeServerEvent extends Event {

        private change: NodeServerChange<any>;

        constructor(change: NodeServerChange<any>) {
            super();
            this.change = change;
        }

        getNodeChange(): NodeServerChange<any> {
            return this.change;
        }

        static is(eventJson: api.event.NodeEventJson): boolean {
            throw new Error('must be implemented in inheritors');
        }

        toString(): string {
            return 'NodeServerEvent: [' + this.change.toString() + ']';
        }

        static on(handler: (event: NodeServerEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: NodeServerEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(nodeEventJson: NodeEventJson): NodeServerEvent {
            throw new Error('must be implemented in inheritors');
        }
    }
}
