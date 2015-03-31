module api.content {

    export class ContentIds implements api.Equitable {

        private array: ContentId[];

        constructor(array: ContentId[]) {
            this.array = [];
            array.forEach((contentId: ContentId) => {

                var duplicate = this.array.some((possibleDuplicate: ContentId) => {
                    return contentId.equals(possibleDuplicate);
                });

                if (!duplicate) {
                    this.array.push(contentId);
                }
                else {
                    throw Error("ContentIds do not allow duplicates, found: '" + contentId.toString() + "'");
                }
            });
        }

        map<U>(callbackfn: (value: ContentId, index?: number) => U): U[] {
            return this.array.map((value: ContentId, index: number) => {
                return callbackfn(value, index);
            });
        }

        static from(contentIds: ContentId[]) : ContentIds {
            return ContentIds.create().fromContentIds(contentIds).build();
        }

        static fromContents(contents: ContentSummary[]) : ContentIds {
            var builder = ContentIds.create();
            contents.forEach((content) => {
               builder.addContentId(content.getContentId());
            });
            return builder.build();
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentIds)) {
                return false;
            }

            var other = <ContentIds>o;
            return api.ObjectHelper.arrayEquals(this.array, other.array);
        }

        static create(): ContentIdsBuilder {
            return new ContentIdsBuilder();
        }
    }

    class ContentIdsBuilder {

        array: ContentId[] = [];

        fromStrings(values: string[]): ContentIdsBuilder {
            if (!!values) {
                values.forEach((value: string) => {
                    this.addContentId(new ContentId(value));
                });
            }
            return this;
        }

        fromContentIds(contentIds: ContentId[]): ContentIdsBuilder {
            if (!!contentIds) {
                contentIds.forEach((contentId: ContentId) => this.addContentId(contentId) );
            }
            return this;
        }

        addContentId(value: ContentId): ContentIdsBuilder {
            this.array.push(value);
            return this;
        }

        build(): ContentIds {
            return new ContentIds(this.array);
        }
    }
}
