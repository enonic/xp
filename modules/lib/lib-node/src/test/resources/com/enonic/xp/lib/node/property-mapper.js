
exports.double = function () {
    return {
        myDouble: 21.0
    }
};

exports.object = function () {
    return {a: 1, b: 2};
};

exports.array = function () {
    return {myArray: ["one", "two", "three"]};
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