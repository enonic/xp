module api.aggregation {

    export class Bucket {

        private key: string;
        private docCount: number;

        public getKey(): string {
            return this.key;
        }

        public getDocCount(): number {
            return this.docCount;
        }

        public static fromJson(json: api.aggregation.BucketJson): Bucket {
            var bucket: Bucket = new Bucket();
            bucket.key = json.key;
            bucket.docCount = json.docCount;
            return bucket;
        }
    }
}