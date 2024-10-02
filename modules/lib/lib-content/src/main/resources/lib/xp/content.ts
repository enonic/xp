/**
 * Functions and constants to find and manipulate content.
 *
 * @example
 * var contentLib = require('/lib/xp/content');
 *
 * @module content
 */

declare global {
    interface XpLibraries {
        '/lib/xp/content': typeof import('./content');
    }

    // eslint-disable-next-line @typescript-eslint/no-empty-interface
    interface XpXData {}
}

import type {
    Aggregations,
    AggregationsResult,
    AggregationsToAggregationResults,
    ByteSource,
    Content,
    Filter,
    FormItem,
    Highlight,
    HighlightResult,
    PublishInfo,
    QueryDsl,
    ScriptValue,
    SortDsl,
} from '@enonic-types/core';

const isString = (value: unknown): value is string => value instanceof String || typeof value === 'string';

const isNumber = (value: unknown): value is number => typeof value === 'number' && isFinite(value);

function checkRequiredString<T extends object>(obj: T, name: keyof T): void {
    checkRequired(obj, name);
    if (!isString(obj[name])) {
        throw `Required parameter '${String(name)}' is not a string!`;
    }
}

function checkOptionalString<T extends object>(obj: T, name: keyof T): void {
    if (obj?.[name] != null && !isString(obj[name])) {
        throw `Optional parameter '${String(name)}' is not a string!`;
    }
}

function checkOptionalNumber<T extends object>(obj: T, name: keyof T): void {
    if (obj?.[name] != null && !isNumber(obj[name])) {
        throw `Optional parameter '${String(name)}' is not a number!`;
    }
}

export type {
    Aggregation,
    Aggregations,
    AggregationsResult,
    Attachment,
    BooleanDslExpression,
    BooleanFilter,
    Bucket,
    BucketsAggregationResult,
    BucketsAggregationsUnion,
    ByteSource,
    Component,
    Content,
    DateBucket,
    DateHistogramAggregation,
    DateRange,
    DateRangeAggregation,
    DistanceUnit,
    DslOperator,
    DslQueryType,
    ExistsDslExpression,
    ExistsFilter,
    FieldSortDsl,
    Filter,
    FormItem,
    FormItemInlineMixin,
    FormItemInput,
    FormItemLayout,
    FormItemOptionSet,
    FormItemSet,
    FulltextDslExpression,
    GeoDistanceAggregation,
    GeoDistanceSortDsl,
    GroupKey,
    HasValueFilter,
    Highlight,
    HighlightResult,
    HistogramAggregation,
    IdsFilter,
    InDslExpression,
    InputType,
    LikeDslExpression,
    MatchAllDslExpression,
    MaxAggregation,
    MinAggregation,
    NgramDslExpression,
    NotExistsFilter,
    NumericBucket,
    NumericRange,
    NumericRangeAggregation,
    PathMatchDslExpression,
    PublishInfo,
    QueryDsl,
    RangeDslExpression,
    Region,
    RoleKey,
    ScriptValue,
    SingleValueMetricAggregationResult,
    SingleValueMetricAggregationsUnion,
    SortDirection,
    SortDsl,
    StatsAggregation,
    StatsAggregationResult,
    StemmedDslExpression,
    TermDslExpression,
    TermsAggregation,
    UserKey,
    ValueCountAggregation,
    ValueType,
} from '@enonic-types/core';

type Attachments = Content['attachments'];

type ContentInheritType = Content['inherit'];

type Workflow = Content['workflow'];

export type Schedule = Omit<PublishInfo, 'first'>;

/* eslint-disable @typescript-eslint/no-unsafe-call, @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-explicit-any*/
declare const Java: any;

export const ARCHIVE_ROOT_PATH = Java.type('com.enonic.xp.archive.ArchiveConstants').ARCHIVE_ROOT_PATH as string;

export const CONTENT_ROOT_PATH = Java.type('com.enonic.xp.content.ContentConstants').CONTENT_ROOT_PATH as string;

/* eslint-enable @typescript-eslint/no-unsafe-call, @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-explicit-any */

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export interface GetContentParams {
    key: string;
    versionId?: string | null;
}

interface GetContentHandler {
    setKey(value?: string): void;

    setVersionId(value?: string | null): void;

    execute<Hit extends Content<unknown>>(): Hit | null;
}

interface GetAttachmentsHandler {
    setKey(value?: string | null): void;

    execute(): Attachments | null;
}

/**
 * This function fetches a content.
 *
 * @example-ref examples/content/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {string} [params.versionId] Version Id of the content.
 *
 * @returns {object} The content (as JSON) fetched from the repository.
 */
export function get<Hit extends Content<unknown> = Content>(params: GetContentParams): Hit | null {
    checkRequired(params, 'key');

    const bean: GetContentHandler = __.newBean<GetContentHandler>('com.enonic.xp.lib.content.GetContentHandler');

    bean.setKey(params.key);
    bean.setVersionId(__.nullOrValue(params.versionId));

    return __.toNativeObject(bean.execute<Hit>());
}

/**
 * This function returns a content attachments.
 *
 * @example-ref examples/content/getAttachments.js
 *
 * @param {string} key Path or id to the content.
 *
 * @returns {object} An object with all the attachments that belong to the content, where the key is the attachment name. Or null if the content cannot be found.
 */
