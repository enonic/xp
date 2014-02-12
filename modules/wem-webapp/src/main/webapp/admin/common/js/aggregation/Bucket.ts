module api.aggregation {

    export class Bucket {

        key: string;
        docCount: number;

        constructor(key: string, docCount: number) {
            this.key = key;
            this.docCount = docCount;
        }

        public getKey(): string {
            return this.key;
        }

        public getDocCount(): number {
            return this.docCount;
        }

        public setKey(key: string) {
            this.key = key;
        }

        public setDocCount(docCount: number) {
            this.docCount = docCount;
        }

        public static fromJson(json: api.aggregation.BucketJson): Bucket {
            return new Bucket(json.key, json.docCount);
        }

        public getSelectionValue(): any {
            return "test";
        }

    }
}