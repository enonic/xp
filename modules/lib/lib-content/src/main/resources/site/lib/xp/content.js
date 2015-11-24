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
 * @example
 * var result = contentLib.get({
 *   key: '/features/js-libraries/mycontent',
 *   branch: 'draft'
 * });
 *
 * if (result) {
 *   log.info('Display Name = %s', result.displayName);
 * } else {
 *   log.info('Content was not found');
 * }
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
 * This function deletes a content.
 *
 * @example
 * var result = contentLib.delete({
 *   key: '/features/js-libraries/mycontent',
 *   branch: 'draft'
 * });
 *
 * if (result) {
 *   log.info('Content deleted');
 * } else {
 *   log.info('Content was not found');
 * }
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
 * @example
 * var result = contentLib.getChildren({
 *   key: '/features/js-libraries/houses',
 *   start: 0,
 *   count: 2,
 *   sort: '_modifiedTime ASC',
 *   branch: 'draft'
 * });
 *
 * log.info('Found ' + result.total + ' number of contents');
 *
 * for (var i = 0; i < result.hits.length; i++) {
 *   var content = result.hits[i];
 *   log.info('Content ' + content._name + ' loaded');
 * }
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
 * @example
 * var result = contentLib.create({
 *   name: 'mycontent',
 *   parentPath: '/features/js-libraries',
 *   displayName: 'My Content',
 *   requireValid: true,
 *   contentType: app.name + ':all-input-types',
 *   branch: 'draft',
 *   language: 'no',
 *   data: {
 *       myCheckbox: true,
 *       myComboBox: 'option1',
 *       myDate: '1970-01-01',
 *       myDateTime: '1970-01-01T10:00',
 *       myDouble: 3.14,
 *       myGeoPoint: '59.91,10.75',
 *       myHtmlArea: '<p>htmlAreaContent</p>',
 *       myImageSelector: '5a5fc786-a4e6-4a4d-a21a-19ac6fd4784b',
 *       myLong: 123,
 *       myRelationship: 'features',
 *       myRadioButtons: 'option1',
 *       myTag: 'aTag',
 *       myTextArea: 'textAreaContent',
 *       myTextLine: 'textLineContent',
 *       myTime: '10:00',
 *       myTextAreas: [
 *           'textAreaContent1',
 *           'textAreaContent2'
 *       ],
 *       myItemSet: {
 *           'textLine': 'textLineContent',
 *           'long': 123
 *       }
 *   },
 *   x: {
 *       "com-enonic-app-features": {
 *           "menu-item": {
 *               "menuItem": true
 *           }
 *       }
 *   }
 * });
 *
 * log.info('Content created with id ' + result._id);
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params.name] Name of content.
 * @param {string} [params.parentPath=/] Path to place content under.
 * @param {string} [params.displayName] Display name. Default is same as `name`.
 * @param {boolean} [params.requireValid=true] The content has to be valid to be created.
 * @param {string} params.contentType Content type to use.
 * @param {string} [params.language] The language tag representing the content’s locale.
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
 * @example
 * var result = contentLib.query({
 *   start: 0,
 *   count: 2,
 *   sort: "modifiedTime DESC, geoDistance('data.location', '59.91,10.75')",
 *   query: "data.city = 'Oslo' AND fulltext('data.description', 'garden', 'AND') ",
 *   branch: "draft",
 *   contentTypes: [
 *       app.name + ":house",
 *       app.name + ":apartment"
 *   ],
 *   aggregations: {
 *       floors: {
 *           terms: {
 *               field: "data.number_floor",
 *               order: "_count asc"
 *           },
 *           aggregations: {
 *               prices: {
 *                   histogram: {
 *                       field: "data.price",
 *                       interval: 1000000,
 *                       extendedBoundMin: 1000000,
 *                       extendedBoundMax: 3000000,
 *                       minDocCount: 0,
 *                       order: "_key desc"
 *                   }
 *               }
 *           }
 *       },
 *       by_month: {
 *           dateHistogram: {
 *               field: "data.publish_date",
 *               interval: "1M",
 *               minDocCount: 0,
 *               format: "MM-yyyy"
 *           }
 *       },
 *       price_ranges: {
 *           range: {
 *               field: "data.price",
 *               ranges: [
 *                   {to: 2000000},
 *                   {from: 2000000, to: 3000000},
 *                   {from: 3000000}
 *               ]
 *           }
 *       },
 *       my_date_range: {
 *           dateRange: {
 *               field: "data.publish_date",
 *               format: "MM-yyyy",
 *               ranges: [
 *                   {to: "now-10M/M"},
 *                   {from: "now-10M/M"}
 *               ]
 *           }
 *       },
 *       price_stats: {
 *           stats: {
 *               field: "data.price"
 *           }
 *       }
 *   }
 * });
 *
 * log.info('Found ' + result.total + ' number of contents');
 *
 * for (var i = 0; i < result.hits.length; i++) {
 *   var content = result.hits[i];
 *   log.info('Content ' + content._name + ' found');
 * }
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
 * @example
 * function editor(c) {
 *   c.displayName = 'Modified';
 *   c.language = 'en';
 *   c.data.myCheckbox = false;
 *   c.data["myTime"] = "11:00";
 *   return c;
 * }
 *
 * var result = contentLib.modify({
 *   key: '/features/js-libraries/mycontent',
 *   editor: editor
 * });
 *
 * if (result) {
 *   log.info('Content modified. New title is ' + result.displayName);
 * } else {
 *   log.info('Content not found');
 * }
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
 * Creates a media content.
 *
 * @example
 * var result = contentLib.createMedia({
 *   name: 'myphoto',
 *   parentPath: '/path/to/media',
 *   mimeType: 'image/jpg',
 *   data: mystream
 * });
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params.name] Name of content.
 * @param {string} [params.parentPath=/] Path to place content under.
 * @param {string} [params.mimeType] Mime-type of the data.
 * @param {number} [params.focalX] Focal point for X axis (if it's an image).
 * @param {number} [params.focalY] Focal point for Y axis (if it's an image).
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
    bean.data = nullOrValue(params.data);
    bean.idGenerator = nullOrValue(params.idGenerator);
    return __.toNativeObject(bean.execute());
};

/**
 * Sets permissions on a content.
 *
 * @example
 * var updatedContent = securityLib.setPermissions({
 *   key: '03c6ae7b-7f48-45f5-973d-1f03606ab928',
 *   permissions: [{
 *     principal: 'user:system:anonymous',
 *     allow: ['READ'],
 *     deny: []
 *   }]
 * });
 *
 * @param {object} params JSON parameters.
 * @param {string} params.key Path or ID of the content.
 * @param {array} params.permissions Array of permissions.
 * @param {string} params.permissions.principal Principal key.
 * @param {array} params.permissions.allow Allowed permissions.
 * @param {array} params.permissions.deny Denied permissions.
 * @returns {object} Updated content.
 */
exports.setPermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.SetPermissionsHandler');

    if (params.key) {
        bean.setKey(params.key);
    }
    if (params.permissions) {
        bean.permissions = __.toScriptValue(params.permissions);
    }
    return __.toNativeObject(bean.execute());
};