export function getAttachments(key: string): Attachments | null {
    const bean: GetAttachmentsHandler = __.newBean<GetAttachmentsHandler>('com.enonic.xp.lib.content.GetAttachmentsHandler');
    bean.setKey(__.nullOrValue(key));
    return __.toNativeObject(bean.execute());
}

export interface GetAttachmentStreamParams {
    key: string;
    name: string;
}

interface GetAttachmentStreamHandler {
    setKey(value: string): void;

    setName(value: string): void;

    getStream(): ByteSource | null;
}

/**
 * This function returns a data-stream for the specified content attachment.
 *
 * @example-ref examples/content/getAttachmentStream.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {string} params.name Attachment name.
 *
 * @returns {*} Stream of the attachment data.
 */
export function getAttachmentStream(params: GetAttachmentStreamParams): ByteSource | null {
    checkRequired(params, 'key');
    checkRequired(params, 'name');

    const bean: GetAttachmentStreamHandler = __.newBean<GetAttachmentStreamHandler>('com.enonic.xp.lib.content.GetAttachmentStreamHandler');

    bean.setKey(params.key);
    bean.setName(params.name);

    return bean.getStream();
}

export interface AddAttachmentParam {
    key: string;
    name: string;
    mimeType: string;
    data: ByteSource;
    label?: string;
}

interface AddAttachmentHandler {
    setKey(value: string): void;

    setName(value: string): void;

    setMimeType(value: string): void;

    setData(value: ByteSource): void;

    setLabel(value?: string | null): void;

    execute(): void;
}

/**
 * Adds an attachment to an existing content.
 *
 * @example-ref examples/content/addAttachment.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {string} params.name Attachment name.
 * @param {string} params.mimeType Attachment content type.
 * @param {string} [params.label] Attachment label.
 * @param {object} params.data Stream with the binary data for the attachment.
 */
export function addAttachment(params: AddAttachmentParam): void {
    checkRequired(params, 'key');
    checkRequired(params, 'name');
    checkRequired(params, 'mimeType');
    checkRequired(params, 'data');

    const bean: AddAttachmentHandler = __.newBean<AddAttachmentHandler>('com.enonic.xp.lib.content.AddAttachmentHandler');

    bean.setKey(params.key);
    bean.setName(params.name);
    bean.setMimeType(params.mimeType);
    bean.setData(params.data);
    bean.setLabel(__.nullOrValue(params.label));

    bean.execute();
}

export interface RemoveAttachmentParams {
    key: string;
    name: string | string[];
}

interface RemoveAttachmentHandler {
    setKey(value: string): void;

    setName(value: string[]): void;

    execute(): void;
}

/**
 * Removes an attachment from an existing content.
 *
 * @example-ref examples/content/removeAttachment.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {string|string[]} params.name Attachment name, or array of names.
 */
export function removeAttachment(params: RemoveAttachmentParams): void {
    checkRequired(params, 'key');
    checkRequired(params, 'name');

    const {
        key,
        name = [],
    } = params ?? {};

    const bean: RemoveAttachmentHandler = __.newBean<RemoveAttachmentHandler>('com.enonic.xp.lib.content.RemoveAttachmentHandler');
    bean.setKey(key);
    bean.setName(([] as string[]).concat(name));
    bean.execute();
}

export interface SiteConfig<Config> {
    applicationKey: string;
    config: Config;
}

export type Site<Config> = Content<{
    description?: string;
    siteConfig: SiteConfig<Config> | SiteConfig<Config>[];
}, 'portal:site'>;

export interface GetSiteParams {
    key?: string | null;
}

interface GetSiteHandler {
    setKey(value?: string | null): void;

    execute<Config>(): Site<Config> | null;
}

/**
 * This function returns the parent site of a content.
 *
 * @example-ref examples/content/getSite.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 *
 * @returns {object} The current site as JSON.
 */
export function getSite<Config = Record<string, unknown>>(params: GetSiteParams): Site<Config> | null {
    checkRequired(params, 'key');

    const bean: GetSiteHandler = __.newBean<GetSiteHandler>('com.enonic.xp.lib.content.GetSiteHandler');
    bean.setKey(params.key);
    return __.toNativeObject(bean.execute());
}

export interface GetSiteConfigParams {
    key: string;
    applicationKey: string;
}

interface GetSiteConfigHandler {
    setKey(value?: string | null): void;

    setApplicationKey(value?: string | null): void;

    execute<Config>(): Config | null;
}

/**
 * This function returns the site configuration for this app in the parent site of a content.
 *
 * @example-ref examples/content/getSiteConfig.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {string} params.applicationKey Application key.
 *
 * @returns {object} The site configuration for current application as JSON.
 */
export function getSiteConfig<Config = Record<string, unknown>>(params: GetSiteConfigParams): Config | null {
    const bean: GetSiteConfigHandler = __.newBean<GetSiteConfigHandler>('com.enonic.xp.lib.content.GetSiteConfigHandler');

    bean.setKey(__.nullOrValue(params.key));
    bean.setApplicationKey(__.nullOrValue(params.applicationKey));

    return __.toNativeObject(bean.execute<Config>());
}

export interface DeleteContentParams {
    key: string;
}

interface DeleteContentHandler {
    setKey(value: string): void;

    execute(): boolean;
}

/**
 * This function deletes a content.
 *
 * @example-ref examples/content/delete.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 *
 * @returns {boolean} True if deleted, false otherwise.
 */
