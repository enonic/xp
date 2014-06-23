module api.content {


    export class CompareContentResult {

        compareStatus: api.content.CompareStatus;

        id: string;

        constructor(id: string, compareStatus: CompareStatus) {

            this.compareStatus = compareStatus;
            this.id = id;
        }

        static fromJson(json: CompareContentResultJson): CompareContentResult {

            var status: CompareStatus = <CompareStatus>CompareStatus[json.compareStatus];

            return new CompareContentResult(json.id, status);
        }
    }
}