/**
 * Functions to get, query and manipulate nodes.
 *
 * @example
 * var nodeLib = require('/lib/xp/node');
 *
 * @module node
 */


var factory = __.newBean('com.enonic.xp.lib.node.NodeHandleFactory');

var multiRepoConnectfactory = __.newBean('com.enonic.xp.lib.node.MultiRepoNodeHandleFactory');

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }
    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }
    return value;
}

function valueOrDefault(value, defaultValue) {
    if (value === undefined) {
        return defaultValue;
    }
    return value;
}

function argsToStringArray(argsArray) {

    var array = [];

    for (var i = 0; i < argsArray.length; i++) {
        var currArgument = argsArray[i];
        if (Array.isArray(currArgument)) {
            currArgument.forEach(function (v) {
                array.push(v);
            }, this);
        } else {
            array.push(currArgument);
        }
    }
    return array;
}

function isString(value) {
    return typeof value === 'string' || value instanceof String;
}

function isObject(value) {
    return value && typeof value === 'object' && value.constructor === Object;
}

function prepareGetParams(params, bean) {
    for (let i = 0; i < params.length; i++) {
        let param = params[i];
        if (isString(param)) {
            bean.add(param);
        } else if (isObject(param)) {
            bean.add(required(param, 'key'), nullOrValue(param.versionId));
        } else if (Array.isArray(param)) {
            prepareGetParams(param, bean);
        } else {
            throw 'Unsupported type';
        }
    }
}

/**
 * Creates a new repo-connection.
 *
 * @returns {*} native Native repo-connection object.
 * @constructor
 */
function RepoConnection(repoConnection) {
    this.repoConnection = repoConnection;
}

/**
 * Creates a new multirepo-connection.
 *
 * @returns {*} native Native multirepo-connection object.
 * @constructor
 */
function MultiRepoConnection(multiRepoConnection) {
    this.multiRepoConnection = multiRepoConnection;
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
RepoConnection.prototype.create = function (params) {
    var scriptValue = __.toScriptValue(params);
    return __.toNativeObject(this.repoConnection.create(scriptValue));
};

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
RepoConnection.prototype.modify = function (params) {
    var editor = __.toScriptValue(params.editor);
    var key = required(params, 'key');
    return __.toNativeObject(this.repoConnection.modify(editor, key));
};

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
RepoConnection.prototype.get = function (keys) {
    let handlerParams = __.newBean('com.enonic.xp.lib.node.GetNodeHandlerParams');
    prepareGetParams(arguments, handlerParams);
    return __.toNativeObject(this.repoConnection.get(handlerParams));
};

/**
 * This function deletes a node or nodes.
 *
 * @example-ref examples/node/delete.js
 *
 * @param {...(string|string[])} keys Keys to delete. Each argument could be an id, a path or an array of the two.
 *
 * @returns {boolean} True if deleted, false otherwise.
 */
RepoConnection.prototype.delete = function (keys) {
    return __.toNativeObject(this.repoConnection.delete(argsToStringArray(arguments)));
};

/**
 * This function push a node to a given branch.
 *
 * @example-ref examples/node/push-1.js
 * @example-ref examples/node/push-2.js
 * @example-ref examples/node/push-3.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string[]} params.key Id or path to the nodes
 * @param {string[]} params.keys Array of ids or paths to the nodes
 * @param {string} params.target Branch to push nodes to.
 * @param {boolean} [params.includeChildren=false] Also push children of given nodes
 * @param {boolean} [params.resolve=true] Resolve dependencies before pushing, meaning that references will also be pushed
 * @param {string[]} [params.exclude] Array of ids or paths to nodes not to be pushed (nodes needed to maintain data integrity (e.g parents must be present in target) will be pushed anyway)
 *
 * @returns {object} PushNodesResult
 */
RepoConnection.prototype.push = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.PushNodeHandlerParams');
    params = params || {};
    if (params.key === undefined && params.keys === undefined) {
        throw 'Parameter \'key\' or \'keys\' is required';
    }
    handlerParams.key = params.key ? params.key : null;
    handlerParams.keys = params.keys ? params.keys : [];
    handlerParams.targetBranch = required(params, 'target');
    handlerParams.includeChildren = valueOrDefault(params.includeChildren, false);
    handlerParams.exclude = nullOrValue(params.exclude);
    handlerParams.resolve = valueOrDefault(params.resolve, true);

    return __.toNativeObject(this.repoConnection.push(handlerParams));
};

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
RepoConnection.prototype.diff = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.DiffBranchesHandlerParams');
    params = params || {};
    handlerParams.key = required(params, 'key');
    handlerParams.targetBranch = required(params, 'target');
    handlerParams.includeChildren = valueOrDefault(params.includeChildren, false);

    return __.toNativeObject(this.repoConnection.diff(handlerParams));
};

