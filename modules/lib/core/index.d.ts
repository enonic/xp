export type ComponentDescriptor = `${string}:${string}`;

export interface NestedRecord {
	[name: PropertyKey]: NestedRecord | unknown
}

declare global {
    interface XpBeans {}
    interface XpLayoutMap {
        [layoutDescriptor: ComponentDescriptor]: NestedRecord;
    }
    interface XpLibraries {}
    interface XpPageMap {
        [pageDescriptor: ComponentDescriptor]: NestedRecord;
    }
    interface XpPartMap {
        [partDescriptor: ComponentDescriptor]: NestedRecord;
    }
    interface XpMixin {
        [key: string]: Record<string, Record<string, unknown>>;
    }
}

export interface App {
    /**
     * The name of the application.
     *
     * @type string
     */
    name: string;
    /**
     * Version of the application.
     *
     * @type string
     */
    version: string;
    /**
     * Values from the application’s configuration file.
     * This can be set using $XP_HOME/config/<app.name>.cfg.
     * Every time the configuration is changed the app is restarted.
     *
     * @type Object
     */
    config: Record<string, string | undefined>;
}

export interface DoubleUnderscore {
    /**
     * Creates a new JavaScript bean that wraps the given Java class and makes its methods available to be called from JavaScript.
     */
    newBean: NewBean;
    /**
     * Converts arrays or complex Java objects to JSON.
     * @param value Value to convert
     */
    toNativeObject: <T = unknown>(value: T) => T;
    /**
     * Converts JSON to a Java Map structure that can be used as parameters to a Java method on a bean created with newBean.
     * @param value Value to convert
     */
    toScriptValue: <T = object>(value: T) => ScriptValue;
    /**
     * Add a disposer that is called when the app is stopped.
     * @param callback Function to call
     */
    disposer: (callback: (...args: unknown[]) => unknown) => void;
    /**
     * Converts a JavaScript variable that is undefined to a Java <code>null</code> object.
     * If the JavaScript variable is defined, it is returned as is.
     * @param value Value to convert
     */
    nullOrValue: <T = object>(value: T) => T | null | undefined;

    /**
     * Doc registerMock.
     *
     * @param name Name of mock.
     * @param value Value to register.
     */
    registerMock: (name: string, value: object) => void
}

export interface Log {
    /**
     * Log debug message.
     *
     * @param {Array} args... logging arguments.
     */
    debug: (...args: unknown[]) => void;

    /**
     * Log info message.
     *
     * @param {Array} args... logging arguments.
     */
    info: (...args: unknown[]) => void;

    /**
     * Log warning message.
     *
     * @param {Array} args... logging arguments.
     */
    warning: (...args: unknown[]) => void;

    /**
     * Log error message.
     *
     * @param {Array} args... logging arguments.
     */
    error: (...args: unknown[]) => void;
}

export type NewBean = <T = unknown, Bean extends keyof XpBeans | string = string>(bean: Bean) =>
    Bean extends keyof XpBeans ? XpBeans[Bean] : T;

export type Resolve = (path: string) => ResourceKey;

export interface ScriptValue {
    isArray(): boolean;

    isObject(): boolean;

    isValue(): boolean;

    isFunction(): boolean;

    getValue(): unknown;

    getKeys(): string[];

    hasMember(key: string): boolean;

    getMember(key: string): ScriptValue;

    getArray(): ScriptValue[];

    getMap(): Record<string, unknown>;

    getList(): object[];
}

export type XpRequire = <Key extends keyof XpLibraries | string = string>(path: Key) =>
    Key extends keyof XpLibraries ? XpLibraries[Key] : unknown;

export type LiteralUnion<T extends U, U = string> = T | (U & Record<never, never>);

export type Merge<
    A,
    B = Record<string, never>
> = B extends Record<string, never>
    ? A
    : Pick<A, Exclude<keyof A, keyof B>> & B;

