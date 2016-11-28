var nodeLib = require('/lib/xp/node');

var TestClass = Java.type('com.enonic.xp.lib.node.CreateNodeHandlerTest');
var stream1 = TestClass.createByteSource('Hello World');

exports.object = function () {
    return {a: 1, b: 2};
};

exports.array = function () {
    return {myArray: ["one", "two", "three"]};
};

exports.geoPoint = function () {
    return {
        myGeoPoint: nodeLib.geoPoint(80, -80),
    }
};

exports.instant = function () {
    return {
        myInstant: nodeLib.instant("2016-08-01T11:22:00Z")
    }
};

exports.boolean = function () {
    return {
        myBoolean: false
    }
};

exports.double = function () {
    return {
        myDouble: 21.0
    }
};


exports.reference = function () {
    return {
        myReference: nodeLib.reference("1234")
    }
};

exports.localDateTime = function () {
    return {
        myLocalDateTime: nodeLib.localDateTime("2010-10-10T10:00:00")
    }
};

exports.localDate = function () {
    return {
        myLocalDate: nodeLib.localDate("2010-10-10")
    }
};

exports.localTime = function () {
    return {
        myLocalTime: nodeLib.localTime("10:00:30")
    }
};

exports.binary = function () {
    return {
        myBinary: nodeLib.binary("myFile", stream1)
    }
};

exports.indexConfig = function () {
    return {
        _indexConfig: {
            default: "minimal",
            configs: {
                path: "displayName",
                config: "fulltext"
            }
        }
    }
};

exports.permissions = function () {
    return {
        _permissions: [
            {
                "principal": "user:system:anonymous",
                "allow": [
                    "READ",
                    "CREATE"
                ],
                "deny": [
                    "MODIFY",
                    "DELETE"
                ]
            },
            {
                "principal": "role:admin",
                "allow": [
                    "READ",
                    "CREATE",
                    "MODIFY",
                    "DELETE",
                    "PUBLISH",
                    "READ_PERMISSIONS",
                    "WRITE_PERMISSIONS"
                ],
                "deny": [
                    "PUBLISH"
                ]
            }
        ]
    }
};

exports.full = function () {
    return {
        "_name": "myName",
        "displayName": "This is brand new node",
        "someData": {
            "cars": ["skoda", "tesla model X"],
            "likes": "plywood",
            "numberOfUselessGadgets": 123,
            "myGeoPoint": "80.0,-80.0",
            "myInstant": "2016-08-01T11:22:00Z"
        },
        "_indexConfig": {
            "default": "minimal",
            "configs": {
                "path": "displayName",
                "config": "fulltext"
            }
        },
        "_permissions": [{
            "principal": "user:system:anonymous",
            "allow": "READ"
        }, {
            "principal": "role:admin",
            "allow": ["READ", "CREATE", "MODIFY", "DELETE", "PUBLISH", "READ_PERMISSIONS", "WRITE_PERMISSIONS"]
        }]
    }
};