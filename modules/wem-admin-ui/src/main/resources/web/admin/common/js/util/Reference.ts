module api.util {

    export class Reference implements api.Equitable {

        private nodeId: string;

        constructor(value: string) {
            this.nodeId = value;
        }

        getNodeId(): string {
            return this.nodeId;
        }

        equals(o: Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Reference)) {
                return false;
            }

            var other = <Reference>o;

            if (!api.ObjectHelper.stringEquals(this.nodeId, other.nodeId)) {
                return false;
            }
        }

        toString(): string {
            return this.nodeId;
        }
    }
}