function _delete(params: DeleteContentParams): boolean {
    checkRequired(params, 'key');

    const bean: DeleteContentHandler = __.newBean<DeleteContentHandler>('com.enonic.xp.lib.content.DeleteContentHandler');
    bean.setKey(params.key);
    return bean.execute();
}

export {
    _delete as delete,
};

export interface ContentsResult<
    Hit extends Content<unknown>,
    AggregationOutput extends Record<string, AggregationsResult> | undefined = undefined
> {
    total: number;
    count: number;
    hits: Hit[];
    aggregations: AggregationOutput;
    highlight?: Record<string, HighlightResult>;
}

export interface GetChildContentParams {
    key: string;
    start?: number;
    count?: number;
    sort?: string;
}

interface GetChildContentHandler {
    setKey(value: string): void;

    setStart(value: number): void;

    setCount(value: number): void;

    setSort(value?: string | null): void;

    execute<
        Hit extends Content<unknown>,
        AggregationOutput extends Record<string, AggregationsResult>
    >(): ContentsResult<Hit, AggregationOutput>;
}

/**
 * This function fetches children of a content.
 *
 * @example-ref examples/content/getChildren.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the parent content.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {string} [params.sort] Sorting expression.
 *
 * @returns {Object} Result (of content) fetched from the repository.
 */
export function getChildren<
    Hit extends Content<unknown> = Content,
    AggregationOutput extends Record<string, AggregationsResult> = never
>(params: GetChildContentParams): ContentsResult<Hit, AggregationOutput> {
    checkRequired(params, 'key');

    const {
        key,
        start = 0,
        count = 10,
        sort,
    } = params ?? {};

    const bean: GetChildContentHandler = __.newBean<GetChildContentHandler>('com.enonic.xp.lib.content.GetChildContentHandler');
    bean.setKey(key);
    bean.setStart(start);
    bean.setCount(count);
    bean.setSort(__.nullOrValue(sort));
    return __.toNativeObject(bean.execute<Hit, AggregationOutput>());
}

export type IdGeneratorSupplier = (value: string) => string;

export interface CreateContentParams<Data, Type extends string> {
    name?: string;
    parentPath: string;
    displayName?: string;
    requireValid?: boolean;
    refresh?: boolean;
    contentType: Type;
    language?: string;
    childOrder?: string;
    data: Data;
    x?: XpXData;
    idGenerator?: IdGeneratorSupplier;
    workflow?: Workflow;
}

interface CreateContentHandler {
    setName(value?: string | null): void;

    setParentPath(value?: string | null): void;

    setDisplayName(value?: string | null): void;

    setContentType(value?: string | null): void;

    setRequireValid(value?: boolean | null): void;

    setRefresh(value?: boolean | null): void;

    setLanguage(value?: string | null): void;

    setChildOrder(value?: string | null): void;

    setData(value: ScriptValue): void;

    setX(value: ScriptValue): void;

    setWorkflow(value: ScriptValue): void;

    setIdGenerator(value?: IdGeneratorSupplier | string | null): void;

    execute<Data, Type extends string>(): Content<Data, Type>;
}

/**
 * This function creates a content.
 *
 * The parameter `name` is optional, but if it is not set then `displayName` must be specified. When name is not set, the
 * system will auto-generate a `name` based on the `displayName`, by lower-casing and replacing certain characters. If there
 * is already a content with the auto-generated name, a suffix will be added to the `name` in order to make it unique.
 *
 * To create a content where the name is not important and there could be multiple instances under the same parent content,
 * skip the `name` parameter and specify a `displayName`.
 *
 * @example-ref examples/content/create.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params.name] Name of content.
 * @param {string} params.parentPath Path to place content under.
 * @param {string} [params.displayName] Display name. Default is same as `name`.
 * @param {boolean} [params.requireValid=true] The content has to be valid, according to the content type, to be created. If requireValid=true and the content is not strictly valid, an error will be thrown.
 * @param {boolean} [params.refresh=true] If refresh is true, the created content will to be searchable through queries immediately, else within 1 second. Since there is a performance penalty doing this refresh, refresh should be set to false for bulk operations.
 * @param {string} params.contentType Content type to use.
 * @param {string} [params.language] The language tag representing the content’s locale.
 * @param {string} [params.childOrder] Default ordering of children when doing getChildren if no order is given in query
 * @param {object} params.data Actual content data.
 * @param {object} [params.x] eXtra data to use.
 * @param {object} [params.workflow] Workflow information to use. Default has state READY and empty check list.
 *
 * @returns {object} Content created as JSON.
 */
export function create<
    Data = Record<string, unknown>,
    Type extends string = string
>(params: CreateContentParams<Data, Type>): Content<Data, Type> {
    const {
        name,
        parentPath,
        displayName,
        requireValid = true,
        refresh = true,
        contentType,
        language,
        childOrder,
        data,
        x,
        idGenerator,
        workflow,
    } = params ?? {};

    const bean: CreateContentHandler = __.newBean<CreateContentHandler>('com.enonic.xp.lib.content.CreateContentHandler');

    bean.setName(__.nullOrValue(name));
    bean.setParentPath(__.nullOrValue(parentPath));
    bean.setDisplayName(__.nullOrValue(displayName));
    bean.setContentType(__.nullOrValue(contentType));
    bean.setRequireValid(__.nullOrValue(requireValid));
    bean.setRefresh(__.nullOrValue(refresh));
    bean.setLanguage(__.nullOrValue(language));
    bean.setChildOrder(__.nullOrValue(childOrder));

    bean.setData(__.toScriptValue(data));
    bean.setX(__.toScriptValue(x));

    bean.setIdGenerator(__.nullOrValue(idGenerator));
    bean.setWorkflow(__.toScriptValue(workflow));

    return __.toNativeObject(bean.execute<Data, Type>());
}

