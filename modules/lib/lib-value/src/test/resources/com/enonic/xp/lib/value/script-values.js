var valueLib = require('/lib/xp/value');

var TestClass = Java.type('com.enonic.xp.lib.value.ScriptValueTranslatorTest');
var stream1 = TestClass.createByteSource('Hello World');

exports.geoPoint = function () {
    return {
        myGeoPoint: valueLib.geoPoint(80, -80),
    }
};

exports.instant = function () {
    return {
        myInstant: valueLib.instant("2016-08-01T11:22:00Z")
    }
};

exports.boolean = function () {
    return {
        myBoolean: false
    }
};

exports.reference = function () {
    return {
        myReference: valueLib.reference("1234")
    }
};

exports.localDateTime = function () {
    return {
        myLocalDateTime: valueLib.localDateTime("2010-10-10T10:00:00")
    }
};

exports.localDate = function () {
    return {
        myLocalDate: valueLib.localDate("2010-10-10")
    }
};

exports.localTime = function () {
    return {
        myLocalTime: valueLib.localTime("10:00:30")
    }
};

exports.binary = function () {
    return {
        myBinary: valueLib.binary("myFile", stream1)
    }
};
