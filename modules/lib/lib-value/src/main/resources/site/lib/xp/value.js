var GeoPointType = Java.type("com.enonic.xp.util.GeoPoint");
var InstantType = Java.type("java.time.Instant");
var LocalDateType = Java.type("java.time.LocalDate");
var LocalDateTimeType = Java.type("java.time.LocalDateTime");
var LocalTimeType = Java.type("java.time.LocalTime");
var ReferenceType = Java.type("com.enonic.xp.util.Reference");
var BinaryReferenceType = Java.type("com.enonic.xp.util.BinaryReference");
var BinaryAttachmentType = Java.type("com.enonic.xp.node.BinaryAttachment");

exports.geoPoint = function (lat, lon) {
    return new GeoPointType(lat, lon);
};

exports.geoPointString = function (value) {
    return GeoPointType.from(value);
};

exports.instant = function (value) {
    return InstantType.parse(value);
};

exports.reference = function (value) {
    return ReferenceType.from(value);
};

exports.localDateTime = function (value) {
    return LocalDateTimeType.parse(value);
};

exports.localDate = function (value) {
    return LocalDateType.parse(value);
};

exports.localTime = function (value) {
    return LocalTimeType.parse(value);
};

exports.binary = function (name, source) {
    return new BinaryAttachmentType(BinaryReferenceType.from(name), source);
};