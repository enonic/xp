/**
 * Functions to find and manipulate content.
 *
 * @example
 * var contentLib = require('/lib/xp/content-graphql');
 *
 * @module content
 */

/* global __ */

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
exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetContentHandler');
    bean.key = required(params, 'key');
    bean.versionId = nullOrValue(params.versionId);
    return __.toNativeObjectIncludeNullValues(bean.execute());
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
    return __.toNativeObjectIncludeNullValues(bean.execute());
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
 * This function returns the parent site of a content.
 *
 * @example-ref examples/content/getSite.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the content.
 *
 * @returns {object} The current site as JSON.
 */
exports.getSite = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetSiteHandler');
    bean.key = nullOrValue(params.key);
    return __.toNativeObjectIncludeNullValues(bean.execute());
};

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
exports.getSiteConfig = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetSiteConfigHandler');
    bean.key = nullOrValue(params.key);
    bean.applicationKey = nullOrValue(params.applicationKey);
    return __.toNativeObjectIncludeNullValues(bean.execute());
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
 *
 * @returns {Object} Result (of content) fetched from the repository.
 */
exports.getChildren = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetChildContentHandler');
    bean.key = required(params, 'key');
    bean.start = params.start;
    bean.count = params.count;
    bean.sort = nullOrValue(params.sort);
    return __.toNativeObjectIncludeNullValues(bean.execute());
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
 * @param {object} [params.filters] Filters to apply to query result
 * @param {string} [params.sort] Sorting expression.
 * @param {string} [params.aggregations] Aggregations expression.
 * @param {string[]} [params.contentTypes] Content types to filter on.
 *
 * @returns {Object} Result of query.
 */
exports.query = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.QueryContentHandler');
    bean.start = params.start;
    bean.count = params.count;
    bean.query = nullOrValue(params.query);
    bean.sort = nullOrValue(params.sort);
    bean.aggregations = __.toScriptValue(params.aggregations);
    bean.contentTypes = __.toScriptValue(params.contentTypes);
    bean.filters = __.toScriptValue(params.filters);
    bean.highlight = __.toScriptValue(params.highlight);
    return __.toNativeObjectIncludeNullValues(bean.execute());
};

/**
 * Check if content exists.
 *
 * @example-ref examples/content/exists.js
 *
 * @param {string} [params.key] content id.
 *
 * @returns {boolean} True if exist, false otherwise.
 */
exports.exists = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.ContentExistsHandler');

    bean.key = required(params, 'key');

    return __.toNativeObjectIncludeNullValues(bean.execute());
};

/**
 * Gets permissions on a content.
 *
 * @example-ref examples/content/getPermissions.js
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or id of the content.
 * @returns {object} Content permissions.
 */
exports.getPermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetPermissionsHandler');

    if (params.key) {
        bean.key = params.key;
    }
    return __.toNativeObjectIncludeNullValues(bean.execute());
};

/**
 * Returns the properties and icon of the specified content type.
 *
 * @example-ref examples/content/getType.js
 *
 * @param name Name of the content type, as 'app:name' (e.g. 'com.enonic.myapp:article').
 * @returns {ContentType} The content type object if found, or null otherwise. See ContentType type definition below.
 */
exports.getType = function (name) {
    var bean = __.newBean('com.enonic.xp.lib.content.ContentTypeHandler');
    bean.name = nullOrValue(name);
    return __.toNativeObjectIncludeNullValues(bean.getContentType());
};

/**
 * Returns the list of all the content types currently registered in the system.
 *
 * @example-ref examples/content/getTypes.js
 *
 * @returns {ContentType[]} Array with all the content types found. See ContentType type definition below.
 */
exports.getTypes = function () {
    var bean = __.newBean('com.enonic.xp.lib.content.ContentTypeHandler');
    return __.toNativeObjectIncludeNullValues(bean.getAllContentTypes());
};

/**
 * Returns outbound dependencies on a content.
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or id of the content.
 * @returns {object} Content Ids.
 */
exports.getOutboundDependencies = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetOutboundDependenciesHandler');

    bean.key = required(params, 'key');

    return __.toNativeObjectIncludeNullValues(bean.execute());
};
