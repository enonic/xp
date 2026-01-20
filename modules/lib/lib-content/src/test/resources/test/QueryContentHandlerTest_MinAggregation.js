var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

var expectedJson = {
    'total': 5,
    'count': 5,
    'hits': [
        {
            '_id': 'id1',
            '_name': 'name1',
            '_path': '/a/b/name1',
            'creator': 'user:system:admin',
            'modifier': 'user:system:admin',
            'createdTime': '1970-01-01T00:00:00Z',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'type': 'base:unstructured',
            'displayName': 'My Content 1',
            'valid': false,
            'data': {
                'category': 'books',
                'productName': 'product 1',
                'price': 10.0
            },
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY'
            }
        },
        {
            '_id': 'id2',
            '_name': 'name2',
            '_path': '/a/b/name2',
            'creator': 'user:system:admin',
            'modifier': 'user:system:admin',
            'createdTime': '1970-01-01T00:00:00Z',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'type': 'base:unstructured',
            'displayName': 'My Content 2',
            'valid': false,
            'data': {
                'category': 'books',
                'productName': 'product 2',
                'price': 20.0
            },
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY'
            }
        },
        {
            '_id': 'id3',
            '_name': 'name3',
            '_path': '/a/b/name3',
            'creator': 'user:system:admin',
            'modifier': 'user:system:admin',
            'createdTime': '1970-01-01T00:00:00Z',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'type': 'base:unstructured',
            'displayName': 'My Content 3',
            'valid': false,
            'data': {
                'category': 'books',
                'productName': 'product 3',
                'price': 30.0
            },
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY'
            }
        },
        {
            '_id': 'id4',
            '_name': 'name4',
            '_path': '/a/b/name4',
            'creator': 'user:system:admin',
            'modifier': 'user:system:admin',
            'createdTime': '1970-01-01T00:00:00Z',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'type': 'base:unstructured',
            'displayName': 'My Content 4',
            'valid': false,
            'data': {
                'category': 'books',
                'productName': 'product 4',
                'price': 40.0
            },
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY'
            }
        },
        {
            '_id': 'id5',
            '_name': 'name5',
            '_path': '/a/b/name5',
            'creator': 'user:system:admin',
            'modifier': 'user:system:admin',
            'createdTime': '1970-01-01T00:00:00Z',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'type': 'base:unstructured',
            'displayName': 'My Content 5',
            'valid': false,
            'data': {
                'category': 'books',
                'productName': 'product 5',
                'price': 50.0
            },
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY'
            }
        }
    ],
    'aggregations': {
        'products': {
            'buckets': [
                {
                    'key': 'books',
                    'docCount': 5,
                    'minPrice': {
                        'value': 10.0
                    }
                }
            ]
        }
    }
};

exports.queryWithAggregations = function () {
    var result = content.query({
        'start': 0,
        'count': 10,
        'query': 'type = \'com.app:Product\'',
        'aggregations': {
            'products': {
                'terms': {
                    'field': 'data.category',
                    'order': '_count asc',
                    'size': 10
                },
                'aggregations': {
                    'minPrice': {
                        'min': {
                            'field': 'data.price'
                        }
                    }
                }
            }
        }
    });

    assert.assertJsonEquals(expectedJson, result);
};
