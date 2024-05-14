import type { NestedRecord } from './util';

// This also makes sure XpLayoutMap, etc are available.
import type { ComponentDescriptor } from './globalModifying';

export type { ComponentDescriptor } from './globalModifying';
export type {
    App,
    AppInterface,
    DoubleUnderscore,
    DoubleUnderscoreInterface,
    Log,
    LogInterface,
    NewBean,
    Resolve,
    ScriptValue,
    ScriptValueInterface,
    XpRequire,
} from './globals';
export type {
    Resource,
    ResourceKey,
    ResourceKeyInterface,
} from './resource';

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
    type: 'page'
    descriptor: Descriptor
    config: Config
    path: '/'
    regions: Regions;
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
    Regions extends Record<string, Region> = Record<string, Region>,
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
> = PageRegion<Components> | LayoutRegion<Components>;

export interface Content<
    Data = Record<string, unknown>,
    Type extends string = string,
    _Component extends (
        Type extends 'portal:fragment'
            ? LayoutComponent | PartComponent
            : PageComponent | Record<string,never>
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
    x: XpXData;
    attachments: Record<string, Attachment>;
    publish?: PublishInfo;
    workflow?: {
        state: 'IN_PROGRESS' | 'PENDING_APPROVAL' | 'REJECTED' | 'READY';
        checks?: Record<string, 'PENDING' | 'REJECTED' | 'APPROVED'>;
    };
    inherit?: ('CONTENT' | 'PARENT' | 'NAME' | 'SORT')[];
    variantOf?: string;
    fragment?: Type extends 'portal:fragment' ? _Component : never;
}

// Compliant with npm module ts-brand
type Brand<
    Base,
    Branding
> = Base & {
  '__type__': Branding
};

export type ByteSource = Brand<object, 'ByteSource'>;

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
