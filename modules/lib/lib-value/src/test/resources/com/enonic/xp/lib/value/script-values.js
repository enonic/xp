var valueLib = require('/lib/xp/value');

var TestClass = Java.type('com.enonic.xp.lib.value.ScriptValueTranslatorTest');
var stream1 = TestClass.createByteSource('Hello World');

exports.geoPoint = function () {
    return {
        myGeoPoint: valueLib.geoPoint(80, -80)
    };
};

exports.instant = function () {
    return {
        myInstant: valueLib.instant('2016-08-01T11:22:00Z')
    };
};

exports.instantFromDate = function () {
    return {
        myInstantExpected: valueLib.instant('2016-08-01T11:22:00Z'),
        myInstant: valueLib.instant(new Date('2016-08-01T11:22:00Z'))
    };
};

exports.boolean = function () {
    return {
        myBoolean: false
    };
};

exports.reference = function () {
    return {
        myReference: valueLib.reference('1234')
    };
};

exports.localDateTime = function () {
    return {
        myLocalDateTime: valueLib.localDateTime('2010-10-10T10:00:00')
    };
};

exports.localDateTimeFromDate = function () {
    return {
        myLocalDateTimeExpected: valueLib.localDateTime('2010-10-10T10:00:00'),
        myLocalDateTime: valueLib.localDateTime(new Date(2010, 9, 10, 10, 0, 0))
    };
};

exports.localDate = function () {
    return {
        myLocalDate: valueLib.localDate('2010-10-10')
    };
};

exports.localDateFromDate = function () {
    return {
        myLocalDateExpected: valueLib.localDate('2010-10-10'),
        myLocalDate: valueLib.localDate(new Date('Sun Oct 10 2010 10:00:00 GMT+0200 (CEST)'))
    };
};

exports.localTime = function () {
    return {
        myLocalTime: valueLib.localTime('10:00:30')
    };
};

exports.localTimeFromDate = function () {
    return {
        myLocalTimeExpected: valueLib.localTime('10:00:30'),
        myLocalTime: valueLib.localTime(new Date(2010, 9, 10, 10, 0, 30))
    };
};

exports.binary = function () {
    return {
        myBinary: valueLib.binary('myFile', stream1)
    };
};

exports.date = function () {
    return {
        myDate: new Date(1995, 11, 17, 3, 24, 0)
    };
};

exports.integer = function () {
    var Integer = Java.type('java.lang.Integer');
    return {
        myInteger: new Integer(42)
    };
};

exports.byte = function () {
    var Byte = Java.type('java.lang.Byte');
    return {
        myByte: new Byte(42)
    };
};

exports.long = function () {
    var Long = Java.type('java.lang.Long');
    return {
        myLong: new Long(42)
    };
};

exports.double = function () {
    var Double = Java.type('java.lang.Double');
    return {
        myDouble: new Double(42)
    };
};

exports.float = function () {
    var Float = Java.type('java.lang.Float');
    return {
        myFloat: new Float(42)
    };
};

exports.number = function () {
    var BigDecimal = Java.type('java.math.BigDecimal');
    return {
        myNumber: BigDecimal.TEN
    };
};

exports.defaultValue = function () {
    var DayOfWeek = Java.type('java.time.DayOfWeek');
    return {
        myDefaultType: DayOfWeek.SUNDAY
    };
};

exports.array = function () {
    return {
        myArray: [1, 2, 3]
    };
};

exports.map = function () {
    return {
        myMap: {
            a: {
                b: 42
            }
        }
    };
};

exports.nullValues = function () {
    return {
        nullProp: null,
        nonNull: 'hello',
        alsoNull: null
    };
};

exports.undefinedValues = function () {
    return {
        undefProp: undefined,
        nonNull: 'hello',
        alsoUndef: undefined
    };
};

exports.deletedValues = function () {
    var obj = {
        deletedProp: 'will be deleted',
        nonNull: 'hello',
        alsoDeleted: 'will be deleted too'
    };
    delete obj.deletedProp;
    delete obj.alsoDeleted;
    return obj;
};