export interface ComplexCookie {
    /**
     * The value of the cookie (optional).
     *
     * @type string
     */
    value: string;
    /**
     * A comment (rfc2109) to document the cookie (optional).
     *
     * @type string
     */
    comment?: string;
    /**
     * The expiration date and time for the cookie (optional).
     *
     * @type string
     */
    expires?: Date;
    /**
     * The domain name for which the cookie is set.
     *
     * @type string
     */
    domain?: string;
    /**
     *  Indicates whether the cookie should not be accessible via JavaScript (optional).
     *
     * @type string
     */
    httpOnly?: boolean;
    /**
     * The maximum age of the cookie in seconds (optional).
     *
     * @type string
     */
    maxAge?: number;
    /**
     * The path on the server where the cookie should be available.
     *
     * @type string
     */
    path?: string;
    /**
     *  Specifies the SameSite attribute (draft RFC) for the cookie (optional).
     *
     * @type string
     */
    sameSite?: LiteralUnion<'lax' | 'strict' | 'none'>;
    /**
     * Indicates whether the cookie should only be sent over HTTPS (optional).
     *
     * @type string
     */
    secure?: boolean;
}

export type RequestBranch = 'draft' | 'master';
export type RequestGetHeaderFunction = (headerName: string) => string | null;
export type RequestMethod = 'GET' | 'POST' | ' HEAD' | 'OPTIONS' | ' PUT' | 'DELETE' | 'PATCH' | 'TRACE' | 'CONNECT';
export type RequestMode = 'edit' | 'inline' | 'live' | 'preview' | 'admin';
export type RequestParams = Record<string, string | string[]>;
export type RequestScheme = 'http' | 'https';

export type RequestCookies = Record<string, string | undefined>;
export type ResponseCookies = Record<string, string | ComplexCookie | undefined>;

export type RequestHeaders = Record<string, string | undefined>;
export type ResponseHeaders = Record<string, string | number | (string | number)[] | undefined>;

export interface DefaultRequestCookies extends RequestCookies {
    JSESSIONID?: string;
}

export type SecFetchDest = 'audio' | 'audioworklet' | 'document' | 'embed' | 'empty' | 'fencedframe' | 'font' | 'frame' | 'iframe' | 'image' | 'manifest' | 'object' | 'paintworklet' | 'report' | 'script' | 'serviceworker' | 'sharedworker' | 'style' | 'track' | 'video' | 'webidentity' | 'worker' | 'xslt';
export type SecFetchMode = 'cors'| 'navigate' | 'no-cors' | 'same-origin' | 'websocket';
export type SecFetchSite = 'cross-site' | 'same-origin' | 'same-site' | 'none';

export interface DefaultRequestHeaders extends RequestHeaders {
    Accept?: string;
    'Accept-Charset'?: string;
    'Accept-Encoding'?: string;
    'Accept-Language'?: string;
    Authorization?: string;
    'Cache-Control'?: string;
    Connection?: string;
    'Content-Length'?: string;
    'Content-Type'?: string;
    Cookie?: string;
    Host?: string;
    'If-None-Match'?: string;
    Priority?: string;
    Referer?: string;
    'Sec-Fetch-Dest'?: LiteralUnion<SecFetchDest>;
    'Sec-Fetch-Mode'?: LiteralUnion<SecFetchMode>;
    'Sec-Fetch-Site'?: LiteralUnion<SecFetchSite>
    'Sec-Fetch-User'?: '?1';
    'Upgrade-Insecure-Requests'?: '1';
    'User-Agent'?: string;
    'X-Forwarded-For'?: string;
    'X-Forwarded-Host'?: string;
    'X-Forwarded-Proto'?: LiteralUnion<RequestScheme>;
    'X-Forwarded-Server'?: string;
}

export interface RequestConstructorParams {
    body?: string;
    branch?: LiteralUnion<RequestBranch>;
    contentType?: string;
    contextPath?: string;
    cookies: RequestCookies;
    headers: RequestHeaders;
    host: string;
    method: LiteralUnion<RequestMethod>;
    mode: LiteralUnion<RequestMode>;
    params: RequestParams;
    path: string;
    port: number;
    rawPath: string;
    remoteAddress: string;
    repositoryId?: string;
    scheme: LiteralUnion<RequestScheme>;
    url: string;
    validTicket?: boolean;
    webSocket: boolean;
}

export interface RequestInterface extends RequestConstructorParams {
    getHeader: RequestGetHeaderFunction;
}

export interface DefaultRequest extends RequestInterface {
    cookies: DefaultRequestCookies;
    headers: DefaultRequestHeaders;
}

export type Request<
    T extends Partial<RequestInterface> = Record<string, never>
> = Merge<DefaultRequest, T>;