export interface QueryContentParams<AggregationInput extends Aggregations = never> {
    start?: number;
    count?: number;
    query?: QueryDsl | string;
    sort?: string | SortDsl | SortDsl[];
    filters?: Filter | Filter[];
    aggregations?: AggregationInput;
    contentTypes?: string[];
    highlight?: Highlight;
}

interface QueryContentHandler {
    setStart(value?: number | null): void;

    setCount(value?: number | null): void;

    setQuery(value: ScriptValue): void;

    setSort(value: ScriptValue): void;

    setAggregations(value: ScriptValue): void;

    setContentTypes(value: ScriptValue): void;

    setFilters(value: ScriptValue): void;

    setHighlight(value: ScriptValue): void;

    execute<
        Hit extends Content<unknown>,
        AggregationInput extends Aggregations = never
    >(): ContentsResult<Hit, AggregationsToAggregationResults<AggregationInput>>;
}

/**
 * This command queries content.
 *
 * @example-ref examples/content/query.js
 *
 * @param {object} params JSON with the parameters.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {string|object} params.query Query expression.
 * @param {object|object[]} [params.filters] Filters to apply to query result
 * @param {string|object|object[]} [params.sort] Sorting expression.
 * @param {object} [params.aggregations] Aggregations expression.
 * @param {string[]} [params.contentTypes] Content types to filter on.
 *
 * @returns {object} Result of query.
 */

export function query<
    Hit extends Content<unknown> = Content,
    AggregationInput extends Aggregations = never
>(params: QueryContentParams<AggregationInput>): ContentsResult<Hit, AggregationsToAggregationResults<AggregationInput>> {
    const bean: QueryContentHandler = __.newBean<QueryContentHandler>('com.enonic.xp.lib.content.QueryContentHandler');

    bean.setStart(params.start);
    bean.setCount(params.count);
    bean.setQuery(__.toScriptValue((params.query)));
    bean.setSort(__.toScriptValue(params.sort));
    bean.setAggregations(__.toScriptValue(params.aggregations));
    bean.setContentTypes(__.toScriptValue(params.contentTypes));
    bean.setFilters(__.toScriptValue(params.filters));
    bean.setHighlight(__.toScriptValue(params.highlight));

    return __.toNativeObject(bean.execute<Hit, AggregationInput>());
}

export interface ModifyContentParams<Data, Type extends string> {
    key: string;
    editor: (v: Content<Data, Type>) => Content<Data, Type>;
    requireValid?: boolean;
}

interface ModifyContentHandler {
    setKey(value: string): void;

    setEditor(value: ScriptValue): void;

    setRequireValid(value: boolean): void;

    execute<Data, Type extends string>(): Content<Data, Type> | null;
}

/**
 * This function modifies a content.
 *
 * @example-ref examples/content/modify.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {function} params.editor Editor callback function.
 * @param {boolean} [params.requireValid=true] The content has to be valid, according to the content type, to be updated. If requireValid=true and the content is not strictly valid, an error will be thrown.
 *
 * @returns {object} Modified content as JSON.
 */
export function modify<Data = Record<string, unknown>, Type extends string = string>(params: ModifyContentParams<Data, Type>): Content<Data, Type> | null {
    checkRequired(params, 'key');

    const {
        key,
        editor,
        requireValid = true,
    } = params ?? {};

    const bean: ModifyContentHandler = __.newBean<ModifyContentHandler>('com.enonic.xp.lib.content.ModifyContentHandler');

    bean.setKey(key);
    bean.setEditor(__.toScriptValue(editor));
    bean.setRequireValid(requireValid);

    return __.toNativeObject(bean.execute<Data, Type>());
}

export interface PublishContentParams {
    keys: string[];
    schedule?: Schedule;
    includeChildren?: boolean;
    excludeChildrenIds?: string[];
    includeDependencies?: boolean;
    message?: string;
}

export interface PublishContentResult {
    pushedContents: string[];
    deletedContents: string[];
    failedContents: string[];
}

interface PublishContentHandler {
    setKeys(value: string[]): void;

    setContentPublishInfo(value: ScriptValue): void;

    setExcludeChildrenIds(value: string[]): void;

    setIncludeChildren(value?: boolean): void;

    setIncludeDependencies(value?: boolean): void;

    setMessage(value?: string | null): void;

    execute(): PublishContentResult;
}

/**
 * This function publishes content from the draft branch to the master branch.
 *
 * @example-ref examples/content/publish.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string[]} params.keys List of all content keys(path or id) that should be published.
 * to another, and publishing user content from master to draft is therefore also valid usage of this function, which may be practical if user input to a web-page is stored on master.
 * @param {object} [params.schedule] Schedule the publish.
 * @param {string} [params.schedule.from] Time from which the content is considered published. Defaults to the time of the publish
 * @param {string} [params.schedule.to] Time until which the content is considered published.
 * @param {string[]} [params.excludeChildrenIds] List of all content keys which children should be excluded from publishing content.
 * @param {boolean} [params.includeDependencies=true] Whether all related content should be included when publishing content.
 * @param {string} [params.message] Publish message.
 *
 * @returns {object} Status of the publish operation in JSON.
 */
