module api.aggregation {

    export class Bucket {

        private name: string;
        private docCount: number;

        public getName(): string {
            return this.name;
        }

        public getDocCount(): number {
            return this.docCount;
        }

        public static fromJson(json: api.aggregation.BucketJson): Bucket {
            var bucket: Bucket = new Bucket();
            bucket.name = json.name;
            bucket.docCount = json.count;
            return bucket;
        }
    }
}