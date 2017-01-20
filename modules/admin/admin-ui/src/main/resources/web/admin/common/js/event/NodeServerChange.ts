module api.event {

    export enum NodeServerChangeType {
        UNKNOWN,
        PUBLISH,
        DUPLICATE,
        CREATE,
        UPDATE,
        DELETE,
        PENDING,
        RENAME,
        SORT,
        MOVE
    }

    export class NodeServerChangeItem<PATH_TYPE> {

        path: PATH_TYPE;

        branch: string;

        constructor(path: PATH_TYPE, branch: string) {
            this.path = path;
            this.branch = branch;
        }

        getPath(): PATH_TYPE {
            return this.path;
        }

        getBranch(): string {
            return this.branch;
        }
    }

    export class NodeServerChange<PATH_TYPE> {

        protected changeItems: NodeServerChangeItem<PATH_TYPE>[];

        protected newNodePaths: PATH_TYPE[];

        protected type: NodeServerChangeType;

        constructor(type: NodeServerChangeType, changeItems: NodeServerChangeItem<PATH_TYPE>[], newNodePaths: PATH_TYPE[]) {
            this.type = type;
            this.changeItems = changeItems;
            this.newNodePaths = newNodePaths;
        }

        getChangeItems(): NodeServerChangeItem<PATH_TYPE>[] {
            return this.changeItems;
        }

        getNewPaths(): PATH_TYPE[] {
            return this.newNodePaths;
        }

        getChangeType(): NodeServerChangeType {
            return this.type;
        }

        protected static getNodeServerChangeType(value: string): NodeServerChangeType {
            switch (value) {
            case 'node.pushed':
                return NodeServerChangeType.PUBLISH;
            case 'node.created':
                return NodeServerChangeType.CREATE;
            case 'node.updated':
                return NodeServerChangeType.UPDATE;
            case 'node.deleted':
                return NodeServerChangeType.DELETE;
            case 'node.duplicated':
                return NodeServerChangeType.DUPLICATE;
            case 'node.stateUpdated':
                return NodeServerChangeType.PENDING;
            case 'node.moved':
                return NodeServerChangeType.MOVE;
            case 'node.renamed':
                return NodeServerChangeType.RENAME;
            case 'node.sorted':
                return NodeServerChangeType.SORT;
            default:
                return NodeServerChangeType.UNKNOWN;
            }
        }

        static fromJson(nodeEventJson: NodeEventJson): NodeServerChange<any> {
            throw new Error('Must be implemented by inheritors');
        }
    }
}
