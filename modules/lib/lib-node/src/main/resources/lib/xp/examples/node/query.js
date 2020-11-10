var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// BEGIN
// Query content using aggregations.
var result = repo.query({
    start: 0,
    count: 2,
    query: 'startTime > instant(\'2016-10-11T14:38:54.454Z\')',
    filters: {
        boolean: {
            must: [
                {
                    exists: {
                        field: 'modifiedTime'
                    }
                },
                {
                    exists: {
                        field: 'other'
                    }
                }
            ],
            mustNot: {
                hasValue: {
                    field: 'myField',
                    values: [
                        'cheese',
                        'fish',
                        'onion'
                    ]
                }
            }
        },
        notExists: {
            field: 'unwantedField'
        },
        ids: {
            values: ['id1', 'id2']
        }
    },
    sort: 'duration DESC',
    aggregations: {
        urls: {
            terms: {
                field: 'url',
                order: '_count desc',
                size: 2
            },
            aggregations: {
                duration: {
                    histogram: {
                        field: 'duration',
                        interval: parseInt('100', 10),
                        minDocCount: 1,
                        extendedBoundMin: 0,
                        extendedBoundMax: 10000,
                        order: '_key desc'
                    }
                },
                durationStats: {
                    stats: {
                        field: 'duration'
                    }
                }
            }
        }
    },
    highlight: {
        encoder: 'html',
        fragmenter: 'simple',
        fragmenterSize: parseInt('5', 10),
        noMatchSize: 5,
        numberOfFragments: 5,
        order: 'score',
        preTag: '<a>',
        postTag: '<b>',
        requireFieldMatch: false,
        tagsSchema: 'styled',
        properties: {
            displayName: {
                fragmenter: 'span',
                fragmenterSize: 6,
                noMatchSize: parseInt('6', 10),
                numberOfFragments: 6,
                order: 'none',
                preTag: '<c>',
                postTag: '<d>',
                requireFieldMatch: true
            }
        }
    }
});

log.info('Found ' + result.total + ' number of contents');

for (var i = 0; i < result.hits.length; i++) {
    var node = result.hits[i];
    log.info('Node ' + node.id + ' found');
}
// END

// BEGIN
// Result set returned.
var expected = {
    'total': 12902,
    'count': 2,
    'hits': [
        {
            'id': 'b186d24f-ac38-42ca-a6db-1c1bda6c6c26',
            'score': 1.2300000190734863,
            'highlight': {
                'property1': [
                    'fragment1',
                    'fragment2'
                ]
            }
        },
        {
            'id': '350ba4a6-589c-498b-8af0-f183850e1120',
            'score': 1.399999976158142
        }
    ],
    'aggregations': {
        'urls': {
            'buckets': [
                {
                    'key': '/site/draft/superhero/search',
                    'docCount': 6762,
                    'duration': {
                        'buckets': [
                            {
                                'key': '1600',
                                'docCount': 2
                            },
                            {
                                'key': '1400',
                                'docCount': 1
                            },
                            {
                                'key': '1300',
                                'docCount': 5
                            }
                        ]
                    }
                },
                {
                    'key': '/site/draft/superhero',
                    'docCount': 1245,
                    'duration': {
                        'buckets': [
                            {
                                'key': '1600',
                                'docCount': 2
                            },
                            {
                                'key': '1400',
                                'docCount': 1
                            },
                            {
                                'key': '1300',
                                'docCount': 5
                            }
                        ]
                    }
                }
            ]
        }
    },
    'suggestions': {
        'termSuggestion': [
            {
                'text': 'text1',
                'length': 2,
                'offset': 1,
                'options': [
                    {
                        'text': 'text1-1',
                        'score': 1,
                        'freq': 2
                    },
                    {
                        'text': 'text1-2',
                        'score': 4,
                        'freq': 5
                    }
                ]
            },
            {
                'text': 'text2',
                'length': 2,
                'offset': 2,
                'options': [
                    {
                        'text': 'text2-1',
                        'score': 2.299999952316284,
                        'freq': 4
                    },
                    {
                        'text': 'text2-2',
                        'score': 1,
                        'freq': 2
                    }
                ]
            }
        ]
    }
};
// END

assert.assertJsonEquals(expected, result);