export type SerializableRequest<T extends RequestInterface = DefaultRequest> = Omit<
    Partial<T>,
    'body' | 'contextPath' | 'rawPath' | 'repositoryId' | 'webSocket'
> & {
    body?: unknown[] | Record<string, unknown> | boolean | number | string | null;
};

export interface DefaultResponseHeaders extends ResponseHeaders {
    'Cache-Control'?: string;
    'Content-Encoding'?: string;
    'Content-Type'?: string;
    'Content-Security-Policy'?: string;
    'Date'?: string;
    Etag?: string | number;
    Location?: string;
}

// NOTE Even though PortalResponseSerializer allows non-array values,
// when it comes back from Java the PortalResponseMapper will always return an array.
// So perhaps it's better to enforce that, so it's consistent both ways.
// It also causes problems in ResponseProcessors, since they work with both from and to Java.
export interface PageContributions {
	headBegin?: string[];
	headEnd?: string[];
	bodyBegin?: string[];
	bodyEnd?: string[];
}

export type ResponseBody = unknown[] | Record<string, unknown> | boolean | number | string | null | ByteSource;

export interface MappedResponse {
    applyFilters: boolean;
    body?: ResponseBody;
    contentType: string;
    cookies: ResponseCookies;
    headers: ResponseHeaders;
    pageContributions: PageContributions;
    postProcess: boolean;
    status: number;
}

export interface ResponseInterface extends Partial<MappedResponse> {
    redirect?: string;
}

export interface DefaultResponse extends ResponseInterface {
    contentType?: LiteralUnion<'text/html' | 'application/json'>;
    headers?: DefaultResponseHeaders;
}

export type Response<
    T extends Partial<ResponseInterface> = Record<string, never>
> = Merge<DefaultResponse, T>;

export type RequestHandler<
    RequestFromJava extends RequestInterface = DefaultRequest,
    ResponseToJava extends ResponseInterface = DefaultResponse
> = (request: RequestFromJava) => ResponseToJava;

type AutoLoginRequestHandler<
    RequestFromJava extends RequestInterface = DefaultRequest
> = (request: RequestFromJava) => void;

export type HttpFilterNext<
    RequestToJava extends SerializableRequest = SerializableRequest<DefaultRequest>,
    ResponseToJava extends ResponseInterface = DefaultResponse
> = (request: RequestToJava) => ResponseToJava;

export interface WebSocketSession {
    id: string;
    params: Record<string, string | string[]>;
    path: string;
    user: Omit<User,'type'>;
}

export type WebSocketEventType = 'open' | 'message' | 'error' | 'close';

export interface WebSocketEvent<T> {
    data: T;
    closeReason?: number;
    error?: string;
    message?: string;
    session: WebSocketSession;
    type: WebSocketEventType;
}

type WebSocketEventHandler<T = Record<string, unknown>> = (event: WebSocketEvent<T>) => void;

export interface Controller<
    Request extends RequestInterface = DefaultRequest,
    Response extends ResponseInterface = DefaultResponse,
> {
    all?: RequestHandler<Request, Response>;
    connect?: RequestHandler<Request, Response>;
    delete?: RequestHandler<Request, Response>;
    get?: RequestHandler<Request, Response>;
    head?: RequestHandler<Request, Response>;
    options?: RequestHandler<Request, Response>;
    patch?: RequestHandler<Request, Response>;
    post?: RequestHandler<Request, Response>;
    put?: RequestHandler<Request, Response>;
    trace?: RequestHandler<Request, Response>;
    webSocketEvent?: WebSocketEventHandler;
}

export interface ErrorRequest<T extends RequestInterface = DefaultRequest> {
    exception?: unknown;
    message: string;
    request: T;
    status: number;
}

export type ErrorRequestHandler<
    Err extends ErrorRequest = ErrorRequest,
    ResponseToJava extends ResponseInterface = DefaultResponse
> = (err: Err) => ResponseToJava;

