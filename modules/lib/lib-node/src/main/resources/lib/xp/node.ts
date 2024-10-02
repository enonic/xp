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

import type {
    Aggregation,
    Aggregations,
    AggregationsResult,
    AggregationsToAggregationResults,
    ByteSource,
    Filter,
    Highlight,
    HighlightResult,
    PrincipalKey,
    QueryDsl,
    ScriptValue,
    SortDsl,
} from '@enonic-types/core';

export type {
    Aggregation,
    Aggregations,
    AggregationsResult,
    BooleanDslExpression,
    BooleanFilter,
    Bucket,
    BucketsAggregationResult,
    BucketsAggregationsUnion,
    ByteSource,
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
    PrincipalKey,
    QueryDsl,
    RangeDslExpression,
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

type WithRequiredProperty<T, K extends keyof T> = T & { [P in K]-?: T[P] };

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

function assertStringArray(value: unknown, name: string): asserts value is string[] {
    if (!Array.isArray(value)) {
        throw new TypeError(`${name} must be an array of strings! Isn't even an array!`);
    }

    if (!value.every(item => typeof item === 'string')) {
        throw new TypeError(`${name} must be an array of strings! Is an array, but contains non-string elements!`);
    }
}

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

export interface Explanation {
    value: number;
    description: string;
    details: Explanation[];
}

export interface NodeQueryResultHit {
    id: string;
    score: number;
    explanation?: Explanation;
    highlight?: HighlightResult;
}

export interface SuggestionResult {
    text: string;
    length: number;
    offset: number;
    options: {
        text: string;
        score: number;
        freq?: number; // only for term
    }[];
}

export interface NodeQueryResult<AggregationOutput extends Record<string, AggregationsResult> | undefined = undefined> {
    total: number;
    count: number;
    hits: NodeQueryResultHit[];
    aggregations: AggregationOutput;
    suggestions?: Record<string, SuggestionResult[]>;
}

export interface NodeMultiRepoQueryResult<AggregationOutput extends Record<string, AggregationsResult> | undefined = undefined> {
    total: number;
    count: number;
    hits: (NodeQueryResultHit & {
        repoId: string;
        branch: string;
    })[];
    aggregations: AggregationOutput;
    suggestions?: Record<string, SuggestionResult[]>;
}

interface NodeHandleFactory {
    create(context: NodeHandleContext): NodeHandler;
}

interface MultiRepoNodeHandleContext {
    addSource(repoId: string, branch: string, principals: PrincipalKey[]): void;
}

interface MultiRepoNodeHandleFactory {
    create(context: MultiRepoNodeHandleContext): MultiRepoNodeHandler;
}

const factory: NodeHandleFactory = __.newBean<NodeHandleFactory>('com.enonic.xp.lib.node.NodeHandleFactory');

const multiRepoConnectFactory: MultiRepoNodeHandleFactory = __.newBean<MultiRepoNodeHandleFactory>('com.enonic.xp.lib.node.MultiRepoNodeHandleFactory');

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

const isString = (value: unknown): value is string => value instanceof String || typeof value === 'string';

function isObject(value: unknown): value is object {
    return typeof value !== 'undefined' && value !== null && typeof value === 'object' && value.constructor === Object;
}

function prepareGetParams(params: (string | GetNodeParams | (string | GetNodeParams)[])[], bean: GetNodeHandlerParams): void {
    params.forEach(param => {
        if (isString(param)) {
            bean.add(param);
        } else if (Array.isArray(param)) {
            prepareGetParams(param, bean);
        } else if (isObject(param)) {
            checkRequired(param, 'key');
            bean.add(param.key, __.nullOrValue(param.versionId));
        } else {
            throw 'Unsupported type';
        }
    });
}

interface MultiRepoNodeHandler {
    query<
        AggregationInput extends Record<string, Aggregation> = never
    >(params: QueryNodeHandlerParams): NodeMultiRepoQueryResult<AggregationsToAggregationResults<AggregationInput>>;
}

interface NodeHandler {
    create<NodeData>(node: ScriptValue): Node<NodeData>;

    modify<NodeData>(editor: ScriptValue, key: string): Node<NodeData>;

    setChildOrder<NodeData>(key: string, childOrder: string): Node<NodeData>;

    get<NodeData>(params: GetNodeHandlerParams): Node<NodeData> | Node<NodeData>[] | null;

    delete(keys: string[]): string[];

    push(params: PushNodeHandlerParams): PushNodesResult;

    diff(params: DiffBranchesHandlerParams): DiffBranchesResult;

    move(source: string, target: string): boolean;

    query<
        AggregationInput extends Aggregations = never
    >(params: QueryNodeHandlerParams): NodeQueryResult<AggregationsToAggregationResults<AggregationInput>>;

    exist(key: string): boolean;

    findVersions(params: FindVersionsHandlerParams): NodeVersionsQueryResult;

    getActiveVersion(key: string): NodeVersion | null;

    setActiveVersion(key: string, versionId: string): boolean;

    findChildren(params: FindChildrenHandlerParams): FindNodesByParentResult;

    commit(keys: string[], message?: string | null): NodeCommit;

    getCommit(commitId: string): NodeCommit | null;

    setRootPermissions<NodeData>(v: ScriptValue): Node<NodeData>;

    applyPermissions(key: string, permissions: ScriptValue, addPermissions: ScriptValue, removePermissions: ScriptValue, branches: string[],
                     scope: string): ApplyPermissionsResult;

    getBinary(key: string, binaryReference?: string | null): ByteSource;

    refresh(mode: RefreshMode): void;

    duplicate<NodeData>(params: DuplicateNodeHandlerParams): Node<NodeData>;
}

export type CreateNodeParams<NodeData = unknown> = NodePropertiesOnCreate & NodeData;

export interface ModifyNodeParams<NodeData = unknown> {
    key: string;
    editor: (node: Node<NodeData>) => ModifiedNode<NodeData>;
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

export interface QueryNodeParams<AggregationInput extends Aggregations = never> {
    start?: number;
    count?: number;
    query?: QueryDsl | string;
    sort?: string | SortDsl | SortDsl[];
    filters?: Filter | Filter[];
    aggregations?: AggregationInput;
    suggestions?: Record<string, TermSuggestion>;
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
    commitId?: string;
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
    childOrder?: string;
    countOnly?: boolean;
    recursive?: boolean;
}

interface FindChildrenHandlerParams {
    setParentKey(parentKey: string): void;

    setStart(start?: number | null): void;

    setCount(count?: number | null): void;

    setChildOrder(childOrder: string): void;

    setCountOnly(countOnly: boolean): void;

    setRecursive(recursive: boolean): void;
}

export interface DuplicateParams<NodeData = Record<string, unknown>> {
    nodeId: string;
    name?: string;
    parent?: string;
    includeChildren?: boolean
    dataProcessor?: (v: NodeData) => NodeData;
    refresh?: RefreshMode;
}

interface DuplicateNodeHandlerParams {
    setNodeId(value: string): void;

    setName(value?: string): void;

    setParent(value?: string): void;

    setIncludeChildren(value: boolean): void;

    setDataProcessor(value?: ScriptValue): void;

    setRefresh(value?: string): void;
}

export interface FindNodesByParentResult {
    total: number;
    count: number;
    hits: {
        id: string;
    }[];
}

export interface ApplyPermissionsParams {
    key: string;
    permissions?: AccessControlEntry[];
    addPermissions?: AccessControlEntry[];
    removePermissions?: AccessControlEntry[];
    branches?: string[];
    scope?: string;
}

export interface ApplyPermissionsResult {
    [nodeId: string]: BranchResult[];
}

export interface BranchResult {
    branch: string;
    node: Node;
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

/**
 * @deprecated
 */
export interface SetRootPermissionsParams {
    _permissions: AccessControlEntry[];
}

export type Permission = 'READ' | 'CREATE' | 'MODIFY' | 'DELETE' | 'PUBLISH' | 'READ_PERMISSIONS' | 'WRITE_PERMISSIONS';

export interface AccessControlEntry {
    principal: PrincipalKey;
    allow?: Permission[];
    deny?: Permission[];
}

export interface NodeIndexConfig {
    analyzer?: string;
    default?: NodeConfigEntry;
    configs: {
        path: string;
        config: NodeConfigEntry;
    }[];
}

export type NodeIndexConfigTemplates =
    | 'none'
    | 'byType'
    | 'fulltext'
    | 'path'
    | 'minimal';

export interface NodeIndexConfigParams {
    analyzer?: string;
    default?: Partial<NodeConfigEntry> | NodeIndexConfigTemplates;
    configs?: {
        path: string;
        config: Partial<NodeConfigEntry> | NodeIndexConfigTemplates;
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

export type CommonNodeProperties = {
    _childOrder: string;
    // _id: string; // Not on create
    // _indexConfig: Partial<NodeIndexConfigParams> | NodeIndexConfigParams | NodeIndexConfig; // Different on read vs write
    _manualOrderValue?: number; // Notice optional
    _name: string;
    _nodeType: string;
    // _parentPath?: string; // Only on create
    _path: string;
    _permissions: AccessControlEntry[];
    _state: string;
    _ts: string;
    _versionKey: string;
};

export type NodePropertiesOnCreate = Partial<CommonNodeProperties> & {
    _indexConfig?: Partial<NodeIndexConfigParams>;
    _parentPath?: string;
    _inheritsPermissions?: boolean;
};

export type NodePropertiesOnModify = CommonNodeProperties & {
    _id: string;
    _indexConfig: NodeIndexConfigParams;
    _parentPath?: never;
};

export type NodePropertiesOnRead = CommonNodeProperties & {
    _id: string;
    _indexConfig: NodeIndexConfig;
    _parentPath?: never;
};

export type ModifiedNode<Data = Record<string, unknown>> = NodePropertiesOnModify & Data;

export type Node<Data = Record<string, unknown>> = NodePropertiesOnRead & Data;

export interface RepoConnection {
    create<NodeData = Record<string, unknown>>(params: CreateNodeParams<NodeData>): Node<NodeData>;

    modify<NodeData = Record<string, unknown>>(params: ModifyNodeParams<NodeData>): Node<NodeData>;

    get<NodeData = Record<string, unknown>>(key: string | GetNodeParams): Node<NodeData> | null;

    get<NodeData = Record<string, unknown>>(keys: (string | GetNodeParams)[]): Node<NodeData> | Node<NodeData>[] | null;

    get<NodeData = Record<string, unknown>>(...keys: (string | GetNodeParams | (string | GetNodeParams)[])[]): Node<NodeData> | Node<NodeData>[] | null;

    delete(...keys: (string | string[])[]): string[];

    push(params: PushNodeParams): PushNodesResult;

    diff(params: DiffBranchesParams): DiffBranchesResult;

    getBinary(params: GetBinaryParams): ByteSource;

    move(params: MoveNodeParams): boolean;

    setChildOrder<NodeData = Record<string, unknown>>(params: SetChildOrderParams): Node<NodeData>;

    query<
        AggregationInput extends Aggregations = never
    >(params: QueryNodeParams<AggregationInput>): NodeQueryResult<AggregationsToAggregationResults<AggregationInput>>;

    exists(key: string): boolean;

    findVersions(params: FindVersionsParams): NodeVersionsQueryResult;

    getActiveVersion(params: GetActiveVersionParams): NodeVersion | null;

    setActiveVersion(params: SetActiveVersionParams): boolean;

    findChildren(params: FindChildrenParams): FindNodesByParentResult;

    refresh(mode?: RefreshMode): void;

    setRootPermissions<NodeData = Record<string, unknown>>(params: SetRootPermissionsParams): Node<NodeData>;

    applyPermissions(params: ApplyPermissionsParams): ApplyPermissionsResult;

    commit(params: CommitParams): NodeCommit;

    getCommit(params: GetCommitParams): NodeCommit | null;

    duplicate<NodeData = Record<string, unknown>>(params: DuplicateParams<NodeData>): Node<NodeData>;
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
     * @param {object} [params._permissions] The access control list for the node. By the default the creator will have full access
     * @param {boolean} [params._inheritsPermissions] true if the permissions should be inherited from the node parent. Default is false.
     * @param {number} [params._manualOrderValue] Value used to order document when ordering by parent and child-order is set to manual
     * @param {string} [params._childOrder] Default ordering of children when doing getChildren if no order is given in query
     *
     * @returns {object} Node created as JSON.
     */
    create<NodeData = Record<string, unknown>>(params: CreateNodeParams<NodeData>): Node<NodeData> {
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
    modify<NodeData = Record<string, unknown>>(params: ModifyNodeParams<NodeData>): Node<NodeData> {
        checkRequired(params, 'key');

        return __.toNativeObject(this.nodeHandler.modify(__.toScriptValue(params.editor), params.key));
    }

    get<NodeData = Record<string, unknown>>(keys: string | GetNodeParams): Node<NodeData> | null;
    get<NodeData = Record<string, unknown>>(keys: (string | GetNodeParams)[]): Node<NodeData>[] | null;
    get<NodeData = Record<string, unknown>>(...keys: (string | GetNodeParams | (string | GetNodeParams)[])[]): Node<NodeData>[] | null;
    /**
     * This function fetches nodes.
     *
     * @example-ref examples/node/get-1.js
     * @example-ref examples/node/get-2.js
     * @example-ref examples/node/get-3.js
     *
     * @param {...(string|object|Array.<(string|object)>)} keys to fetch. Each argument could be an id, a path, an object with key and versionId properties or an array of them.
     *
     * @returns {object} The node or node array (as JSON) fetched from the repository.
     */
    get<NodeData = Record<string, unknown>>(...keys: (string | GetNodeParams | (string | GetNodeParams)[])[]): Node<NodeData> | Node<NodeData>[] | null {
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

        const handlerParams: PushNodeHandlerParams = __.newBean<PushNodeHandlerParams>('com.enonic.xp.lib.node.PushNodeHandlerParams');

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

        const handlerParams: DiffBranchesHandlerParams = __.newBean<DiffBranchesHandlerParams>('com.enonic.xp.lib.node.DiffBranchesHandlerParams');

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
    getBinary(params: GetBinaryParams): ByteSource {
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
    setChildOrder<NodeData = Record<string, unknown>>(params: SetChildOrderParams): Node<NodeData> {
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
    query<
        AggregationInput extends Aggregations = never
    >(params: QueryNodeParams<AggregationInput>): NodeQueryResult<AggregationsToAggregationResults<AggregationInput>> {
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

        const handlerParams: QueryNodeHandlerParams = __.newBean<QueryNodeHandlerParams>('com.enonic.xp.lib.node.QueryNodeHandlerParams');

        handlerParams.setStart(start);
        handlerParams.setCount(count);
        handlerParams.setQuery(__.toScriptValue((query)));
        handlerParams.setSort(__.toScriptValue(sort));
        handlerParams.setAggregations(__.toScriptValue(aggregations));
        handlerParams.setSuggestions(__.toScriptValue(suggestions));
        handlerParams.setHighlight(__.toScriptValue(highlight));
        handlerParams.setFilters(__.toScriptValue(filters));
        handlerParams.setExplain(explain);

        return __.toNativeObject(this.nodeHandler.query<AggregationInput>(handlerParams));
    }

    /**
     * Check if node exists.
     *
     * @example-ref examples/node/exists.js
     *
     * @param {string} [key] node path or id.
     *
     * @returns {boolean} True if exists, false otherwise.
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

        const handlerParams: FindVersionsHandlerParams = __.newBean<FindVersionsHandlerParams>('com.enonic.xp.lib.node.FindVersionsHandlerParams');

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

        const handlerParams: FindChildrenHandlerParams = __.newBean<FindChildrenHandlerParams>('com.enonic.xp.lib.node.FindChildrenHandlerParams');

        handlerParams.setParentKey(parentKey);
        handlerParams.setStart(start);
        handlerParams.setCount(count);
        if (childOrder != null) {
            handlerParams.setChildOrder(childOrder);
        }
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
     * @deprecated
     *
     * Set the root node permissions and inherit.
     *
     * @example-ref examples/node/modifyRootPermissions.js
     *
     * @param {object} params JSON with the parameters.
     * @param {object} params._permissions the permission json
     *
     * @returns {object} Updated root-node as JSON.
     */
    setRootPermissions<NodeData = Record<string, unknown>>(params: SetRootPermissionsParams): Node<NodeData> {
        checkRequired(params, '_permissions');

        return __.toNativeObject(this.nodeHandler.setRootPermissions(__.toScriptValue(params)));
    }

    /**
     * Apply permissions to a node.
     *
     * @example-ref examples/node/applyPermissions.js
     *
     * @param {object} params JSON with the parameters.
     * @param {string} params.key Path or ID of the node.
     * @param {object} [params.permissions] the permissions json
     * @param {object} [params.addPermissions] the permissions to add json
     * @param {object} [params.removePermissions] the permissions to remove json
     * @param {string[]} [params.branches] Additional branches to apply permissions to. Current context branch should not be included.
     * @param {string} [params.scope] Scope of operation. Possible values are 'SINGE', 'TREE' or 'CHILDREN'. Default is 'SINGLE'.
     *
     * @returns {object} Result of the apply permissions operation.
     */

    applyPermissions(params: ApplyPermissionsParams): ApplyPermissionsResult {
        checkRequired(params, 'key');

        const branches = params.branches != null ? params.branches : [];
        const scope = params.scope != null ? params.scope : 'SINGLE';

        return __.toNativeObject(this.nodeHandler.applyPermissions(params.key, __.toScriptValue(params.permissions),
            __.toScriptValue(params.addPermissions), __.toScriptValue(params.removePermissions), branches, scope));
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

    /**
     * This function duplicates a node.
     *
     * @example-ref examples/node/duplicate.js
     *
     * @param {object} params JSON parameters.
     * @param {string} params.nodeId Id to the node.
     * @param {string} [params.name] New node name.
     * @param {boolean} [params.includeChildren=true] Indicates that children nodes must be duplicated, too.
     * @param {string} [params.parent] Destination parent path. By default, a duplicated node will be added as a sibling of the source node.
     * @param {string} [params.refresh] Refresh the index for the current repoConnection.
     * @param {function} [params.dataProcessor] Node data processor.
     *
     * @returns {object} Duplicated node.
     */
    duplicate<NodeData = Record<string, unknown>>(params: DuplicateParams<NodeData>): Node<NodeData> {
        checkRequired(params, 'nodeId');

        const {
            nodeId,
            name,
            includeChildren = true,
            parent,
            dataProcessor,
            refresh,
        } = params ?? {};

        const handlerParams: DuplicateNodeHandlerParams = __.newBean<DuplicateNodeHandlerParams>('com.enonic.xp.lib.node.DuplicateNodeHandlerParams');

        handlerParams.setNodeId(nodeId);
        handlerParams.setIncludeChildren(includeChildren);
        if (name != null) {
            handlerParams.setName(name);
        }
        if (parent != null) {
            handlerParams.setParent(parent);
        }
        if (refresh != null) {
            handlerParams.setRefresh(refresh);
        }
        handlerParams.setDataProcessor(__.toScriptValue(dataProcessor));

        return __.toNativeObject(this.nodeHandler.duplicate(handlerParams));
    }
}

export interface MultiRepoConnection {
    query<
        AggregationInput extends Aggregations = never
    >(params: QueryNodeParams<AggregationInput>): NodeMultiRepoQueryResult<AggregationsToAggregationResults<AggregationInput>>;
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
    query<
        AggregationInput extends Aggregations = never
    >(params: QueryNodeParams<AggregationInput>): NodeMultiRepoQueryResult<AggregationsToAggregationResults<AggregationInput>> {
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

        const handlerParams: QueryNodeHandlerParams = __.newBean<QueryNodeHandlerParams>('com.enonic.xp.lib.node.QueryNodeHandlerParams');

        handlerParams.setStart(start);
        handlerParams.setCount(count);
        handlerParams.setQuery(__.toScriptValue((query)));
        handlerParams.setSort(__.toScriptValue(sort));
        handlerParams.setAggregations(__.toScriptValue(aggregations));
        handlerParams.setSuggestions(__.toScriptValue(suggestions));
        handlerParams.setHighlight(__.toScriptValue(highlight));
        handlerParams.setFilters(__.toScriptValue(filters));
        handlerParams.setExplain(explain);

        return __.toNativeObject(this.multiRepoConnection.query<AggregationInput>(handlerParams));
    }
}

export interface ConnectParams {
    repoId: string;
    branch: string;
    principals?: PrincipalKey[];
    user?: {
        login: string;
        idProvider?: string;
    };
}

interface NodeHandleContext {
    setRepoId(value: string): void;

    setBranch(value: string): void;

    setUsername(value: string): void;

    setIdProvider(value: string): void;

    setPrincipals(value: string[] | null): void;
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

    const nodeHandleContext: NodeHandleContext = __.newBean<NodeHandleContext>('com.enonic.xp.lib.node.NodeHandleContext');
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

    nodeHandleContext.setPrincipals(params.principals ?? null);

    return new RepoConnectionImpl(factory.create(nodeHandleContext));
}

export interface MultiRepoConnectParams {
    sources: WithRequiredProperty<ConnectParams, 'principals'>[];
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
 * @param {string[]} params.sources.principals Principals to execute the callback with.
 *
 * @returns {MultiRepoConnection} Returns a new multirepo-connection.
 */
export function multiRepoConnect(params: MultiRepoConnectParams): MultiRepoConnection {
    const multiRepoNodeHandleContext: MultiRepoNodeHandleContext = __.newBean<MultiRepoNodeHandleContext>('com.enonic.xp.lib.node.MultiRepoNodeHandleContext');

    params.sources.forEach((source: ConnectParams) => {
        checkRequired(source, 'repoId');
        checkRequired(source, 'branch');
        checkRequired(source, 'principals');
        assertStringArray(source.principals, 'principals');
        multiRepoNodeHandleContext.addSource(source.repoId, source.branch, source.principals);
    });

    return new MultiRepoConnectionImpl(multiRepoConnectFactory.create(multiRepoNodeHandleContext));
}