export function publish(params: PublishContentParams): PublishContentResult {
    checkRequired(params, 'keys');

    const bean: PublishContentHandler = __.newBean<PublishContentHandler>('com.enonic.xp.lib.content.PublishContentHandler');
    bean.setKeys(params.keys);
    if (params.schedule) {
        bean.setContentPublishInfo(__.toScriptValue(params.schedule));
    }
    if (params.excludeChildrenIds) {
        bean.setExcludeChildrenIds(params.excludeChildrenIds);
    }
    if (!__.nullOrValue(params.includeChildren)) {
        // keep for backwards compatibility
        bean.setIncludeChildren(params.includeChildren);
    }
    if (!__.nullOrValue(params.includeDependencies)) {
        bean.setIncludeDependencies(params.includeDependencies);
    }
    bean.setMessage(__.nullOrValue(params.message));
    return __.toNativeObject(bean.execute());
}

export interface UnpublishContentParams {
    keys: string[];
}

interface UnpublishContentHandler {
    setKeys(value: string[]): void;

    execute(): string[];
}

/**
 * This function unpublishes content that had been published to the master branch.
 *
 * @example-ref examples/content/unpublish.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string[]} params.keys List of all content keys(path or id) that should be unpublished.
 *
 * @returns {string[]} List with ids of the content that were unpublished.
 */
export function unpublish(params: UnpublishContentParams): string[] {
    checkRequired(params, 'keys');

    const bean: UnpublishContentHandler = __.newBean<UnpublishContentHandler>('com.enonic.xp.lib.content.UnpublishContentHandler');
    bean.setKeys(params.keys);
    return __.toNativeObject(bean.execute());
}


export interface ContentExistsParams {
    key: string;
}

interface ContentExistsHandler {
    setKey(value: string): void;

    execute(): boolean;
}

/**
 * Check if content exists.
 *
 * @example-ref examples/content/exists.js
 *
 * @param {string} params.key content id.
 *
 * @returns {boolean} True if exist, false otherwise.
 */
export function exists(params: ContentExistsParams): boolean {
    checkRequired(params, 'key');

    const bean: ContentExistsHandler = __.newBean<ContentExistsHandler>('com.enonic.xp.lib.content.ContentExistsHandler');

    bean.setKey(params.key);

    return __.toNativeObject(bean.execute());
}

export interface CreateMediaParams {
    name: string;
    parentPath?: string;
    mimeType?: string;
    focalX?: number;
    focalY?: number;
    data: ByteSource;
    idGenerator?: (v: string) => string;
}

interface CreateMediaHandler {
    setName(value: string): void;

    setParentPath(value?: string | null): void;

    setMimeType(value?: string | null): void;

    setFocalX(value?: number): void;

    setFocalY(value?: number): void;

    setData(value?: ByteSource | null): void;

    setIdGenerator(value?: IdGeneratorSupplier | null): void;

    execute<Data, Type extends string>(): Content<Data, Type>;
}

/**
 * Creates a media content.
 *
 * @example-ref examples/content/createMedia.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Name of content.
 * @param {string} [params.parentPath=/] Path to place content under.
 * @param {string} [params.mimeType] Mime-type of the data.
 * @param {number} [params.focalX=0.5] Focal point for X axis (if it's an image).
 * @param {number} [params.focalY=0.5] Focal point for Y axis (if it's an image).
 * @param  params.data Data (as stream) to use.
 *
 * @returns {object} Returns the created media content.
 */
export function createMedia<Data = Record<string, unknown>, Type extends string = string>(params: CreateMediaParams): Content<Data, Type> {
    checkRequired(params, 'name');

    const bean: CreateMediaHandler = __.newBean<CreateMediaHandler>('com.enonic.xp.lib.content.CreateMediaHandler');

    bean.setName(params.name);
    bean.setParentPath(__.nullOrValue(params.parentPath));
    bean.setMimeType(__.nullOrValue(params.mimeType));
    bean.setData(__.nullOrValue(params.data));
    bean.setIdGenerator(__.nullOrValue(params.idGenerator));

    if (params.focalX) {
        bean.setFocalX(params.focalX);
    }
    if (params.focalY) {
        bean.setFocalY(params.focalY);
    }

    return __.toNativeObject(bean.execute<Data, Type>());
}

export interface MoveContentParams {
    source: string;
    target: string;
}

interface MoveContentHandler {
    setSource(value: string): void;

    setTarget(value: string): void;

    execute<Data, Type extends string>(): Content<Data, Type>;
}

/**
 * Rename a content or move it to a new path.
 *
 * @example-ref examples/content/move.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.source Path or id of the content to be moved or renamed.
 * @param {string} params.target New path or name for the content. If the target ends in slash '/', it specifies the parent path where to be moved. Otherwise it means the new desired path or name for the content.
 *
 * @returns {object} The content that was moved or renamed.
 */