export interface ErrorController {
    handle400?: ErrorRequestHandler;
    handle401?: ErrorRequestHandler;
    handle402?: ErrorRequestHandler;
    handle403?: ErrorRequestHandler;
    handle404?: ErrorRequestHandler;
    handle405?: ErrorRequestHandler;
    handle406?: ErrorRequestHandler;
    handle407?: ErrorRequestHandler;
    handle408?: ErrorRequestHandler;
    handle409?: ErrorRequestHandler;
    handle410?: ErrorRequestHandler;
    handle411?: ErrorRequestHandler;
    handle412?: ErrorRequestHandler;
    handle413?: ErrorRequestHandler;
    handle414?: ErrorRequestHandler;
    handle415?: ErrorRequestHandler;
    handle416?: ErrorRequestHandler;
    handle417?: ErrorRequestHandler;
    handle418?: ErrorRequestHandler;
    handle421?: ErrorRequestHandler;
    handle422?: ErrorRequestHandler;
    handle423?: ErrorRequestHandler;
    handle424?: ErrorRequestHandler;
    handle425?: ErrorRequestHandler;
    handle426?: ErrorRequestHandler;
    handle428?: ErrorRequestHandler;
    handle429?: ErrorRequestHandler;
    handle431?: ErrorRequestHandler;
    handle451?: ErrorRequestHandler;
    handle500?: ErrorRequestHandler;
    handle501?: ErrorRequestHandler;
    handle502?: ErrorRequestHandler;
    handle503?: ErrorRequestHandler;
    handle504?: ErrorRequestHandler;
    handle505?: ErrorRequestHandler;
    handle506?: ErrorRequestHandler;
    handle507?: ErrorRequestHandler;
    handle508?: ErrorRequestHandler;
    handle510?: ErrorRequestHandler;
    handle511?: ErrorRequestHandler;
    handleError?: ErrorRequestHandler;
}

export interface IdProviderController extends Controller {
    autoLogin?: AutoLoginRequestHandler;
    handle401?: RequestHandler;
    login?: RequestHandler;
    logout?: RequestHandler;
}

export interface HttpFilterController<
  RequestFromJava extends RequestInterface = DefaultRequest,
  ResponseFromNext extends ResponseInterface = Response,
  ResponseToJava extends ResponseInterface = ResponseFromNext
> {
    filter: (
        request: RequestFromJava,
        next: HttpFilterNext<
            SerializableRequest<RequestFromJava>,
            ResponseFromNext
        >
    ) => ResponseToJava;
}

export interface ResponseProcessorController<
    RequestFromJava extends RequestInterface = DefaultRequest,
    ResponseFromJava extends MappedResponse = MappedResponse,
    ResponseToJava extends ResponseInterface = Partial<ResponseFromJava>
> {
    responseProcessor: (request: RequestFromJava, response: ResponseFromJava) => ResponseToJava;
}

export type UserKey = `user:${string}:${string}`;
export type GroupKey = `group:${string}:${string}`;
export type RoleKey = `role:${string}`;

export type PrincipalKey = UserKey | GroupKey | RoleKey;

export interface User {
    type: 'user';
    key: UserKey;
    displayName: string;
    modifiedTime?: string;
    disabled?: boolean;
    email?: string;
    login: string;
    idProvider: string;
    hasPassword: boolean;
}

export interface Group {
    type: 'group';
    key: GroupKey;
    displayName: string;
    modifiedTime: string;
    description?: string;
}

export interface Role {
    type: 'role';
    key: RoleKey;
    displayName: string;
    modifiedTime: string;
    description?: string;
}

export type Principal = User | Group | Role;

export interface Attachment {
    name: string;
    label?: string;
    size: number;
    mimeType: string;
}

export interface DataValidationError
    extends ValidationError {
    propertyPath: string;
}

export interface AttachmentValidationError
    extends ValidationError {
    attachment: string;
}

export interface ValidationError {
    message: string;
    i18n: string;
    errorCode: ValidationErrorCode;
    args: any[];
}

export interface ValidationErrorCode {
    applicationKey: string;
    code: string;
}

export interface PublishInfo {
    from?: string;
    to?: string;
    first?: string;
}

export interface FragmentComponent {
    type: 'fragment'
    fragment: string;
    path: string;
}

export interface LayoutComponent<
    Descriptor extends ComponentDescriptor = ComponentDescriptor,
    Config extends NestedRecord = Descriptor extends LayoutDescriptor
        ? XpLayoutMap[Descriptor]
        : NestedRecord,
    Regions extends
        Record<string, Region<(FragmentComponent | PartComponent | TextComponent)[]>> =
        Record<string, Region<(FragmentComponent | Part          | TextComponent)[]>>
