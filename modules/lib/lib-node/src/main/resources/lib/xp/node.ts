/**
 * Functions to get, query and manipulate nodes.
 *
 * @example
 * var nodeLib = require('/lib/xp/node');
 *
 * @module node
 */

declare global {
    interface XpLibraries {
        '/lib/xp/node': typeof import('./node');
    }
}

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

// START AGGREGATIONS, FILTERS, QUERIES, SUGGESTIONS

export interface Bucket {
    [subAggregationName: string]: BucketsAggregation | StatsAggregation | SingleValueMetricAggregation | string | number | undefined;

    key: string;
    docCount: number;
}

export interface NumericBucket
    extends Bucket {
    from?: number;
    to?: number;
}

export interface DateBucket
    extends Bucket {
    from?: string;
    to?: string;
}

export interface BucketsAggregation {
    buckets: (DateBucket | NumericBucket)[];
}

export interface StatsAggregation {
    count: number;
    min: number;
    max: number;
    avg: number;
    sum: number;
}

export interface SingleValueMetricAggregation {
    value: number;
}

export type AggregationsResult = BucketsAggregation | StatsAggregation | SingleValueMetricAggregation;

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

export interface TermsAggregation {
    terms: {
        field: string;
        order?: string;
        size?: number;
        minDocCount?: number;
    };
    aggregations?: {
        [subAggregations: string]: Aggregation;
    };
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
    aggregations?: {
        [subAggregations: string]: Aggregation;
    };
}

export interface DateHistogramAggregation {
    dateHistogram: {
        field: string;
        interval?: string;
        minDocCount?: number;
        format: string;
    };
    aggregations?: {
        [subAggregations: string]: Aggregation;
    };
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
    aggregations?: {
        [subAggregations: string]: Aggregation;
    };
}

export interface NumericRangeAggregation {
    range: {
        field: string;
        ranges?: NumericRange[];
    };
    aggregations?: {
        [subAggregations: string]: Aggregation;
    };
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
    aggregations?: {
        [subAggregations: string]: Aggregation;
    };
}

export interface StatsAggregation {
    stats: {
        field: string;
    };
}

export interface GeoDistanceAggregation {
    geoDistance: {
        field: string;
        unit: string;
        origin?: {
            lat: string;
            lon: string;
        };
        ranges?: NumericRange[];
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
        must?: Filter[];
        mustNot?: Filter[];
        should?: Filter[];
    };
}

export type Filter = ExistsFilter | NotExistsFilter | HasValueFilter | IdsFilter | BooleanFilter;

export type Suggestion = TermSuggestion;

export interface TermSuggestion {
    text: string;
    term: TermSuggestionOptions;
}

export interface TermSuggestionOptions {
    field: string;
    analyzer?: string;
    sort?: 'score' | 'frequency';
    suggestMode?: 'missing' | 'popular' | 'always';
    stringDistance?: 'internal' | 'damerau_levenshtein' | 'levenshtein' | 'jarowinkler' | 'ngram';
    size?: number | null;
    maxEdits?: number | null;
    prefixLength?: number | null;
    minWordLength?: number | null;
    maxInspections?: number | null;
    minDocFreq?: number | null;
    maxTermFreq?: number | null;
}

type LiteralUnion<T extends U, U = string> = T | (U & Record<never, never>);

export type DslQueryType = LiteralUnion<'dateTime' | 'time'> | number | boolean;

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
}

export interface NgramDslExpression {
    fields: string[];
    query: string;
    operator?: DslOperator;
}

export interface StemmedDslExpression {
    fields: string[];
    query: string;
    language: string;
}

export interface BooleanDslExpression {
    should?: DslExpression | DslExpression[];
    must?: DslExpression | DslExpression[];
    mustNot?: DslExpression | DslExpression[];
    filter?: DslExpression | DslExpression[];
}

export type DslExpression =
    | TermDslExpression
    | InDslExpression
    | LikeDslExpression
    | RangeDslExpression
    | PathMatchDslExpression
    | MatchAllDslExpression
    | FulltextDslExpression
    | NgramDslExpression
    | BooleanDslExpression
    | StemmedDslExpression;

