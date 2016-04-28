/**
 * Functions to find and manipulate content.
 *
 * @example
 * var contentLib = require('/lib/xp/content');
 *
 * @module lib/xp/content
 */

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw "Parameter '" + name + "' is required";
    }

    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

/**
 * This function fetches a content.
 *
 * @example-ref examples/content/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 *
 * @returns {object} The content (as JSON) fetched from the repository.
 */
exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetContentHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    return __.toNativeObject(bean.execute());
};

/**
 * This function returns a content attachments.
 *
 * @example-ref examples/content/getAttachments.js
 *
 * @param {string} key Path or id to the content.
 *
 * @returns {object} An object with all the attachments that belong to the content, where the key is the attachment name. Or null if the content cannot be found.
 */
exports.getAttachments = function (key) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetAttachmentsHandler');
    bean.key = nullOrValue(key);
    return __.toNativeObject(bean.execute());
};

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
exports.getAttachmentStream = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetAttachmentStreamHandler');
    bean.key = required(params, 'key');
    bean.name = required(params, 'name');
    return bean.getStream();
};

/**
 * This function deletes a content.
 *
 * @example-ref examples/content/delete.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 *
 * @returns {boolean} True if deleted, false otherwise.
 */
exports.delete = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.DeleteContentHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    return bean.execute();
};

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
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 *
 * @returns {boolean} Result (of content) fetched from the repository.
 */
exports.getChildren = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetChildContentHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    bean.start = params.start;
    bean.count = params.count;
    bean.sort = nullOrValue(params.sort);
    return __.toNativeObject(bean.execute());
};

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
 * @param {string} [params.parentPath=/] Path to place content under.
 * @param {string} [params.displayName] Display name. Default is same as `name`.
 * @param {boolean} [params.requireValid=true] The content has to be valid to be created.
 * @param {string} params.contentType Content type to use.
 * @param {string} [params.language] The language tag representing the content’s locale.
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 * @param {object} params.data Actual content data.
 * @param {object} [params.x] eXtra data to use.
 *
 * @returns {object} Content created as JSON.
 */
exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.CreateContentHandler');
    bean.name = nullOrValue(params.name);
    bean.parentPath = nullOrValue(params.parentPath);
    bean.displayName = nullOrValue(params.displayName);
    bean.contentType = nullOrValue(params.contentType);
    bean.requireValid = params.requireValid;
    bean.language = nullOrValue(params.language);
    bean.branch = nullOrValue(params.branch);

    bean.data = __.toScriptValue(params.data);
    bean.x = __.toScriptValue(params.x);

    bean.idGenerator = nullOrValue(params.idGenerator);

    return __.toNativeObject(bean.execute());
};

/**
 * This command queries content.
 *
 * @example-ref examples/content/query.js
 *
 * @param {object} params JSON with the parameters.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {string} params.query Query expression.
 * @param {string} [params.sort] Sorting expression.
 * @param {string} [params.aggregations] Aggregations expression.
 * @param {string[]} [params.contentTypes] Content types to filter on.
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 *
 * @returns {boolean} Result of query.
 */
exports.query = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.QueryContentHandler');
    bean.branch = nullOrValue(params.branch);
    bean.start = params.start;
    bean.count = params.count;
    bean.query = nullOrValue(params.query);
    bean.sort = nullOrValue(params.sort);
    bean.aggregations = __.toScriptValue(params.aggregations);
    bean.contentTypes = __.toScriptValue(params.contentTypes);
    return __.toNativeObject(bean.execute());
};

/**
 * This function modifies a content.
 *
 * @example-ref examples/content/modify.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 * @param {function} params.editor Editor callback function.
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 *
 * @returns {object} Modified content as JSON.
 */
exports.modify = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.ModifyContentHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    bean.editor = __.toScriptValue(params.editor);
    return __.toNativeObject(bean.execute());
};

/**
 * This function publishes content to a branch.
 *
 * @example-ref examples/content/publish.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string[]} params.keys List of all content keys(path or id) that should be published.
 * @param {string} params.sourceBranch The branch where the content to be published is stored.
 * @param {string} params.targetBranch The branch to which the content should be published.  Technically, publishing is just a move from one branch
 * to another, and publishing user content from master to draft is therefore also valid usage of this function, which may be practical if user input to a web-page is stored on master.
 * @param {boolean} [params.includeChildren=true] Whether all children should be included when publishing content.
 * @param {boolean} [params.includeDependencies=true] Whether all related content should be included when publishing content.
 *
 * @returns {object} Modified content as JSON.
 */
exports.publish = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.PublishContentHandler');
    bean.keys = required(params, 'keys');
    bean.targetBranch = required(params, 'targetBranch');
    bean.sourceBranch = required(params, 'sourceBranch');
    if (!nullOrValue(params.includeChildren)) {
        bean.includeChildren = params.includeChildren;
    }
    if (!nullOrValue(params.includeDependencies)) {
        bean.includeDependencies = params.includeDependencies;
    }
    return __.toNativeObject(bean.execute());
};

/**
 * Creates a media content.
 *
 * @example-ref examples/content/createMedia.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params.name] Name of content.
 * @param {string} [params.parentPath=/] Path to place content under.
 * @param {string} [params.mimeType] Mime-type of the data.
 * @param {number} [params.focalX] Focal point for X axis (if it's an image).
 * @param {number} [params.focalY] Focal point for Y axis (if it's an image).
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 * @param  params.data Data (as stream) to use.
 *
 * @returns {object} Returns the created media content.
 */
exports.createMedia = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.CreateMediaHandler');
    bean.name = required(params, 'name');
    bean.parentPath = nullOrValue(params.parentPath);
    bean.mimeType = nullOrValue(params.mimeType);
    bean.focalX = nullOrValue(params.focalX);
    bean.focalY = nullOrValue(params.focalY);
    bean.branch = nullOrValue(params.branch);
    bean.data = nullOrValue(params.data);
    bean.idGenerator = nullOrValue(params.idGenerator);
    return __.toNativeObject(bean.execute());
};

/**
 * Sets permissions on a content.
 *
 * @example-ref examples/content/setPermissions.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or ID of the content.
 * @param {boolean} [params.inheritPermissions] Set to true if the content must inherit permissions. Default to false.
 * @param {boolean} [params.overwriteChildPermissions] Set to true to overwrite child permissions. Default to false.
 * @param {array} [params.permissions] Array of permissions.
 * @param {string} params.permissions.principal Principal key.
 * @param {array} params.permissions.allow Allowed permissions.
 * @param {array} params.permissions.deny Denied permissions.
 * @returns {boolean} True if successful, false otherwise.
 */
exports.setPermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.SetPermissionsHandler');

    if (params.key) {
        bean.key = params.key;
    }
    if (params.inheritPermissions) {
        bean.inheritPermissions = params.inheritPermissions;
    }
    if (params.overwriteChildPermissions) {
        bean.overwriteChildPermissions = params.overwriteChildPermissions;
    }
    if (params.permissions) {
        bean.permissions = __.toScriptValue(params.permissions);
    }
    return __.toNativeObject(bean.execute());
};

/**
 * Gets permissions on a content.
 *
 * @example-ref examples/content/getPermissions.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or ID of the content.
 * @returns {object} Content permissions.
 */
exports.getPermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetPermissionsHandler');

    if (params.key) {
        bean.key = params.key;
    }
    return __.toNativeObject(bean.execute());
};
