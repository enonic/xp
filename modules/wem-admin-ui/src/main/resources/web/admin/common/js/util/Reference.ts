module api.util {

    export class Reference implements api.Equitable {

        private referenceId: string;

        constructor(value: string) {
            this.referenceId = value;
        }

        static from(value: api.content.ContentId): Reference {
            return new Reference(value.toString());
        }

        getNodeId(): string {
            return this.referenceId;
        }

        equals(o: Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Reference)) {
                return false;
            }

            var other = <Reference>o;

            if (!api.ObjectHelper.stringEquals(this.referenceId, other.referenceId)) {
                return false;
            }

            return true;
        }

        toString(): string {
            return this.referenceId;
        }
    }
}