export function move<Data = Record<string, unknown>, Type extends string = string>(params: MoveContentParams): Content<Data, Type> {
    checkRequired(params, 'source');
    checkRequired(params, 'target');

    const bean: MoveContentHandler = __.newBean<MoveContentHandler>('com.enonic.xp.lib.content.MoveContentHandler');

    bean.setSource(params.source);
    bean.setTarget(params.target);

    return __.toNativeObject(bean.execute<Data, Type>());
}

export interface ArchiveContentParams {
    content: string;
}

interface ArchiveContentHandler {
    setContent(value: string): void;

    execute(): string[];
}

/**
 * Archive a content.
 *
 * @example-ref examples/content/archive.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.content Path or id of the content to be archived.
 *
 * @returns {string[]} List with ids of the contents that were archived.
 */
export function archive(params: ArchiveContentParams): string[] {
    checkRequired(params, 'content');

    const bean: ArchiveContentHandler = __.newBean<ArchiveContentHandler>('com.enonic.xp.lib.content.ArchiveContentHandler');
    bean.setContent(params.content);
    return __.toNativeObject(bean.execute());
}

export interface RestoreContentParams {
    content: string;
    path: string;
}

interface RestoreContentHandler {
    setContent(value: string): void;

    setPath(value?: string | null): void;

    execute(): string[];
}

/**
 * Restore a content from the archive.
 *
 * @example-ref examples/content/restore.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.content Path or id of the content to be restored.
 * @param {string} params.path Path of parent for restored content.
 *
 * @returns {string[]} List with ids of the contents that were restored.
 */
export function restore(params: RestoreContentParams): string[] {
    checkRequired(params, 'content');

    const bean: RestoreContentHandler = __.newBean<RestoreContentHandler>('com.enonic.xp.lib.content.RestoreContentHandler');
    bean.setContent(params.content);
    bean.setPath(__.nullOrValue(params.path));
    return __.toNativeObject(bean.execute());
}

export type Permission = 'READ' | 'CREATE' | 'MODIFY' | 'DELETE' | 'PUBLISH' | 'READ_PERMISSIONS' | 'WRITE_PERMISSIONS';

export interface AccessControlEntry {
    principal: string;
    allow?: Permission[];
    deny?: Permission[];
}

/**
 * @deprecated Use the new {@link ApplyPermissionsParams} interface instead.
 */
export interface SetPermissionsParams {
    key: string;
    inheritPermissions?: boolean;
    overwriteChildPermissions?: boolean;
    permissions?: AccessControlEntry[];
}

export interface ApplyPermissionsParams {
    key: string;
    scope?: 'SINGLE' | 'TREE' | 'CHILDREN';
    permissions?: AccessControlEntry[];
    addPermissions?: AccessControlEntry[];
    removePermissions?: AccessControlEntry[];
}

export interface ApplyPermissionsResult {
    [nodeId: string]: BranchResult[];
}

export interface BranchResult {
    branch: string;
    content: Content;
}

export interface Permissions {
    permissions?: AccessControlEntry[];
}

interface ApplyPermissionsHandler {
    setKey(value: string): void;

    setScope(value: string): void;

    setPermissions(value: ScriptValue): void;

    setAddPermissions(value: ScriptValue): void;

    setRemovePermissions(value: ScriptValue): void;

    execute(): ApplyPermissionsResult;
}

/**
 * Sets permissions on a content.
 *
 * @deprecated Please use {@link applyPermissions}.
 *
 * @example-ref examples/content/setPermissions.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or id of the content.
 * @param {boolean} [params.inheritPermissions] Set to true if the content must inherit permissions. Default to false.
 * @param {boolean} [params.overwriteChildPermissions] Set to true to overwrite child permissions. Default to false.
 * @param {array} [params.permissions] Array of permissions.
 * @param {string} params.permissions.principal Principal key.
 * @param {array} params.permissions.allow Allowed permissions.
 * @param {array} params.permissions.deny Denied permissions.
 * @returns {boolean} True if successful, false otherwise.
 */
export function setPermissions(params: SetPermissionsParams): boolean {
    const bean: ApplyPermissionsHandler = __.newBean<ApplyPermissionsHandler>('com.enonic.xp.lib.content.ApplyPermissionsHandler');

    if (params.key) {
        bean.setKey(params.key);
    }
    if (params.permissions) {
        bean.setPermissions(__.toScriptValue(params.permissions));
    }
    const result: ApplyPermissionsResult = bean.execute();

    for (const nodeId in result) {
        const branchResults: BranchResult[] = result.nodeId;
        for (const branchResult of branchResults) {
            if (branchResult.content !== null) {
                return true;
            }
        }
    }

    return false;
}

/**
 * Applies permissions to a content.
 *
 * @example-ref examples/content/applyPermissions.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or id of the content.
 * @param {string} [params.scope] Scope of operation. Possible values are 'SINGE', 'TREE' or 'CHILDREN'. Default is 'SINGLE'.
 * @param {array} [params.permissions] Array of permissions. Cannot be used together with addPermissions and removePermissions.
 * @param {string} params.permissions.principal Principal key.
 * @param {array} params.permissions.allow Allowed permissions.
 * @param {array} params.permissions.deny Denied permissions.
 * @param {array} [params.addPermissions] Array of permissions to add. Cannot be used together with permissions.
 * @param {array} [params.removePermissions] Array of permissions to remove. Cannot be used together with permissions.
 *
 * @returns {object} Result of the apply permissions operation.
 */
