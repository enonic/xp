module api.content.resource {

    import LocalDateTime = api.util.LocalDateTime;

    export class PublishContentRequest extends ContentResourceRequest<api.task.TaskIdJson, api.task.TaskId> {

        private ids: ContentId[] = [];

        private excludedIds: ContentId[] = [];

        private includeChildren: boolean;

        private publishFrom: Date;

        private publishTo: Date;

        constructor(contentId?: ContentId) {
            super();
            super.setMethod("POST");
            if (contentId) {
                this.addId(contentId);
            }
        }

        setIds(contentIds: ContentId[]): PublishContentRequest {
            this.ids = contentIds;
            return this;
        }

        setExcludedIds(excludedIds: ContentId[]): PublishContentRequest {
            this.excludedIds = excludedIds;
            return this;
        }

        setIncludeChildren(includeChildren: boolean): PublishContentRequest {
            this.includeChildren = includeChildren;
            return this;
        }

        addId(contentId: ContentId): PublishContentRequest {
            this.ids.push(contentId);
            return this;
        }

        setPublishFrom(publishFrom: Date) {
            this.publishFrom = publishFrom;
        }

        setPublishTo(publishTo: Date) {
            this.publishTo = publishTo;
        }

        getParams(): Object {
            return {
                ids: this.ids.map((el) => {
                    return el.toString();
                }),
                excludedIds: this.excludedIds.map((el) => {
                    return el.toString();
                }),
                includeChildren: this.includeChildren,
                schedule: this.publishFrom ? {
                    from: this.publishFrom.toISOString(),
                    to: this.publishTo ? this.publishTo.toISOString() : undefined
                } : null
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "publish");
        }

        sendAndParse(): wemQ.Promise<api.task.TaskId> {
            return this.send().then((response: api.rest.JsonResponse<api.task.TaskIdJson>) => {
                return api.task.TaskId.fromJson(response.getResult());
            });
        }
    }
}