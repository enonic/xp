var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/assert');

// BEGIN
// Creates a repository with default configuration
var result1 = repoLib.create({
    id: 'test-repo'
});

log.info('Repository created with id ' + result1.id);
// END

// BEGIN
// Creates a repository with specific settings
var result2 = repoLib.create({
    id: 'test-repo2',
    settings: {
        definitions: {
            version: {
                settings: {
                    "index": {
                        "number_of_shards": 1,
                        "number_of_replicas": 1
                    },
                    "analysis": {
                        "analyzer": {
                            "keywordlowercase": {
                                "type": "custom",
                                "tokenizer": "keyword",
                                "filter": [
                                    "lowercase"
                                ]
                            }
                        }
                    }
                },
                mapping: {
                    "version": {
                        "_all": {
                            "enabled": false
                        },
                        "_source": {
                            "enabled": true
                        },
                        "date_detection": false,
                        "numeric_detection": false,
                        "properties": {
                            "nodeid": {
                                "type": "string",
                                "store": "true",
                                "index": "not_analyzed"
                            },
                            "versionid": {
                                "type": "string",
                                "store": "true",
                                "index": "not_analyzed"
                            },
                            "timestamp": {
                                "type": "date",
                                "store": "true",
                                "index": "not_analyzed"
                            },
                            "nodepath": {
                                "type": "string",
                                "store": "true",
                                "index": "analyzed",
                                "analyzer": "keywordlowercase"
                            }
                        }
                    }
                }
            },
            branch: {
                settings: {
                    "index": {
                        "number_of_shards": 1,
                        "number_of_replicas": 1
                    },
                    "analysis": {
                        "analyzer": {
                            "keywordlowercase": {
                                "type": "custom",
                                "tokenizer": "keyword",
                                "filter": [
                                    "lowercase"
                                ]
                            }
                        }
                    }
                }
            }
        }
    }
});

log.info('Repository created with id ' + result2.id);
// END

// BEGIN
// First repository created.
var expected1 = {
    "id": "test-repo",
    "branches": [
        "master"
    ],
    settings: {}
};
// END
assert.assertJsonEquals(expected1, result1);

var expected2 = {
    "id": "test-repo2",
    "branches": [
        "master"
    ],
    settings: {
        definitions: {
            version: {
                settings: {
                    "index": {
                        "number_of_shards": 1,
                        "number_of_replicas": 1
                    },
                    "analysis": {
                        "analyzer": {
                            "keywordlowercase": {
                                "type": "custom",
                                "tokenizer": "keyword",
                                "filter": "lowercase"
                            }
                        }
                    }
                },
                mapping: {
                    "version": {
                        "_all": {
                            "enabled": "false" //TODO Should be a boolean. JsonToPropertyTreeTranslator does not treat boolean values
                        },
                        "_source": {
                            "enabled": "true"
                        },
                        "date_detection": "false",
                        "numeric_detection": "false",
                        "properties": {
                            "nodeid": {
                                "type": "string",
                                "store": "true",
                                "index": "not_analyzed"
                            },
                            "versionid": {
                                "type": "string",
                                "store": "true",
                                "index": "not_analyzed"
                            },
                            "timestamp": {
                                "type": "date",
                                "store": "true",
                                "index": "not_analyzed"
                            },
                            "nodepath": {
                                "type": "string",
                                "store": "true",
                                "index": "analyzed",
                                "analyzer": "keywordlowercase"
                            }
                        }
                    }
                }
            },
            branch: {
                settings: {
                    "index": {
                        "number_of_shards": 1,
                        "number_of_replicas": 1
                    },
                    "analysis": {
                        "analyzer": {
                            "keywordlowercase": {
                                "type": "custom",
                                "tokenizer": "keyword",
                                "filter": "lowercase"
                            }
                        }
                    }
                }
            }
        }
    }
};
assert.assertJsonEquals(expected2, result2);