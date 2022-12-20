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

export interface Component<Config extends object = object, Regions extends Record<string, Region> = Record<string, Region>> {
    config: Config;
    descriptor: string;
    path: string;
    type: 'page' | 'layout' | 'part';
    regions: Regions;
}

export interface Region<Config extends object = object> {
    name: string;
    components: Component<Config>[];
}

export interface Content<
    Data = Record<string, unknown>,
    Type extends string = string,
    Page extends Component = Component,
    > {
    _id: string;
    _name: string;
    _path: string;
    _score: number;
    creator: UserKey;
    modifier: UserKey;
    createdTime: string;
    modifiedTime: string;
    owner: string;
    data: Data;
    type: Type;
    displayName: string;
    hasChildren: boolean;
    language: string;
    valid: boolean;
    originProject: string;
    childOrder?: string;
    _sort?: object[];
    page: Page;
    x: XpXData;
    attachments: Record<string, Attachment>;
    publish?: PublishInfo;
    workflow?: {
        state: 'IN_PROGRESS' | 'PENDING_APPROVAL' | 'REJECTED' | 'READY';
        checks?: Record<string, 'PENDING' | 'REJECTED' | 'APPROVED'>;
    };
    inherit?: ('CONTENT' | 'PARENT' | 'NAME' | 'SORT')[];
}

// Compliant with npm module ts-brand
type Brand<
    Base,
    Branding
> = Base & {
  '__type__': Branding
};

export type ByteSource = Brand<object, 'ByteSource'>;

export interface Resource {
    getSize(): number;

    getTimestamp(): number;

    getStream(): ByteSource;

    exists(): boolean;
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