> {
    type: 'layout'
    descriptor: Descriptor
    config: Config
    path?: string // Missing in fragmentPreview https://github.com/enonic/xp/issues/10116
    regions: Regions;
}
type LayoutDescriptor = keyof XpLayoutMap;
type Layout = LayoutDescriptor extends any // this lets us iterate over every member of the union
    ? LayoutComponent<LayoutDescriptor, XpLayoutMap[LayoutDescriptor]>
    : never;

export interface PartComponent<
    Descriptor extends ComponentDescriptor = ComponentDescriptor,
    Config extends NestedRecord =
        Descriptor extends PartDescriptor
        ? XpPartMap[Descriptor]
        : NestedRecord
> {
    type: 'part'
    descriptor: Descriptor
    config: Config
    path?: string // Missing in fragmentPreview https://github.com/enonic/xp/issues/10116
}
type PartDescriptor = keyof XpPartMap;
type Part = PartDescriptor extends any // this lets us iterate over every member of the union
    ? PartComponent<PartDescriptor, XpPartMap[PartDescriptor]>
    : never;

// NOTE: This reflect lib-portal.getContent where page templates are resolved.
// WARNING: This does NOT reflect lib-content.getContent where page templates are NOT resolved!
export interface PageComponent<
    Descriptor extends ComponentDescriptor = ComponentDescriptor,
    Config extends NestedRecord =
        Descriptor extends PageDescriptor
        ? XpPageMap[Descriptor]
        : NestedRecord,
    Regions extends
        Record<string, Region<(FragmentComponent | LayoutComponent | PartComponent | TextComponent)[]>> =
        Record<string, Region<(FragmentComponent | Layout          | Part          | TextComponent)[]>>
> {
    config: Config
    descriptor: Descriptor
    path: '/'
    regions: Regions;
    template?: string;
    type: 'page'
}

type PageDescriptor = keyof XpPageMap;
type Page = PageDescriptor extends any // this lets us iterate over every member of the union
    ? PageComponent<PageDescriptor, XpPageMap[PageDescriptor]>
    : never;

export interface TextComponent {
    type: 'text'
    path: string
    text: string
}

export type Component<
    Descriptor extends ComponentDescriptor = LayoutDescriptor | PageDescriptor | PartDescriptor,
    Config extends NestedRecord = NestedRecord,
    Regions extends (
        Descriptor extends LayoutDescriptor
        ? Record<string, Region<(FragmentComponent | PartComponent | TextComponent)[]>>
        : Record<string, Region>
    ) = Descriptor extends LayoutDescriptor
        ? Record<string, Region<(FragmentComponent | PartComponent | TextComponent)[]>>
        : Record<string, Region>,
> =
    | FragmentComponent
    | LayoutComponent<Descriptor, Config, Regions>
    | PageComponent<Descriptor, Config, Regions>
    | PartComponent<Descriptor, Config>
    | TextComponent;

export interface PageRegion<
    Components extends
        (FragmentComponent | LayoutComponent | PartComponent | TextComponent)[] =
        (FragmentComponent | Layout          | Part          | TextComponent)[]
> {
    name: string;
    components: Components;
}

export interface LayoutRegion<
    Components extends
        (FragmentComponent | PartComponent | TextComponent)[] =
        (FragmentComponent | Part          | TextComponent)[]
> {
    name: string;
    components: Components;
}

export type Region<
    Components extends
        (FragmentComponent | LayoutComponent | PartComponent | TextComponent)[] =
        (FragmentComponent | Layout          | Part          | TextComponent)[]
// @ts-expect-error TODO LayoutRegion can't eat LayoutComponent nor Layout!!!
> = PageRegion<Components> | LayoutRegion<Components>;

export interface Content<
    Data = Record<string, unknown>,
    Type extends string = string,
    _Component extends (
        Type extends 'portal:fragment'
            ? LayoutComponent | PartComponent
            : PageComponent
        ) = (
            Type extends 'portal:fragment'
                ? Layout | Part
                : Page
            ),
    > {
    _id: string;
    _name: string;
    _path: string;
    _score?: number;
    creator: UserKey;
    modifier?: UserKey;
    createdTime: string;
    modifiedTime?: string;
    owner: string;
    data: Type extends 'portal:fragment' ? Record<string,never> : Data;
    type: Type;
    displayName: string;
    hasChildren: boolean;
    language?: string;
    valid: boolean;
    originProject?: string;
    childOrder?: string;
    _sort?: object[];
    page?: Type extends 'portal:fragment' ? never : _Component;
    x: XpMixin;
    attachments: Record<string, Attachment>;
    publish?: PublishInfo;
    workflow?: Workflow;
    inherit?: ContentInheritValue[];
    variantOf?: string;
    fragment?: Type extends 'portal:fragment' ? _Component : never;
}

