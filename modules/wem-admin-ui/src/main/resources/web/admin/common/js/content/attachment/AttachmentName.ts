module api.content.attachment {

    export class AttachmentName implements api.Equitable {

        private fileName: string;

        constructor(fileName: string) {
            this.fileName = fileName;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, AttachmentName)) {
                return false;
            }

            var other = <AttachmentName>o;

            if (!api.ObjectHelper.stringEquals(this.fileName, other.fileName)) {
                return false;
            }

            return true;
        }

        toString(): string {
            return this.fileName;
        }
    }
}
