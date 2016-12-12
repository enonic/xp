/**
 * Functions to pass java-types in JSON, typically usage is to type e.g a Geo-point value when creating nodes in node-lib.
 *
 * @example
 * var valueLib = require('/lib/xp/value');
 *
 * @module lib/xp/value
 */


var GeoPointType = Java.type("com.enonic.xp.util.GeoPoint");
var InstantType = Java.type("java.time.Instant");
var LocalDateType = Java.type("java.time.LocalDate");
var LocalDateTimeType = Java.type("java.time.LocalDateTime");
var LocalTimeType = Java.type("java.time.LocalTime");
var ReferenceType = Java.type("com.enonic.xp.util.Reference");
var BinaryReferenceType = Java.type("com.enonic.xp.util.BinaryReference");
var BinaryAttachmentType = Java.type("com.enonic.xp.node.BinaryAttachment");

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
 * @param {string} value An ISO-8601-formatted instant (e.g '2011-12-03T10:15:30Z')
 *
 * @returns {*} Instant java-type
 */
exports.instant = function (value) {
    return InstantType.parse(value);
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
 * @param {string} value A local date-time string (e.g '2007-12-03T10:15:30')
 *
 * @returns {*} LocalDateTime java-type
 */
exports.localDateTime = function (value) {
    return LocalDateTimeType.parse(value);
};

/**
 * Creates a LocalDate java-type.
 * @param {string} value A ISO local date-time string (e.g '2011-12-03')
 *
 * @returns {*} LocalDate java-type
 */
exports.localDate = function (value) {
    return LocalDateType.parse(value);
};

/**
 * Creates a LocalTime java-type.
 * @param {string} value A ISO local date-time string (e.g '10:15:30')
 *
 * @returns {*} LocalTime java-type
 */
exports.localTime = function (value) {
    return LocalTimeType.parse(value);
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