export interface PatchableContent<
    Data extends Record<string, unknown> = Record<string, unknown>,
    Type extends string = string,
> {
    displayName: string;
    data: Type extends 'portal:fragment' ? Record<string, never> : Data;
    x: XpMixin;
    page: Type extends 'portal:fragment' ? never : _Component;
    valid: boolean;
    owner: UserKey;
    language: string;
    creator: UserKey;
    createdTime: string;
    modifier: UserKey;
    modifiedTime: string;
    publishInfo: PublishInfo;
    processedReferences: string[];
    workflowInfo: Workflow;
    manualOrderValue: number;
    inherit: ContentInheritValue[];
    variantOf: string;
    modifyAttachments: Attachment;
    removeAttachments: string[];
    createAttachments: AddAttachmentParam[];
    validationErrors: ValidationError[];
    type: Type;
    childOrder: string;
    originProject: string;
    originalParentPath: string;
    archivedTime: string;
    archivedBy: UserKey;
}

export type Workflow = {
    state: 'IN_PROGRESS' | 'PENDING_APPROVAL' | 'REJECTED' | 'READY';
    checks?: Record<string, 'PENDING' | 'REJECTED' | 'APPROVED'>;
};

export type ContentInheritValue = 'CONTENT' | 'PARENT' | 'NAME' | 'SORT';

// Compliant with npm module ts-brand
type Brand<
    Base,
    Branding
> = Base & {
  '__type__': Branding
};

export type ByteSource = Brand<object, 'ByteSource'>;

//
// RESOURCES
//
export interface Resource {
    getSize(): number;

    getTimestamp(): number;

    getStream(): ByteSource;

    exists(): boolean;
}

export interface ResourceKey {
    getApplicationKey(): string;
    getPath(): string;
    getUri(): string;
    isRoot(): boolean;
    getName(): string;
    getExtension(): string;
}

//
// DSL QUERIES
//
export type DslQueryType = 'dateTime' | 'time';

export type DslOperator = 'OR' | 'AND';

export interface TermDslExpression {
    field: string;
    value: unknown;
    type?: DslQueryType;
    boost?: number;
}

export interface InDslExpression {
    field: string;
    values: unknown[];
    type?: DslQueryType;
    boost?: number;
}

export interface LikeDslExpression {
    field: string;
    value: string;
    type?: DslQueryType;
    boost?: number;
}

export interface RangeDslExpression {
    field: string;
    type?: DslQueryType;
    lt?: unknown;
    lte?: unknown;
    gt?: unknown;
    gte?: unknown;
    boost?: number;
}

export interface PathMatchDslExpression {
    field: string;
    path: string;
    minimumMatch?: number;
    boost?: number;
}

export interface MatchAllDslExpression {
    boost?: number;
}

export interface FulltextDslExpression {
    fields: string[];
    query: string;
    operator?: DslOperator;
    boost?: number;
}

export interface NgramDslExpression {
    fields: string[];
    query: string;
    operator?: DslOperator;
    boost?: number;
}

export interface StemmedDslExpression {
    fields: string[];
    query: string;
    language: string;
    operator?: DslOperator;
    boost?: number;
}

export interface ExistsDslExpression {
    field: string;
    boost?: number;
}

export interface BooleanDslExpression {
    should?: QueryDsl | QueryDsl[];
    must?: QueryDsl | QueryDsl[];
    mustNot?: QueryDsl | QueryDsl[];
    filter?: QueryDsl | QueryDsl[];
    boost?: number;
}

export type QueryDsl = {
    boolean: BooleanDslExpression;
} | {
    ngram: NgramDslExpression;
} | {
    stemmed: StemmedDslExpression;
} | {
    fulltext: FulltextDslExpression;
} | {
    matchAll: MatchAllDslExpression;
} | {
    pathMatch: PathMatchDslExpression;
} | {
    range: RangeDslExpression;
} | {
    like: LikeDslExpression;
} | {
    in: InDslExpression;
} | {
    term: TermDslExpression;
} | {
    exists: ExistsDslExpression;
};

