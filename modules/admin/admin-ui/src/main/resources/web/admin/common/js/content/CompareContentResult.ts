module api.content {


    export class CompareContentResult implements api.Equitable {

        compareStatus: api.content.CompareStatus;

        id: string;

        constructor(id: string, compareStatus: CompareStatus) {

            this.compareStatus = compareStatus;
            this.id = id;
        }

        getId(): string {
            return this.id;
        }

        getCompareStatus(): CompareStatus {
            return this.compareStatus;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, CompareContentResult)) {
                return false;
            }

            var other = <CompareContentResult>o;

            if (!api.ObjectHelper.stringEquals(this.id.toString(), other.id.toString())) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.content.json.CompareContentResultJson): CompareContentResult {

            var status: CompareStatus = <CompareStatus>CompareStatus[json.compareStatus];

            return new CompareContentResult(json.id, status);
        }
    }
}