/**
 * This function returns a binary stream.
 *
 * @example-ref examples/node/getBinary.js
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the node.
 * @param {string} params.binaryReference to the binary.
 *
 * @returns {*} Stream of the binary.
 */
RepoConnection.prototype.getBinary = function (params) {
    var key = required(params, 'key');
    var binaryReference = params.binaryReference;
    return this.repoConnection.getBinary(key, binaryReference);
};

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
RepoConnection.prototype.move = function (params) {
    var source = required(params, 'source');
    var target = required(params, 'target');
    return __.toNativeObject(this.repoConnection.move(source, target));
};

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
RepoConnection.prototype.setChildOrder = function (params) {
    var key = required(params, 'key');
    var childOrder = required(params, 'childOrder');
    return __.toNativeObject(this.repoConnection.setChildOrder(key, childOrder));
};

/**
 * This command queries nodes.
 *
 * @example-ref examples/node/query.js
 *
 * @param {object} params JSON with the parameters.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {string} params.query Query expression.
 * @param {object} [params.filters] Query filters
 * @param {string} [params.sort='_score DESC'] Sorting expression.
 * @param {string} [params.aggregations] Aggregations expression.
 * @param {string} [params.highlight] Highlighting parameters.
 * @param {boolean} [params.explain=false] Return score calculation explanation.
 * @returns {object} Result of query.
 */
RepoConnection.prototype.query = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.QueryNodeHandlerParams');
    handlerParams.start = params.start;
    handlerParams.count = params.count;
    handlerParams.query = nullOrValue(params.query);
    handlerParams.sort = valueOrDefault(params.sort, '_score DESC');
    handlerParams.aggregations = __.toScriptValue(params.aggregations);
    handlerParams.suggestions = __.toScriptValue(params.suggestions);
    handlerParams.highlight = __.toScriptValue(params.highlight);
    handlerParams.filters = __.toScriptValue(params.filters);
    handlerParams.explain = valueOrDefault(params.explain, false);
    return __.toNativeObject(this.repoConnection.query(handlerParams));
};


/**
 * This command queries nodes in a multi-repo connection.
 *
 * @example-ref examples/node/multiRepoQuery.js
 *
 * @param {object} params JSON with the parameters.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {string} params.query Query expression.
 * @param {object} [params.filters] Query filters
 * @param {string} [params.sort='_score DESC'] Sorting expression.
 * @param {string} [params.aggregations] Aggregations expression.
 * @param {boolean} [params.explain=false] Return score calculation explanation.
 * @returns {object} Result of query.
 */
MultiRepoConnection.prototype.query = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.QueryNodeHandlerParams');
    handlerParams.start = params.start;
    handlerParams.count = params.count;
    handlerParams.query = nullOrValue(params.query);
    handlerParams.sort = valueOrDefault(params.sort, '_score DESC');
    handlerParams.aggregations = __.toScriptValue(params.aggregations);
    handlerParams.suggestions = __.toScriptValue(params.suggestions);
    handlerParams.highlight = __.toScriptValue(params.highlight);
    handlerParams.filters = __.toScriptValue(params.filters);
    handlerParams.explain = valueOrDefault(params.explain, false);
    return __.toNativeObject(this.multiRepoConnection.query(handlerParams));
};