export type QueryDsl = {
    boolean: BooleanDslExpression;
} | {
    ngram: NgramDslExpression;
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

export interface Explanation {
    value: number;
    description: string;
    details: Explanation[];
}

export interface NodeQueryResult {
    total: number;
    count: number;
    hits: {
        id: string;
        score: number;
        explanation?: Explanation;
        highlight?: HighlightResult;
    }[];
    aggregations?: AggregationsResult;
    suggestions?: {
        [suggestionName: string]: {
            text: string;
            length: number;
            offset: number;
            options: {
                text: string;
                score: number;
                freq?: number; // only for term
            }[];
        }[];
    }[];
}

export type NodeMultiRepoQueryResult = NodeQueryResult & {
    hits: {
        repoId: string;
        branch: string;
    }[];
};

// END AGGREGATIONS, FILTERS, QUERIES, SUGGESTIONS

interface NodeHandleFactory {
    create(context: NodeHandleContext): NodeHandler;
}

interface MultiRepoNodeHandleContext {
    addSource(repoId: string, branch: string, principals: string[]): void;
}

interface MultiRepoNodeHandleFactory {
    create(context: MultiRepoNodeHandleContext): MultiRepoNodeHandler;
}

const factory = __.newBean<NodeHandleFactory>('com.enonic.xp.lib.node.NodeHandleFactory');

const multiRepoConnectFactory = __.newBean<MultiRepoNodeHandleFactory>('com.enonic.xp.lib.node.MultiRepoNodeHandleFactory');

function argsToStringArray(argsArray: (string | string[])[]): string[] {
    const array: string[] = [];

    for (let i = 0; i < argsArray.length; i++) {
        const currArgument = argsArray[i];
        if (Array.isArray(currArgument)) {
            currArgument.forEach((v) => {
                array.push(v);
            });
        } else {
            array.push(currArgument);
        }
    }
    return array;
}

function isString(value: unknown): boolean {
    return typeof value === 'string' || value instanceof String;
}

function isObject(value: unknown): boolean {
    return typeof value !== 'undefined' && value !== null && typeof value === 'object' && value.constructor === Object;
}

function prepareGetParams(params: (string | string[] | GetNodeParams | GetNodeParams[])[], bean: GetNodeHandlerParams): void {
    for (let i = 0; i < params.length; i++) {
        const param = params[i];
        if (isString(param)) {
            bean.add(param as string);
        } else if (isObject(param)) {
            const getParams = param as GetNodeParams;
            checkRequired(getParams, 'key');
            bean.add(getParams.key, __.nullOrValue(getParams.versionId));
        } else if (Array.isArray(param)) {
            prepareGetParams(param, bean);
        } else {
            throw 'Unsupported type';
        }
    }
}

interface MultiRepoNodeHandler {
    query(params: QueryNodeHandlerParams): NodeMultiRepoQueryResult;
}

interface NodeHandler {
    create(node: ScriptValue): Node;

    modify(editor: ScriptValue, key: string): Node;

    setChildOrder(key: string, childOrder: string): Node;

    get(params: GetNodeHandlerParams): Node | Node[] | null;

    delete(keys: string[]): string[];

    push(params: PushNodeHandlerParams): PushNodesResult;

    diff(params: DiffBranchesHandlerParams): DiffBranchesResult;

    move(source: string, target: string): boolean;

    query(params: QueryNodeHandlerParams): NodeQueryResult;

    exist(key: string): boolean;

    findVersions(params: FindVersionsHandlerParams): NodeVersionsQueryResult;

    getActiveVersion(key: string): NodeVersion | null;

    setActiveVersion(key: string, versionId: string): boolean;

    findChildren(params: FindChildrenHandlerParams): FindNodesByParentResult;

    commit(keys: string[], message?: string | null): NodeCommit;

    getCommit(commitId: string): NodeCommit | null;

    setRootPermissions(v: ScriptValue): Node;

    getBinary(key: string, binaryReference?: string | null): object;

    refresh(mode: RefreshMode): void;
}

export interface CreateNodeParams {
    _name?: string;
    _parentPath?: string;
    _indexConfig?: NodeConfigEntry;
    _permissions?: AccessControlEntry[];
    _inheritsPermissions?: boolean;
    _manualOrderValue?: number;
    _childOrder?: string;
}

export interface ModifyNodeParams {
    key: string;
    editor: (node: Node) => Node;
}

export interface GetNodeParams {
    key: string;
    versionId?: string;
}

interface GetNodeHandlerParams {
    add(key: string): void;

    add(key: string, versionId?: string | null): void;
}

export interface PushNodeParams {
    key?: string | null;
    keys?: string[] | null;
    target: string;
    includeChildren?: boolean;
    resolve?: boolean;
    exclude?: string[] | null;
}

export interface PushNodesResult {
    success: string[];
    failed: {
        id: string;
        reason: string;
    }[];
    deleted: string[];
}

interface PushNodeHandlerParams {
    setKey(value?: string | null): void;

    setKeys(value?: string[] | null): void;

    setTargetBranch(value: string): void;

    setIncludeChildren(value: boolean): void;

    setExclude(value?: string[] | null): void;

    setResolve(value: boolean): void;
}

export interface DiffBranchesParams {
    key: string;
    target: string;
    includeChildren: boolean;
}

export interface DiffBranchesResult {
    diff: {
        id: string;
        status: string;
    }[];
}

interface DiffBranchesHandlerParams {
    setKey(value: string): void;

    setTargetBranch(value: string): void;

    setIncludeChildren(value: boolean): void;
}

export interface GetBinaryParams {
    key: string;
    binaryReference?: string | null;
}

export interface MoveNodeParams {
    source: string;
    target: string;
}

export interface SetChildOrderParams {
    key: string;
    childOrder: string;
}

export interface QueryNodeParams {
    start?: number;
    count?: number;
    query?: QueryDsl | string;
    sort?: string | SortDsl | SortDsl[];
    filters?: Filter | Filter[];
    aggregations?: Record<string, Aggregation>;
    suggestions?: Record<string, Suggestion>;
    highlight?: Highlight;
    explain?: boolean;
}

interface QueryNodeHandlerParams {
    setStart(value?: number | null): void;

    setCount(value?: number | null): void;

    setQuery(value: ScriptValue): void;

    setSort(value: ScriptValue): void;

    setAggregations(value: ScriptValue): void;

    setSuggestions(value: ScriptValue): void;

    setHighlight(value: ScriptValue): void;

    setFilters(value: ScriptValue): void;

    setExplain(value: boolean): void;
}

export interface FindVersionsParams {
    key: string;
    start?: number | null;
    count?: number | null;
}

interface FindVersionsHandlerParams {
    setKey(key: string): void;

    setStart(start?: number | null): void;

    setCount(count?: number | null): void;
}

export interface NodeVersion {
    versionId: string;
    nodeId: string;
    nodePath: string;
    timestamp: string;
    commitId: string;
}

export interface NodeVersionsQueryResult {
    total: number;
    count: number;
    hits: NodeVersion[];
}

export interface GetActiveVersionParams {
    key: string;
}

export interface SetActiveVersionParams {
    key: string;
    versionId: string;
}

export interface FindChildrenParams {
    parentKey: string;
    start?: number | null;
    count?: number | null;
    childOrder: string;
    countOnly: boolean;
    recursive: boolean;
}

interface FindChildrenHandlerParams {
    setParentKey(parentKey: string): void;

    setStart(start?: number | null): void;

    setCount(count?: number | null): void;

    setChildOrder(childOrder: string): void;

    setCountOnly(countOnly: boolean): void;

    setRecursive(recursive: boolean): void;
}

export interface FindNodesByParentResult {
    total: number;
    count: number;
    hits: {
        id: string;
    }[];
}

export type RefreshMode = 'SEARCH' | 'STORAGE' | 'ALL';

export interface GetCommitParams {
    id: string;
}

export interface NodeCommit {
    id: string;
    message: string;
    committer: string;
    timestamp: string;
}

export interface CommitParams {
    keys: string | string[];
    message?: string;
}

export interface SetRootPermissionsParams {
    _permissions: AccessControlEntry[];
    _inheritsPermissions: boolean;
}

export type Permission = 'READ' | 'CREATE' | 'MODIFY' | 'DELETE' | 'PUBLISH' | 'READ_PERMISSIONS' | 'WRITE_PERMISSIONS';

export interface AccessControlEntry {
    principal: string;
    allow?: Permission[];
    deny?: Permission[];
}

export interface NodeIndexConfig {
    analyzer: string;
    default?: NodeConfigEntry;
    configs: {
        path: string;
        config: NodeConfigEntry;
    }[];
}

export interface NodeConfigEntry {
    decideByType: boolean;
    enabled: boolean;
    nGram: boolean;
    fulltext: boolean;
    includeInAllText: boolean;
    path: boolean;
    indexValueProcessors: string[];
    languages: string[];
}

export interface Node {
    [key: string]: unknown; // node data

    _id: string;
    _name: string;
    _path: string;
    _childOrder: string;
    _state: string;
    _nodeType: string;
    _versionKey: string;
    _manualOrderValue: number;
    _ts: string;
    _parentPath: string;
    _indexConfig: NodeIndexConfig;
    _inheritsPermissions: boolean;
    _permissions?: AccessControlEntry[];
}

export interface RepoConnection {
    create(params: CreateNodeParams): Node;

    modify(params: ModifyNodeParams): Node;

    get(...keys: (string | string[] | GetNodeParams | GetNodeParams[])[]): Node | Node[] | null;

    delete(...keys: (string | string[])[]): string[];

    push(params: PushNodeParams): PushNodesResult;

    diff(params: DiffBranchesParams): DiffBranchesResult;

    getBinary(params: GetBinaryParams): object;

    move(params: MoveNodeParams): boolean;

    setChildOrder(params: SetChildOrderParams): Node;

    query(params: QueryNodeParams): NodeQueryResult;

    exists(key: string): boolean;

    findVersions(params: FindVersionsParams): NodeVersionsQueryResult;

    getActiveVersion(params: GetActiveVersionParams): NodeVersion | null;

    setActiveVersion(params: SetActiveVersionParams): boolean;

    findChildren(params: FindChildrenParams): FindNodesByParentResult;

    refresh(mode: RefreshMode): void;

    setRootPermissions(params: SetRootPermissionsParams): Node;

    commit(params: CommitParams): NodeCommit;

    getCommit(params: GetCommitParams): NodeCommit | null;
}

/**
 * Creates a new repo connection.
 *
 * @constructor
 * @hideconstructor
 * @alias RepoConnection
 */
class RepoConnectionImpl
    implements RepoConnection {

    constructor(private nodeHandler: NodeHandler) {
    }

    /**
     * This function creates a node.
     *
     *
     * To create a content where the name is not important and there could be multiple instances under the same parent content,
     * skip the `name` parameter and specify a `displayName`.
     *
     * @example-ref examples/node/create-1.js
     * @example-ref examples/node/create-2.js
     *
     * @param {object} params JSON with the parameters.
     * @param {string} [params._name] Name of content.
     * @param {string} [params._parentPath] Path to place content under.
     * @param {object} [params._indexConfig] How the document should be indexed. A default value "byType" will be set if no value specified.
     * @param {object} [params._permissions] The access control list for the node. By default the creator will have full access
     * @param {boolean} [params._inheritsPermissions] true if the permissions should be inherited from the node parent. Default is false.
     * @param {number} [params._manualOrderValue] Value used to order document when ordering by parent and child-order is set to manual
     * @param {string} [params._childOrder] Default ordering of children when doing getChildren if no order is given in query
     *
     * @returns {object} Node created as JSON.
     */
    create(params: CreateNodeParams): Node {
        return __.toNativeObject(this.nodeHandler.create(__.toScriptValue(params)));
    }

    /**
     * This function modifies a node.
     *
     * @example-ref examples/node/modify.js
     *
     * @param {object} params JSON with the parameters.
     * @param {string} params.key Path or id to the node.
     * @param {function} params.editor Editor callback function.
     *
     * @returns {object} Modified node as JSON.
     */
    modify(params: ModifyNodeParams): Node {
        checkRequired(params, 'key');

        return __.toNativeObject(this.nodeHandler.modify(__.toScriptValue(params.editor), params.key));
    }

    /**
     * This function fetches nodes.
     *
     * @example-ref examples/node/get-1.js
     * @example-ref examples/node/get-2.js
     * @example-ref examples/node/get-3.js
     *
     * @param {...(string|string[]|object|object[])} keys to fetch. Each argument could be an id, a path, an object with key and versionId properties or an array of them.
     *
     * @returns {object} The node or node array (as JSON) fetched from the repository.
     */
    get(...keys: (string | string[] | GetNodeParams | GetNodeParams[])[]): Node | Node[] | null {
        const handlerParams = __.newBean<GetNodeHandlerParams>('com.enonic.xp.lib.node.GetNodeHandlerParams');
        prepareGetParams(keys, handlerParams);
        return __.toNativeObject(this.nodeHandler.get(handlerParams));
    }

    /**
     * This function deletes a node or nodes.
     *
     * @example-ref examples/node/delete.js
     *
     * @param {...(string|string[])} keys Keys to delete. Each argument could be an id, a path or an array of the two
     *
     * @returns {string[]} An array of keys that were actually deleted
     */
    delete(...keys: (string | string[])[]): string[] {
        return __.toNativeObject(this.nodeHandler.delete(argsToStringArray(keys)));
    }

    /**
     * This function push a node to a given branch.
     *
     * @example-ref examples/node/push-1.js
     * @example-ref examples/node/push-2.js
     * @example-ref examples/node/push-3.js
     *
     * @param {object} params JSON with the parameters
     * @param {string} params.key Id or path to the nodes
     * @param {string[]} params.keys Array of ids or paths to the nodes
     * @param {string} params.target Branch to push nodes to
     * @param {boolean} [params.includeChildren=false] Also push children of given nodes
     * @param {boolean} [params.resolve=true] Resolve dependencies before pushing, meaning that references will also be pushed
     * @param {string[]} [params.exclude] Array of ids or paths to nodes not to be pushed (nodes needed to maintain data integrity (e.g parents must be present in target) will be pushed anyway)
     *
     * @returns {object} PushNodesResult
     */
    push(params: PushNodeParams): PushNodesResult {
        checkRequired(params, 'target');

        const {
            key,
            keys,
            target,
            includeChildren = false,
            resolve = true,
            exclude,
        } = params ?? {};

        if (typeof key === 'undefined' && typeof keys === 'undefined') {
            throw "Parameter key' or 'keys' is required";
        }

        const handlerParams = __.newBean<PushNodeHandlerParams>('com.enonic.xp.lib.node.PushNodeHandlerParams');

        handlerParams.setKey(__.nullOrValue(key));
        handlerParams.setKeys(__.nullOrValue(keys));
        handlerParams.setTargetBranch(target);
        handlerParams.setIncludeChildren(includeChildren);
        handlerParams.setExclude(__.nullOrValue(exclude));
        handlerParams.setResolve(resolve);

        return __.toNativeObject(this.nodeHandler.push(handlerParams));
    }

    /**
     * This function resolves the differences for node between current and given branch
     *
     * @example-ref examples/node/diff-1.js
     *
     * @param {object} params JSON with the parameters.
     * @param {string} params.key Path or id to resolve diff for
     * @param {string} params.target Branch to diff against.
     * @param {boolean} [params.includeChildren=false] also resolve dependencies for children
     *
     * @returns {object} DiffNodesResult
     */
    diff(params: DiffBranchesParams): DiffBranchesResult {
        const {
            key,
            target,
            includeChildren = false,
        } = params ?? {};

        const handlerParams = __.newBean<DiffBranchesHandlerParams>('com.enonic.xp.lib.node.DiffBranchesHandlerParams');

        handlerParams.setKey(key);
        handlerParams.setTargetBranch(target);
        handlerParams.setIncludeChildren(includeChildren);

        return __.toNativeObject(this.nodeHandler.diff(handlerParams));
    }

    /**
     * This function returns a binary stream.
     *
     * @example-ref examples/node/getBinary.js
     * @param {string} params.key Path or id to the node.
     * @param {string} params.binaryReference to the binary.
     *
     * @returns {*} Stream of the binary.
     */
    getBinary(params: GetBinaryParams): object {
        checkRequired(params, 'key');
        return this.nodeHandler.getBinary(params.key, params.binaryReference);
    }

    /**
     * Rename a node or move it to a new path.
     *
     * @example-ref examples/node/move-1.js
     * @example-ref examples/node/move-2.js
     * @example-ref examples/node/move-3.js
     *
     * @param {object} params JSON with the parameters.
     * @param {string} params.source Path or id of the node to be moved or renamed.
     * @param {string} params.target New path or name for the node. If the target ends in slash '/', it specifies the parent path where to be moved. Otherwise it means the new desired path or name for the node.
     *
     * @returns {boolean} True if the node was successfully moved or renamed, false otherwise.
     */
    move(params: MoveNodeParams): boolean {
        checkRequired(params, 'source');
        checkRequired(params, 'target');

        return __.toNativeObject(this.nodeHandler.move(params.source, params.target));
    }

    /**
     * Set node's children order
     *
     * @example-ref examples/node/setChildOrder.js
     *
     * @param {object} params JSON with the parameters.
     * @param {string} params.key node's path or id
     * @param {string} params.childOrder children order
     * @returns {object} updated node
     */
    setChildOrder(params: SetChildOrderParams): Node {
        checkRequired(params, 'key');
        checkRequired(params, 'childOrder');

        return __.toNativeObject(this.nodeHandler.setChildOrder(params.key, params.childOrder));
    }

    /**
     * This command queries nodes.
     *
     * @example-ref examples/node/query.js
     *
     * @param {object} params JSON with the parameters.
     * @param {number} [params.start=0] Start index (used for paging).
     * @param {number} [params.count=10] Number of contents to fetch.
     * @param {string|object} [params.query] Query expression.
     * @param {object} [params.filters] Query filters
     * @param {string|object|object[]} [params.sort='_score DESC'] Sorting expression.
     * @param {string} [params.aggregations] Aggregations expression.
     * @param {string} [params.highlight] Highlighting parameters.
     * @param {boolean} [params.explain=false] Return score calculation explanation.
     * @returns {object} Result of query.
     */
    query(params: QueryNodeParams): NodeQueryResult {
        const {
            start = 0,
            count = 10,
            query,
            sort,
            aggregations,
            suggestions,
            highlight,
            filters,
            explain = false,
        } = params ?? {};

        const handlerParams = __.newBean<QueryNodeHandlerParams>('com.enonic.xp.lib.node.QueryNodeHandlerParams');

        handlerParams.setStart(start);
        handlerParams.setCount(count);
        handlerParams.setQuery(__.toScriptValue((query)));
        handlerParams.setSort(__.toScriptValue(sort));
        handlerParams.setAggregations(__.toScriptValue(aggregations));
        handlerParams.setSuggestions(__.toScriptValue(suggestions));
        handlerParams.setHighlight(__.toScriptValue(highlight));
        handlerParams.setFilters(__.toScriptValue(filters));
        handlerParams.setExplain(explain);

        return __.toNativeObject(this.nodeHandler.query(handlerParams));
    }

    /**
     * Check if node exists.
     *
     * @example-ref examples/node/exists.js
     *
     * @param {string} [key] node path or id.
     *
     * @returns {boolean} True if exist, false otherwise.
     */
    exists(key: string): boolean {
        return __.toNativeObject(this.nodeHandler.exist(key));
    }

    /**
     * This function returns node versions.
     *
     * @example-ref examples/node/findVersions.js
     * @param {object} params JSON parameters.
     * @param {string} params.key Path or ID of the node.
     * @param {number} [params.start=0] Start index (used for paging).
     * @param {number} [params.count=10] Number of node versions to fetch.
     *
     * @returns {object[]} Node versions.
     */
    findVersions(params: FindVersionsParams): NodeVersionsQueryResult {
        checkRequired(params, 'key');

        const {
            key,
            start = 0,
            count = 10,
        } = params ?? {};

        const handlerParams = __.newBean<FindVersionsHandlerParams>('com.enonic.xp.lib.node.FindVersionsHandlerParams');

        handlerParams.setKey(key);
        handlerParams.setStart(start);
        handlerParams.setCount(count);

        return __.toNativeObject(this.nodeHandler.findVersions(handlerParams));
    }

    /**
     * This function returns the active version of a node.
     *
     * @example-ref examples/node/getActiveVersion.js
     * @param {object} params JSON parameters.
     * @param {string} params.key Path or ID of the node.
     *
     * @returns {object} Active content versions per branch.
     */
    getActiveVersion(params: GetActiveVersionParams): NodeVersion | null {
        checkRequired(params, 'key');

        return __.toNativeObject(this.nodeHandler.getActiveVersion(params.key));
    }

    /**
     * This function sets the active version of a node.
     *
     * @example-ref examples/node/setActiveVersion.js
     * @param {object} params JSON parameters.
     * @param {string} params.key Path or ID of the node.
     * @param {string} params.versionId Version to set as active.
     *
     * @returns {boolean} True if deleted, false otherwise.
     */
    setActiveVersion(params: SetActiveVersionParams): boolean {
        checkRequired(params, 'key');
        checkRequired(params, 'versionId');

        return __.toNativeObject(this.nodeHandler.setActiveVersion(params.key, params.versionId));
    }

    /**
     * Get children for given node.
     *
     * @example-ref examples/node/findChildren.js
     *
     * @param {object} params JSON with the parameters.
     * @param {number} params.parentKey path or id of parent to get children of
     * @param {number} [params.start=0] Start index (used for paging).
     * @param {number} [params.count=10] Number of contents to fetch.
     * @param {string} [params.childOrder] How to order the children (defaults to value stored on parent)
     * @param {boolean} [params.countOnly=false] Optimize for count children only ( no children returned )
     * @param {boolean} [params.recursive=false] Do recursive fetching of all children of children
     * @returns {object} Result of getChildren.
     */
    findChildren(params: FindChildrenParams): FindNodesByParentResult {
        checkRequired(params, 'parentKey');

        const {
            parentKey,
            start = 0,
            count = 10,
            childOrder,
            countOnly = false,
            recursive = false,
        } = params ?? {};

        const handlerParams = __.newBean<FindChildrenHandlerParams>('com.enonic.xp.lib.node.FindChildrenHandlerParams');

        handlerParams.setParentKey(parentKey);
        handlerParams.setStart(start);
        handlerParams.setCount(count);
        handlerParams.setChildOrder(childOrder);
        handlerParams.setCountOnly(countOnly);
        handlerParams.setRecursive(recursive);

        return __.toNativeObject(this.nodeHandler.findChildren(handlerParams));
    }

    /**
     * Refresh the index for the current repoConnection
     *
     * @example-ref examples/node/refresh.js
     *
     * @param {string} [mode]=ALL Refresh all (ALL) data, or just the search-index (SEARCH), or the storage-index (STORAGE)
     */
    refresh(mode: RefreshMode = 'ALL'): void {
        this.nodeHandler.refresh(mode);
    }

    /**
     * Set the root node permissions and inherit.
     *
     * @example-ref examples/node/modifyRootPermissions.js
     *
     * @param {object} params JSON with the parameters.
     * @param {object} params._permissions the permission json
     * @param {object} [params._inheritsPermissions]= true if the permissions should be inherited to children
     *
     * @returns {object} Updated root-node as JSON.
     */
    setRootPermissions(params: SetRootPermissionsParams): Node {
        checkRequired(params, '_permissions');

        return __.toNativeObject(this.nodeHandler.setRootPermissions(__.toScriptValue(params)));
    }

    /**
     * This function commits the active version of nodes.
     *
     * @example-ref examples/node/commit.js
     *
     * @param {...(string|string[])} params.keys Node keys to commit. Each argument could be an id, a path or an array of the two. Prefer the usage of ID rather than paths.
     * @param {string} [params.message] Commit message.
     *
     * @returns {object} Commit object.
     */
    commit(params: CommitParams): NodeCommit {
        const keys: string[] = Array.isArray(params.keys) ? params.keys : [params.keys];

        return __.toNativeObject(this.nodeHandler.commit(argsToStringArray(keys), __.nullOrValue(params.message)));
    }

    /**
     * This function fetches commit by id.
     *
     * @example-ref examples/node/commit.js
     *
     * @param {string} params.id existing commit id.
     *
     * @returns {object} Commit object.
     */
    getCommit(params: GetCommitParams): NodeCommit | null {
        checkRequired(params, 'id');
        return __.toNativeObject(this.nodeHandler.getCommit(params.id));
    }
}

export interface MultiRepoConnection {
    query(params: QueryNodeParams): NodeMultiRepoQueryResult;
}

/**
 * Creates a new multirepo-connection.
 *
 * @constructor
 * @hideconstructor
 * @alias MultiRepoConnection
 */
class MultiRepoConnectionImpl
    implements MultiRepoConnection {

    constructor(private multiRepoConnection: MultiRepoNodeHandler) {
    }

    /**
     * This command queries nodes in a multi-repo connection.
     *
     * @example-ref examples/node/multiRepoQuery.js
     *
     * @param {object} params JSON with the parameters.
     * @param {number} [params.start=0] Start index (used for paging).
     * @param {number} [params.count=10] Number of contents to fetch.
     * @param {string|object} [params.query] Query expression.
     * @param {object} [params.filters] Query filters
     * @param {string|object|object[]} [params.sort='_score DESC'] Sorting expression.
     * @param {string} [params.aggregations] Aggregations expression.
     * @param {string} [params.highlight] Highlighting parameters.
     * @param {boolean} [params.explain=false] Return score calculation explanation.
     * @returns {object} Result of query.
     */
    query(params: QueryNodeParams): NodeMultiRepoQueryResult {
        const {
            start = 0,
            count = 10,
            query,
            sort,
            aggregations,
            suggestions,
            highlight,
            filters,
            explain = false,
        } = params ?? {};

        const handlerParams = __.newBean<QueryNodeHandlerParams>('com.enonic.xp.lib.node.QueryNodeHandlerParams');

        handlerParams.setStart(start);
        handlerParams.setCount(count);
        handlerParams.setQuery(__.toScriptValue((query)));
        handlerParams.setSort(__.toScriptValue(sort));
        handlerParams.setAggregations(__.toScriptValue(aggregations));
        handlerParams.setSuggestions(__.toScriptValue(suggestions));
        handlerParams.setHighlight(__.toScriptValue(highlight));
        handlerParams.setFilters(__.toScriptValue(filters));
        handlerParams.setExplain(explain);

        return __.toNativeObject(this.multiRepoConnection.query(handlerParams));
    }
}

export interface ConnectParams {
    repoId: string;
    branch: string;
    principals: string[];
    user: {
        login: string;
        idProvider: string;
    };
}

interface NodeHandleContext {
    setRepoId(value: string): void;

    setBranch(value: string): void;

    setUsername(value: string): void;

    setIdProvider(value: string): void;

    setPrincipals(value: string[]): void;
}

/**
 * Creates a connection to a repository with a given branch and authentication info.
 *
 * @example-ref examples/node/connect.js
 *
 * @param {object} params JSON with the parameters.
 * @param {object} params.repoId repository id
 * @param {object} params.branch branch id
 * @param {object} [params.user] User to execute the callback with. Default is the current user.
 * @param {string} params.user.login Login of the user.
 * @param {string} [params.user.idProvider] Id provider containing the user. By default, all the id providers will be used.
 * @param {string[]} [params.principals] Additional principals to execute the callback with.
 * @returns {RepoConnection} Returns a new repo-connection.
 */
export function connect(params: ConnectParams): RepoConnection {
    checkRequired(params, 'repoId');
    checkRequired(params, 'branch');

    const nodeHandleContext = __.newBean<NodeHandleContext>('com.enonic.xp.lib.node.NodeHandleContext');
    nodeHandleContext.setRepoId(params.repoId);
    nodeHandleContext.setBranch(params.branch);

    if (params.user) {
        if (params.user.login) {
            nodeHandleContext.setUsername(params.user.login);
        }
        if (params.user.idProvider) {
            nodeHandleContext.setIdProvider(params.user.idProvider);
        }
    }

    if (params.principals) {
        nodeHandleContext.setPrincipals(params.principals);
    }

    return new RepoConnectionImpl(factory.create(nodeHandleContext));
}

export interface MultiRepoConnectParams {
    sources: ConnectParams[];
}

/**
 * Creates a connection to several repositories with a given branch and authentication info.
 *
 * @example-ref examples/node/multiRepoConnect.js
 *
 * @param {object} params JSON with the parameters.
 * @param {object[]} params.sources array of sources to connect to
 * @param {object} params.sources.repoId repository id
 * @param {object} params.sources.branch branch id
 * @param {object} [params.sources.user] User to execute the callback with. Default is the current user.
 * @param {string} params.sources.user.login Login of the user.
 * @param {string} [params.sources.user.idProvider] Id provider containing the user. By default, all the id providers will be used.
 * @param {string[]} [params.sources.principals] Additional principals to execute the callback with.
 *
 * @returns {MultiRepoConnection} Returns a new multirepo-connection.
 */
export function multiRepoConnect(params: MultiRepoConnectParams): MultiRepoConnection {
    const multiRepoNodeHandleContext = __.newBean<MultiRepoNodeHandleContext>('com.enonic.xp.lib.node.MultiRepoNodeHandleContext');

    params.sources.forEach(function (source: ConnectParams) {
        checkRequired(source, 'repoId');
        checkRequired(source, 'branch');
        checkRequired(source, 'principals');
        multiRepoNodeHandleContext.addSource(source.repoId, source.branch, source.principals);
    });

    return new MultiRepoConnectionImpl(multiRepoConnectFactory.create(multiRepoNodeHandleContext));
}