export function applyPermissions(params: ApplyPermissionsParams): ApplyPermissionsResult {
    const bean: ApplyPermissionsHandler = __.newBean<ApplyPermissionsHandler>('com.enonic.xp.lib.content.ApplyPermissionsHandler');

    if (params.key) {
        bean.setKey(params.key);
    }
    if (params.scope) {
        bean.setScope(params.scope);
    }
    if (params.permissions) {
        bean.setPermissions(__.toScriptValue(params.permissions));
    }
    if (params.addPermissions) {
        bean.setAddPermissions(__.toScriptValue(params.addPermissions));
    }
    if (params.removePermissions) {
        bean.setRemovePermissions(__.toScriptValue(params.removePermissions));
    }

    return __.toNativeObject(bean.execute());
}

export interface GetPermissionsParams {
    key: string;
}

interface GetPermissionsHandler {
    setKey(key: string): void;

    execute(): Permissions | null;
}

/**
 * Gets permissions on a content.
 *
 * @example-ref examples/content/getPermissions.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or id of the content.
 * @returns {object} Content permissions.
 */
export function getPermissions(params: GetPermissionsParams): Permissions | null {
    const bean: GetPermissionsHandler = __.newBean<GetPermissionsHandler>('com.enonic.xp.lib.content.GetPermissionsHandler');

    if (params.key) {
        bean.setKey(params.key);
    }
    return __.toNativeObject(bean.execute());
}

export interface Icon {
    data: ByteSource;
    mimeType: string;
    modifiedTime: string;
}

/**
 * @typedef ContentType
 * @type Object
 * @property {string} name Name of the content type.
 * @property {string} displayName Display name of the content type.
 * @property {string} description Description of the content type.
 * @property {string} superType Name of the super type, or null if it has no super type.
 * @property {boolean} abstract Whether or not content of this type may be instantiated.
 * @property {boolean} final Whether or not it may be used as super type of other content types.
 * @property {boolean} allowChildContent Whether or not allow creating child items on content of this type.
 * @property {string} displayNameExpression ES6 string template for generating the content name based on values in the content form.
 * @property {object} [icon] Icon of the content type.
 * @property {object} [icon.data] Stream with the binary data for the icon.
 * @property {string} [icon.mimeType] Mime type of the icon image.
 * @property {string} [icon.modifiedTime] Modified time of the icon. May be used for caching.
 * @property {object[]} form Form schema represented as an array of form items: Input, ItemSet, Layout, OptionSet.
 */
export interface ContentType {
    name: string;
    displayName: string;
    description: string;
    superType: string;
    abstract: boolean;
    final: boolean;
    allowChildContent: boolean;
    displayNameExpression: string;
    modifiedTime: string;
    icon?: Icon;
    form: FormItem[];
}

interface ContentTypeHandler {
    setName(value?: string | null): void;

    getContentType(): ContentType | null;

    getAllContentTypes(): ContentType[];
}

/**
 * Returns the properties and icon of the specified content type.
 *
 * @example-ref examples/content/getType.js
 *
 * @param name Name of the content type, as 'app:name' (e.g. 'com.enonic.myapp:article').
 * @returns {ContentType} The content type object if found, or null otherwise. See ContentType type definition below.
 */
export function getType(name: string): ContentType | null {
    const bean: ContentTypeHandler = __.newBean<ContentTypeHandler>('com.enonic.xp.lib.content.ContentTypeHandler');
    bean.setName(__.nullOrValue(name));
    return __.toNativeObject(bean.getContentType());
}

/**
 * Returns the list of all the content types currently registered in the system.
 *
 * @example-ref examples/content/getTypes.js
 *
 * @returns {ContentType[]} Array with all the content types found. See ContentType type definition below.
 */
export function getTypes(): ContentType[] {
    const bean: ContentTypeHandler = __.newBean<ContentTypeHandler>('com.enonic.xp.lib.content.ContentTypeHandler');
    return __.toNativeObject(bean.getAllContentTypes());
}

export interface GetOutboundDependenciesParams {
    key: string;
}

interface GetOutboundDependenciesHandler {
    setKey(value: string): void;

    execute(): string[];
}

/**
 * Returns outbound dependencies on a content.
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or id of the content.
 * @returns {object} Content Ids.
 */
export function getOutboundDependencies(params: GetOutboundDependenciesParams): string[] {
    checkRequired(params, 'key');

    const bean: GetOutboundDependenciesHandler = __.newBean<GetOutboundDependenciesHandler>('com.enonic.xp.lib.content.GetOutboundDependenciesHandler');

    bean.setKey(params.key);

    return __.toNativeObject(bean.execute());
}

export interface ResetInheritanceParams {
    key: string;
    projectName: string;
    inherit: ContentInheritType[];
}

export interface ResetInheritanceHandler {
    setKey(value: string): void;

    setProjectName(value: string): void;

    setInherit(value: ContentInheritType[]): void;

    execute(): void;
}

/** Resets dropped inherit flags back.
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or id of the content.
 * @param {string} params.projectName name of project with content.
 * @param {string[]} params.inherit flags to be reset.
 */
