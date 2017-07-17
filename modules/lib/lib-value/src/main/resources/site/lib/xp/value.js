/**
 * Functions to pass java-types in JSON, typically usage is to type e.g a Geo-point value when creating nodes in node-lib.
 *
 * @example
 * var valueLib = require('/lib/xp/value');
 *
 * @module value
 */


var GeoPointType = Java.type("com.enonic.xp.util.GeoPoint");
var InstantType = Java.type("java.time.Instant");
var LocalDateType = Java.type("java.time.LocalDate");
var LocalDateTimeType = Java.type("java.time.LocalDateTime");
var LocalTimeType = Java.type("java.time.LocalTime");
var ReferenceType = Java.type("com.enonic.xp.util.Reference");
var BinaryReferenceType = Java.type("com.enonic.xp.util.BinaryReference");
var BinaryAttachmentType = Java.type("com.enonic.xp.node.BinaryAttachment");

var pad = function (number) {
    if (number < 10) {
        return '0' + number;
    }
    return number;
};

var toLocalDateString = function (date) {
    return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate());
};

var toLocalTimeString = function (date) {
    return date.toTimeString().substring(0, 8);
};

var toLocalDateTimeString = function (date) {
    return toLocalDateString(date) + 'T' + toLocalTimeString(date);
};

/**
 * Creates a GeoPoint java-type.
 * @param {number} lat Latitude
 * @param {number} lon Longitude
 *
 * @returns {*} GeoPoint java-type
 */
exports.geoPoint = function (lat, lon) {
    return new GeoPointType(lat, lon);
};


/**
 * Creates a GeoPoint java-type.
 * @param {string} value comma-separated lat and lon
 *
 * @returns {*} GeoPoint java-type
 */
exports.geoPointString = function (value) {
    return GeoPointType.from(value);
};

/**
 * Creates a Instant java-type.
 * @param {string|Date} value An ISO-8601-formatted instant (e.g '2011-12-03T10:15:30Z'), or a Date object.
 *
 * @returns {*} Instant java-type
 */
exports.instant = function (value) {
    if (typeof value === 'string') {
        return InstantType.parse(value);
    } else if (value.toISOString) {
        return InstantType.parse(value.toISOString());
    }
    return InstantType.parse(value.toString());
};

/**
 * Creates a Reference java-type.
 * @param {string} value A nodeId as string (e.g '1234-5678-91011')
 *
 * @returns {*} Reference java-type
 */
exports.reference = function (value) {
    return ReferenceType.from(value);
};

/**
 * Creates a LocalDateTime java-type.
 * @param {string|Date} value A local date-time string (e.g '2007-12-03T10:15:30'), or a Date object.
 *
 * @returns {*} LocalDateTime java-type
 */
exports.localDateTime = function (value) {
    if (typeof value === 'string') {
        return LocalDateTimeType.parse(value);
    } else if (value.toISOString) {
        return LocalDateTimeType.parse(toLocalDateTimeString(value));
    }
    return LocalDateTimeType.parse(value.toString());
};

/**
 * Creates a LocalDate java-type.
 * @param {string|Date} value A ISO local date-time string (e.g '2011-12-03'), or a Date object.
 *
 * @returns {*} LocalDate java-type
 */
exports.localDate = function (value) {
    if (typeof value === 'string') {
        return LocalDateType.parse(value);
    } else if (value.toISOString) {
        return LocalDateType.parse(toLocalDateString(value));
    }
    return LocalDateType.parse(value.toString());
};

/**
 * Creates a LocalTime java-type.
 * @param {string|Date} value A ISO local date-time string (e.g '10:15:30'), or a Date object.
 *
 * @returns {*} LocalTime java-type
 */
exports.localTime = function (value) {
    if (typeof value === 'string') {
        return LocalTimeType.parse(value);
    } else if (value.toISOString) {
        return LocalTimeType.parse(toLocalTimeString(value));
    }
    return LocalTimeType.parse(value.toString());
};

/**
 * Creates a BinaryAttachment java-type.
 * @param {string} name The binary name
 * @param stream The binary stream
 *
 * @returns {*} BinaryAttachment java-type
 */
exports.binary = function (name, stream) {
    return new BinaryAttachmentType(BinaryReferenceType.from(name), stream);
};