export type SortDirection = 'ASC' | 'DESC';

export type DistanceUnit =
    | 'm'
    | 'meters'
    | 'in'
    | 'inch'
    | 'yd'
    | 'yards'
    | 'ft'
    | 'feet'
    | 'km'
    | 'kilometers'
    | 'NM'
    | 'nmi'
    | 'nauticalmiles'
    | 'mm'
    | 'millimeters'
    | 'cm'
    | 'centimeters'
    | 'mi'
    | 'miles';

export interface FieldSortDsl {
    field: string;
    direction?: SortDirection;
}

export interface GeoDistanceSortDsl
    extends FieldSortDsl {

    unit?: DistanceUnit;
    location?: {
        lat: number;
        lon: number;
    }
}

export type SortDsl = FieldSortDsl | GeoDistanceSortDsl;

//
// START AGGREGATIONS, FILTERS
//
export type Bucket<SubAggregations extends Aggregations = Record<never, never>> = AggregationsToAggregationResults<SubAggregations> & {
    key: string;
    docCount: number;
};

export type NumericBucket<SubAggregations extends Aggregations = Record<never, never>> = Bucket<SubAggregations> & {
    from?: number;
    to?: number;
};

export type DateBucket<SubAggregations extends Aggregations = Record<never, never>> = Bucket<SubAggregations> & {
    from?: string;
    to?: string;
};

export interface BucketsAggregationResult<SubAggregations extends Aggregations = Record<never, never>> {
    buckets: (DateBucket<SubAggregations> | NumericBucket<SubAggregations>)[];
}

export interface StatsAggregationResult {
    count: number;
    min: number;
    max: number;
    avg: number;
    sum: number;
}

export interface SingleValueMetricAggregationResult {
    value: number;
}

export type AggregationsResult<SubAggregations extends Aggregations = Record<never, never>> =
    | BucketsAggregationResult<SubAggregations>
    | StatsAggregationResult
    | SingleValueMetricAggregationResult;

export type AggregationsToAggregationResults<AggregationInput extends Aggregations = never> = {
    [Key in keyof AggregationInput]: AggregationToAggregationResult<AggregationInput[Key]>;
};

type AggregationOrNone<T> = T extends Aggregation ? T : Record<never, never>;

export type AggregationToAggregationResult<Type extends Aggregation> = Type extends GeoDistanceAggregation
                                                                       ? BucketsAggregationResult
                                                                       : Type extends Exclude<BucketsAggregationsUnion, GeoDistanceAggregation>
                                                                         ? BucketsAggregationResult<AggregationOrNone<Type['aggregations']>>
                                                                         : Type extends SingleValueMetricAggregationsUnion
                                                                           ? SingleValueMetricAggregationResult
                                                                           : Type extends StatsAggregation
                                                                             ? StatsAggregationResult
                                                                             : never;

export type BucketsAggregationsUnion =
    | TermsAggregation
    | GeoDistanceAggregation
    | NumericRangeAggregation
    | DateRangeAggregation
    | HistogramAggregation
    | DateHistogramAggregation;

export type SingleValueMetricAggregationsUnion =
    | MinAggregation
    | MaxAggregation
    | ValueCountAggregation;

export type Aggregation =
    | TermsAggregation
    | HistogramAggregation
    | DateHistogramAggregation
    | NumericRangeAggregation
    | DateRangeAggregation
    | StatsAggregation
    | GeoDistanceAggregation
    | MinAggregation
    | MaxAggregation
    | ValueCountAggregation;

export type Aggregations = Record<string, Aggregation>;

export interface TermsAggregation {
    terms: {
        field: string;
        order?: string;
        size?: number;
        minDocCount?: number;
    };
    aggregations?: Aggregations;
}

export interface HistogramAggregation {
    histogram: {
        field: string;
        order?: string;
        interval?: number;
        extendedBoundMin?: number;
        extendedBoundMax?: number;
        minDocCount?: number;
    };
    aggregations?: Aggregations;
}

export interface DateHistogramAggregation {
    dateHistogram: {
        field: string;
        interval?: string;
        minDocCount?: number;
        format: string;
    };
    aggregations?: Aggregations;
}

export interface NumericRange {
    from?: number;
    to?: number;
    key?: string;
}

export interface NumericRangeAggregation {
    range: {
        field: string;
        ranges?: NumericRange[];
    };
    aggregations?: Aggregations;
}

