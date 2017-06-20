    import {PublishRequestItem} from './PublishRequestItem';
    import {PublishRequestJson} from './json/PublishRequestJson';
    import ContentId = api.content.ContentId;

    export class PublishRequest {

        private excludeIds: ContentId[];

        private items: PublishRequestItem[];

        constructor(builder: PublishRequestBuilder) {
            this.excludeIds = builder.excludeIds;
            this.items = builder.issueItems;
        }

        public getExcludeIds(): ContentId[] {
            return this.excludeIds;
        }

        public getItems(): PublishRequestItem[] {
            return this.items;
        }

        public getItemsIds(): ContentId[] {
            return this.items ?
                   this.items.map(item => item.getId()) : [];
        }

        public getExcludeChildrenIds(): ContentId[] {
            return this.items ?
                   this.items.filter(item => !item.isIncludeChildren()).map(item => item.getId()) : [];
        }

        toJson(): PublishRequestJson {
            return {
                excludeIds: this.excludeIds.map(id => id.toString()),
                items: this.items.map(id => id.toJson()),
            };
        }

        public static create(source?: PublishRequest): PublishRequestBuilder {
            return new PublishRequestBuilder(source);
        }

    }

    export class PublishRequestBuilder {

        excludeIds: ContentId[] = [];

        issueItems: PublishRequestItem[] = [];

        constructor(source?: PublishRequest) {
            if(source) {
                this.excludeIds = !!source.getExcludeIds() ? source.getExcludeIds().slice() : [];
                this.issueItems = !!source.getItems() ? source.getItems().slice() : [];
            }
        }

        fromJson(json: PublishRequestJson): PublishRequestBuilder {
            this.excludeIds = json.excludeIds ? json.excludeIds.map(excludeId => new ContentId(excludeId)) : [];
            this.issueItems = json.items ?
                              json.items.map(itemJson => PublishRequestItem.create().fromJson(itemJson).build()) : [];

            return this;
        }

        public addExcludeId(id: ContentId): PublishRequestBuilder {
            this.excludeIds.push(id);
            return this;
        }

        public addExcludeIds(ids: ContentId[] = []): PublishRequestBuilder {
            this.excludeIds = this.excludeIds.concat(ids);
            return this;
        }

        public addPublishRequestItem(item: PublishRequestItem): PublishRequestBuilder {
            this.issueItems.push(item);
            return this;
        }

        public addPublishRequestItems(items: PublishRequestItem[] = []): PublishRequestBuilder {
            this.issueItems = this.issueItems.concat(items);
            return this;
        }

        public setPublishRequestItems(items: PublishRequestItem[] = []): PublishRequestBuilder {
            this.issueItems = items.slice();
            return this;
        }

        public build(): PublishRequest {
            return new PublishRequest(this);
        }
    }
