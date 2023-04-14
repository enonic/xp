declare global {
    interface XpXData {
        [key: string]: Record<string, Record<string, unknown>>;
    }
}

export type UserKey = `user:${string}:${string}`;
export type GroupKey = `group:${string}:${string}`;
export type RoleKey = `role:${string}`;

export type PrincipalKey = UserKey | GroupKey | RoleKey;

export interface User {
    type: 'user';
    key: UserKey;
    displayName: string;
    modifiedTime: string;
    disabled?: boolean;
    email?: string;
    login: string;
    idProvider: string;
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

export interface PublishInfo {
    from?: string;
    to?: string;
    first?: string;
}

// ─────────────────────────────────────────────────────────────────────────────
// Component
// ─────────────────────────────────────────────────────────────────────────────
export interface FragmentComponent {
    type: 'fragment'
    fragment: string;
    path: string;
}

export interface LayoutComponent<
    Descriptor extends string = string,
    Config extends object = object,
    Regions extends Record<string, LayoutRegion> = Record<string, LayoutRegion>
> {
    type: 'layout'
    descriptor: Descriptor
    config: Config
    path?: string // Missing in fragmentPreview https://github.com/enonic/xp/issues/10116
    regions: Regions;
}

export interface PartComponent<
    Descriptor extends string = string,
    Config extends object = object
> {
    type: 'part'
    descriptor: Descriptor
    config: Config
    path?: string // Missing in fragmentPreview https://github.com/enonic/xp/issues/10116
}

export interface PageComponent<
    Descriptor extends string = string,
    Config extends object = object,
    Regions extends Record<string, PageRegion> = Record<string, PageRegion>
> {
    type: 'page'
    descriptor: Descriptor
    config: Config
    path: '/'
    regions: Regions;
}

export interface TextComponent {
    type: 'text'
    path: string
    text: string
}

export type LayoutRegionComponent<
    Descriptor extends string = string,
    Config extends object = object
> =
    | FragmentComponent
    | PartComponent<Descriptor, Config>
    | TextComponent;

export type PageRegionComponent<
    Descriptor extends string = string,
    Config extends object = object,
    Regions extends Record<string, PageRegion> = Record<string, PageRegion>
> =
    | FragmentComponent
    | LayoutComponent<Descriptor, Config, Regions>
    | PartComponent<Descriptor, Config>
    | TextComponent;

export type Component<
    Descriptor extends string = string,
    Config extends object = object,
    Regions extends Record<string, Region> = Record<string, Region>
> =
    | FragmentComponent
    | LayoutComponent<Descriptor, Config, Regions>
    | PageComponent<Descriptor, Config, Regions>
    | PartComponent<Descriptor, Config>
    | TextComponent;

// ─────────────────────────────────────────────────────────────────────────────
// Region
// ─────────────────────────────────────────────────────────────────────────────
export interface LayoutRegion<Components extends LayoutRegionComponent[] = LayoutRegionComponent[]> {
    name: string;
    components: Components;
}

export interface PageRegion<Components extends PageRegionComponent[] = PageRegionComponent[]> {
    name: string;
    components: Components;
}

export interface Region<Components extends Component[] = Component[]> {
    name: string;
    components: Component<Config>[];
}

// ─────────────────────────────────────────────────────────────────────────────
// Content
// ─────────────────────────────────────────────────────────────────────────────
export interface CommonContent {
    _id: string;
    _name: string;
    _path: string;
    _score?: number;
    creator: UserKey;
    modifier?: UserKey;
    createdTime: string;
    modifiedTime?: string;
    owner: string;
    
    displayName: string;
    hasChildren: boolean;
    language?: string;
    valid: boolean;
    originProject?: string;
    childOrder?: string;
    _sort?: object[];
    
    x: XpXData;
    attachments: Record<string, Attachment>;
    publish?: PublishInfo;
    workflow?: {
        state: 'IN_PROGRESS' | 'PENDING_APPROVAL' | 'REJECTED' | 'READY';
        checks?: Record<string, 'PENDING' | 'REJECTED' | 'APPROVED'>;
    };
    inherit?: ('CONTENT' | 'PARENT' | 'NAME' | 'SORT')[];
    variantOf?: string;
}

export interface FragmentContent extends CommonContent {
    fragment: LayoutComponent | PartComponent
    type: 'portal:fragment'
}

export interface PageContent<
    Data = Record<string, unknown>,
    Type extends string = string,
    Page extends Component = Component,
> extends CommonContent {
    data: Data;
    page: Page;
    type: Type;
}

export type Content<
    Data = Record<string, unknown>,
    Type extends string = string,
    Page extends Component = Component,
> = FragmentContent | PageContent;

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

type SortDirection = 'ASC' | 'DESC';

type DistanceUnit =
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

export interface FormItemInlineMixin {
    formItemType: 'InlineMixin';
    name: string;
}

export type FormItem = FormItemSet | FormItemLayout | FormItemOptionSet | FormItemInput | FormItemInlineMixin;