/**
 * Check if node exists.
 *
 * @example-ref examples/node/exists.js
 *
 * @param {string} [key] node path or id.
 *
 * @returns {boolean} True if exist, false otherwise.
 */
RepoConnection.prototype.exists = function (key) {
    return __.toNativeObject(this.repoConnection.exist(key));
};


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
RepoConnection.prototype.findVersions = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.FindVersionsHandlerParams');
    handlerParams.key = required(params, 'key');
    handlerParams.start = nullOrValue(params.start);
    handlerParams.count = nullOrValue(params.count);
    return __.toNativeObject(this.repoConnection.findVersions(handlerParams));
};

/**
 * This function returns the active version of a node.
 *
 * @example-ref examples/node/getActiveVersion.js
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or ID of the node.
 *
 * @returns {object} Active content versions per branch.
 */
RepoConnection.prototype.getActiveVersion = function (params) {
    var key = required(params, 'key');
    return __.toNativeObject(this.repoConnection.getActiveVersion(key));
};

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
RepoConnection.prototype.setActiveVersion = function (params) {
    var key = required(params, 'key');
    var versionId = required(params, 'versionId');
    return __.toNativeObject(this.repoConnection.setActiveVersion(key, versionId));
};

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
RepoConnection.prototype.findChildren = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.FindChildrenHandlerParams');
    handlerParams.parentKey = params.parentKey;
    handlerParams.start = valueOrDefault(params.start, 0);
    handlerParams.count = valueOrDefault(params.count, 10);
    handlerParams.childOrder = nullOrValue(params.childOrder);
    handlerParams.countOnly = valueOrDefault(params.countOnly, false);
    handlerParams.recursive = valueOrDefault(params.recursive, false);
    return __.toNativeObject(this.repoConnection.findChildren(handlerParams));
};

/**
 * Refresh the index for the current repoConnection
 *
 * @example-ref examples/node/refresh.js
 *
 * @param {string} [mode]=ALL Refresh all (ALL) data, or just the search-index (SEARCH) or the storage-index (STORAGE)
 */
RepoConnection.prototype.refresh = function (mode) {
    this.repoConnection.refresh(valueOrDefault(mode, 'ALL'));
};


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
RepoConnection.prototype.setRootPermissions = function (params) {
    required(params, '_permissions');
    return __.toNativeObject(this.repoConnection.setRootPermissions(__.toScriptValue(params)));
};


/**
 * This function commits the active version of nodes.
 *
 * @example-ref examples/node/commit.js
 *
 * @param {...(string|string[])} keys Node keys to commit. Each argument could be an id, a path or an array of the two. Prefer the usage of ID rather than paths.
 * @param {string} [message] Commit message.
 *
 * @returns {object} Commit object.
 */
RepoConnection.prototype.commit = function (params) {
    var keys = argsToStringArray(params.keys);
    var message = nullOrValue(params.message);
    return __.toNativeObject(this.repoConnection.commit(keys, message));
};


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
exports.connect = function (params) {

    var nodeHandleContext = __.newBean('com.enonic.xp.lib.node.NodeHandleContext');
    nodeHandleContext.repoId = required(params, 'repoId');
    nodeHandleContext.branch = required(params, 'branch');

    if (params.user) {
        if (params.user.login) {
            nodeHandleContext.username = params.user.login;
        }
        if (params.user.idProvider) {
            nodeHandleContext.idProvider = params.user.idProvider;
        }
    }

    if (params.principals) {
        nodeHandleContext.principals = params.principals;
    }

    return new RepoConnection(factory.create(nodeHandleContext));
};

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
exports.multiRepoConnect = function (params) {
    var multiRepoNodeHandleContext = __.newBean('com.enonic.xp.lib.node.MultiRepoNodeHandleContext');

    params.sources.forEach(function (source) {
        multiRepoNodeHandleContext.addSource(required(source, 'repoId'), required(source, 'branch'), required(source, 'principals'));
    });

    return new MultiRepoConnection(multiRepoConnectfactory.create(multiRepoNodeHandleContext));
};
