module api.content {

    export class ContentChangeResult {

        private type: ContentServerChangeType;

        private result: TreeNodesOfContentPath[];

        constructor(type: ContentServerChangeType, result: TreeNodesOfContentPath[]) {
            this.type = type;
            this.result = result;
        }

        getResult(): TreeNodesOfContentPath[] {
            return this.result;
        }

        getChangeType(): ContentServerChangeType {
            return this.type;
        }
    }

}