export interface DateRange {
    from?: string;
    to?: string;
    key?: string;
}

export interface DateRangeAggregation {
    dateRange: {
        field: string;
        format: string;
        ranges: DateRange[];
    };
    aggregations?: Aggregations;
}

export interface StatsAggregation {
    stats: {
        field: string;
    };
}

export interface GeoDistanceAggregation {
    geoDistance: {
        field: string;
        unit?: string;
        origin: {
            lat: string;
            lon: string;
        };
        ranges: NumericRange[];
    };
}

export interface MinAggregation {
    min: {
        field: string;
    };
}

export interface MaxAggregation {
    max: {
        field: string;
    };
}

export interface ValueCountAggregation {
    count: {
        field: string;
    };
}

export interface Highlight {
    encoder?: 'default' | 'html';
    tagsSchema?: 'styled';
    fragmenter?: 'simple' | 'span';
    fragmentSize?: number;
    noMatchSize?: number;
    numberOfFragments?: number;
    order?: 'score' | 'none';
    preTag?: string;
    postTag?: string;
    requireFieldMatch?: boolean;
    properties?: Record<string, Highlight>;
}

export interface HighlightResult {
    [highlightedFieldName: string]: string[];
}

export interface ExistsFilter {
    exists: {
        field: string;
    };
}

export interface NotExistsFilter {
    notExists: {
        field: string;
    };
}

export interface HasValueFilter {
    hasValue: {
        field: string;
        values: unknown[];
    };
}

export interface IdsFilter {
    ids: {
        values: string[];
    };
}

export interface BooleanFilter {
    boolean: {
        must?: Filter | Filter[];
        mustNot?: Filter | Filter[];
        should?: Filter | Filter[];
    };
}

export type Filter = ExistsFilter | NotExistsFilter | HasValueFilter | IdsFilter | BooleanFilter;

//
// FORM ITEMS
//
export type InputType =
    | 'Time'
    | 'DateTime'
    | 'Instant'
    | 'CheckBox'
    | 'ComboBox'
    | 'Long'
    | 'Double'
    | 'RadioButton'
    | 'TextArea'
    | 'ContentTypeFilter'
    | 'GeoPoint'
    | 'TextLine'
    | 'Tag'
    | 'CustomSelector'
    | 'AttachmentUploader'
    | 'ContentSelector'
    | 'MediaSelector'
    | 'ImageSelector'
    | 'Date'
    | 'MediaUploader'
    | 'SiteConfigurator'
    | 'HtmlArea';

export interface FormItemSet {
    formItemType: 'ItemSet';
    name: string;
    label: string;
    customText: string;
    helpText: string;
    maximize: boolean;
    inputType: InputType;
    occurrences: {
        maximum: number;
        minimum: number;
    };
    items: FormItem[];
}

export interface FormItemLayout {
    formItemType: 'Layout';
    name: string;
    label: string;
    items: FormItem[];
}

export type ValueType =
    | 'BinaryReference'
    | 'Boolean'
    | 'DateTime'
    | 'Double'
    | 'GeoPoint'
    | 'Link'
    | 'LocalDateTime'
    | 'LocalDate'
    | 'LocalTime'
    | 'Long'
    | 'PropertySet'
    | 'Reference'
    | 'String'
    | 'Xml';

export interface FormItemInput {
    formItemType: 'Input';
    name: string;
    label: string;
    customText: string;
    helpText: string;
    validationRegexp: string;
    maximize: boolean;
    inputType: InputType;
    occurrences: {
        maximum: number;
        minimum: number;
    };
    default: {
        value: string;
        type: ValueType;
    }
    config: {
        [configName: string]: {
            [attributeKey: string]: string;
            value: string;
        }[]
    }
}

export interface FormItemOptionSet {
    formItemType: 'OptionSet';
    name: string;
    label: string;
    expanded: boolean;
    helpText: string;
    occurrences: {
        maximum: number;
        minimum: number;
    };
    selection: {
        maximum: number;
        minimum: number;
    };
    options: {
        name: string;
        label: string;
        helpText: string;
        default: boolean;
        items: FormItem[]
    }[];
}

export interface FormItemFormFragment {
    formItemType: 'FormFragment';
    name: string;
}

export type FormItem = FormItemSet | FormItemLayout | FormItemOptionSet | FormItemInput | FormItemFormFragment;