export function resetInheritance(params: ResetInheritanceParams): void {
    checkRequired(params, 'key');
    checkRequired(params, 'projectName');
    checkRequired(params, 'inherit');

    const bean: ResetInheritanceHandler = __.newBean<ResetInheritanceHandler>('com.enonic.xp.lib.content.ResetInheritanceHandler');

    bean.setKey(params.key);
    bean.setProjectName(params.projectName);
    bean.setInherit(params.inherit);

    bean.execute();
}

export interface ModifyMediaParams {
    key: string;
    name: string;
    data: ByteSource;
    artist?: string | string[];
    caption?: string;
    copyright?: string;
    focalX?: number;
    focalY?: number;
    mimeType?: string;
    tags?: string | string[];
    workflow?: Workflow;
}

interface ModifyMediaHandler {
    setKey(value: string): void;

    setName(value: string): void;

    setData(value: ByteSource): void;

    setFocalX(value: number): void;

    setFocalY(value: number): void;

    setArtist(value: string[]): void;

    setCaption(value?: string | null): void;

    setCopyright(value?: string | null): void;

    setMimeType(value?: string | null): void;

    setTags(value: string[]): void;

    setWorkflow(value: ScriptValue): void;

    execute<Data, Type extends string>(): Content<Data, Type>;
}

/** This function modifies a media content.
 *
 * @example-ref examples/content/modifyMedia.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {string} params.name Name to the content.
 * @param {function} params.data Data (as stream) to use.
 * @param {string} [params.mimeType] Mime-type of the data.
 * @param {string|string[]} [params.artist] Artist to the content.
 * @param {string} [params.caption] Caption to the content.
 * @param {string} [params.copyright] Copyright to the content.
 * @param {string|string[]} [params.tags] Tags to the content.
 * @param {object} [params.workflow] Workflow information to use. Default has state READY and empty check list.
 * @param {number} [params.focalX=0.5] Focal point for X axis (if it's an image).
 * @param {number} [params.focalY=0.5] Focal point for Y axis (if it's an image).
 *
 * @returns {object} Modified content as JSON.
 */
export function modifyMedia<Data = Record<string, unknown>, Type extends string = string>(params: ModifyMediaParams): Content<Data, Type> | null {
    checkRequired(params, 'data');
    checkRequiredString(params, 'key');
    checkRequiredString(params, 'name');
    checkOptionalString(params, 'caption');
    checkOptionalString(params, 'copyright');
    checkOptionalString(params, 'mimeType');
    checkOptionalNumber(params, 'focalX');
    checkOptionalNumber(params, 'focalY');

    const {
        data,
        key,
        name,
        caption,
        copyright,
        mimeType,
        focalX,
        focalY,
        workflow,
        artist = [],
        tags = [],
    } = params;

    const bean: ModifyMediaHandler = __.newBean<ModifyMediaHandler>('com.enonic.xp.lib.content.ModifyMediaHandler');

    bean.setKey(key);
    bean.setName(name);
    bean.setData(data);
    bean.setCaption(__.nullOrValue(caption));
    bean.setCopyright(__.nullOrValue(copyright));
    bean.setMimeType(__.nullOrValue(mimeType));
    bean.setWorkflow(__.toScriptValue(workflow));
    bean.setArtist(([] as string[]).concat(artist));
    bean.setTags(([] as string[]).concat(tags));

    if (focalX != null) {
        bean.setFocalX(focalX);
    }
    if (focalY != null) {
        bean.setFocalY(focalY);
    }

    return __.toNativeObject(bean.execute());
}

export interface DuplicateContentParams {
    contentId: string;
    workflow?: Workflow;
    includeChildren?: boolean;
    variant?: boolean;
    parent?: string;
    name?: string;
}

export interface DuplicateContentsResult {
    contentName: string;
    sourceContentPath: string;
    duplicatedContents: string[];
}

interface DuplicateContentHandler {
    setContentId(value: string): void;

    setWorkflow(value: ScriptValue): void;

    setIncludeChildren(value: boolean): void;

    setVariant(value: boolean): void;

    setName(value?: string): void;

    setParentPath(value?: string): void;

    execute(): DuplicateContentsResult;
}

/** This function duplicates a content.
 *
 * @example-ref examples/content/duplicate.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.contentId Id to the content.
 * @param {object} [params.workflow] Workflow information to use. Default has state READY and empty check list.
 * @param {boolean} [params.includeChildren=true] Indicates that children contents must be duplicated, too. Default value `true`. Ignored if `variant=true`.
 * @param {boolean} [params.variant=false] Indicates that duplicated content is a variant. Default value `false`.
 * @param {string} [params.parent] Destination parent path. By default, a duplicated content will be added as a sibling of the source content.
 * @param {string} [params.name] New content name.
 *
 * @returns {object} summary about duplicated content.
 */
export function duplicate(params: DuplicateContentParams): DuplicateContentsResult {
    checkRequired(params, 'contentId');

    const {
        contentId,
        workflow,
        includeChildren = true,
        variant = false,
        parent,
        name,
    } = params ?? {};

    const bean: DuplicateContentHandler = __.newBean<DuplicateContentHandler>('com.enonic.xp.lib.content.DuplicateContentHandler');

    bean.setContentId(contentId);
    bean.setWorkflow(__.toScriptValue(workflow));
    if (name != null) {
        bean.setName((name));
    }
    if (parent != null) {
        bean.setParentPath(parent);
    }
    bean.setIncludeChildren(includeChildren);
    bean.setVariant(variant);

    return __.toNativeObject(bean.execute());
}
