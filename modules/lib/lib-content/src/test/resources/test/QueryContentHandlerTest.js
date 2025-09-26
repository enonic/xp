var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

var expectedJson = {
    'total': 20,
    'count': 3,
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
            'data': {},
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY',
                'checks': {}
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
            'data': {},
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY',
                'checks': {}
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
            'data': {},
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY',
                'checks': {}
            }
        }
    ],
    'aggregations': {
        'genders': {
            'buckets': [
                {
                    'key': 'male',
                    'docCount': 10
                },
                {
                    'key': 'female',
                    'docCount': 12
                }
            ]
        },
        'by_month': {
            'buckets': [
                {
                    'key': '2014-01',
                    'docCount': 8
                },
                {
                    'key': '2014-02',
                    'docCount': 10
                },
                {
                    'key': '2014-03',
                    'docCount': 12
                }
            ]
        },
        'price_ranges': {
            'buckets': [
                {
                    'key': 'a',
                    'docCount': 2,
                    'to': 50
                },
                {
                    'key': 'b',
                    'docCount': 4,
                    'from': 50,
                    'to': 100
                },
                {
                    'key': 'c',
                    'docCount': 4,
                    'from': 100,
                }
            ]
        },
        'my_date_range': {
            'buckets': [
                {
                    'key': 'date range bucket key',
                    'docCount': 2,
                    'from': '2014-09-01T00:00:00Z',
                },
                {
                    'docCount': 5,
                    'from': '2014-10-01T00:00:00Z',
                    'to': '2014-09-01T00:00:00Z'
                },
                {
                    'docCount': 7,
                    'to': '2014-11-01T00:00:00Z'
                }
            ]
        },
        'item_count': {
            'count': 5,
            'min': 1,
            'max': 5,
            'avg': 3,
            'sum': 15
        }
    }
};

var expectedEmptyJson = {
    'total': 0,
    'count': 0,
    'hits': [],
    'aggregations': {},
    'highlight': {}
};

exports.query = function () {

    var result = content.query({
        'start': 0,
        'count': 100,
        'sort': '_modifiedTime DESC, geodistance(\'p1\', \'p2\')',
        'query': 'type = \'article\' AND fulltext(\'myField\', \'searching for cheese\', \'AND\') ',
        'contentTypes': [
            'article',
            'comment'
            ],
        'aggregations': {
            'genders': {
                'terms': {
                    'field': 'gender',
                    'order': '_count asc',
                    'size': 2
                    },
                'aggregations': {
                    'prices': {
                        'histogram': {
                            'field': 'price',
                            'interval': 50,
                            'extendedBoundMin': 0,
                            'extendedBoundMax': 500,
                            'minDocCount': 0,
                            'order': '_key desc'
                            }
                        }
                    }
                },
            'by_month': {
                'dateHistogram': {
                    'field': 'init_date',
                    'interval': '1M',
                    'minDocCount': 0,
                    'format': 'MM-yyy'
                    }
                },
            'price_ranges': {
                'range': {
                    'field': 'price',
                    'ranges': [
                        {'to': 50},
                        {'from': 50, 'to': 100},
                        {'from': 100}
                        ]
                    }
                },
            'my_date_range': {
                'dateRange': {
                    'field': 'date',
                    'format': 'MM-yyy',
                    'ranges': [
                        {'to': 'now-10M/M'},
                        {'from': 'now-10M/M'}
                        ]
                    }
                },
            'time_stats': {
                'stats': {
                    'field': 'item_count'
                    }
                }
            },
        'attachments': {}
        }
    );

    assert.assertJsonEquals(expectedJson, result);
};

exports.queryEmpty = function () {
    var result = content.query({});
    assert.assertJsonEquals(expectedEmptyJson, result);
};

