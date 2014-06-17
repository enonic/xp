module api.content {

    export class CompareContentResult {

        compareStatus: api.content.CompareStatus;

        id: string;

        constructor(id: string, compareStatus: CompareStatus) {

            this.compareStatus = compareStatus;
            this.id = id;
        }

        static fromJson(json: CompareContentResultJson): CompareContentResult {

            return new CompareContentResult(json.id, CompareStatus.fromJson(json.compareStatus));
        }
    }
}