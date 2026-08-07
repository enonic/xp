#!/usr/bin/env bash
# smoke.sh — Gate G's live editing-flow smoke, repeatable (nodb/BUILD-PHASE-4.md Gate G).
#
# Drives the running dev-stack (nodb/dev-stack.sh start) through the full Content Studio
# REST flow and the rebuild drill, failing loudly on the first broken step:
#
#   create -> query-your-write (refresh contract) -> update -> markAsReady ->
#   resolvePublishContent -> publish -> fulltext search -> terms aggregation ->
#   version history -> compare -> rebuild drill (drop index at OpenSearch, replay, verify)
#
#   XP_URL=http://localhost:18080 OS_URL=http://localhost:19200 OPS_URL=http://localhost:7701 \
#     nodb/smoke.sh
#
# Requires: curl, jq, a booted stack, su password as in dev-stack.sh (password123).
# The default XP_URL matches dev-stack.sh's off-standard web port (18080) — the stack
# deliberately stays off 8080/4848/2609 so a developer's own sandboxes are unaffected.

set -euo pipefail

XP_URL="${XP_URL:-http://localhost:18080}"
OS_URL="${OS_URL:-http://localhost:19200}"
OPS_URL="${OPS_URL:-http://localhost:7701}"
SU_PASS="${SU_PASS:-password123}"
TENANT="${TENANT:-myxp}"
PROJECT="${PROJECT:-default}"
REPO="com.enonic.cms.${PROJECT}"

CMS="$XP_URL/admin/rest-v2/cs/cms/$PROJECT/content/content"
COOKIES=$(mktemp)
NAME="smoke-$(date +%s)"
H1='Content-Type: application/json'
H2='X-Requested-With: XMLHttpRequest'

pass() { printf '\033[1;32mPASS\033[0m %s\n' "$*"; }
fail() { printf '\033[1;31mFAIL\033[0m %s\n' "$*" >&2; exit 1; }

cs() { # cs <path> <json-body>
  curl -sf -b "$COOKIES" -H "$H1" -H "$H2" -X POST "$CMS/$1" -d "$2"
}

# --- login ---------------------------------------------------------------------
AUTH=$(curl -sf -c "$COOKIES" -X POST "$XP_URL/_/idprovider/system" -H "$H1" \
  -d '{"action":"login","user":"su","password":"'"$SU_PASS"'"}' | jq -r '.authenticated')
[ "$AUTH" = "true" ] || fail "login as su"
pass "login (session)"

# --- project (create if missing) -----------------------------------------------
curl -s -b "$COOKIES" -H "$H1" -H "$H2" -X POST "$XP_URL/admin/rest-v2/cs/project/create" \
  -d '{"name":"'"$PROJECT"'","displayName":"'"$PROJECT"'","readAccess":{"type":"private","principals":[]}}' >/dev/null
sleep 2

# --- create + query-your-write ---------------------------------------------------
ID=$(cs create '{"valid":false,"requireValid":false,"name":"'"$NAME"'","parent":"/","contentType":"base:folder","data":[],"meta":[],"displayName":"Smoke Fjordland '"$NAME"'","workflow":{"state":"IN_PROGRESS","checks":{}}}' | jq -r '.id')
[ -n "$ID" ] && [ "$ID" != "null" ] || fail "create content"
pass "create ($ID)"

HITS=$(cs query '{"queryExpr":"_name = '\'''"$NAME"''\''","contentTypeNames":[],"from":0,"size":10,"expand":"summary"}' | jq -r '.metadata.totalHits')
[ "$HITS" = "1" ] || fail "query-your-write: expected 1 hit immediately after create, got $HITS"
pass "query-your-write (refresh contract: 1 hit immediately after create)"

# --- update, ready, resolve, publish ---------------------------------------------
cs update '{"contentId":"'"$ID"'","contentName":"'"$NAME"'","displayName":"Smoke Fjordland updated '"$NAME"'","data":[],"meta":[],"requireValid":false}' >/dev/null
pass "update"
cs markAsReady '{"contentIds":["'"$ID"'"]}' >/dev/null || true
pass "markAsReady"
RESOLVED=$(cs resolvePublishContent '{"ids":["'"$ID"'"],"excludedIds":[],"excludeChildrenIds":[]}' | jq -r '.requestedContents | length')
[ "$RESOLVED" = "1" ] || fail "resolvePublishContent"
pass "resolvePublishContent"
TASK=$(cs publish '{"ids":["'"$ID"'"],"excludedIds":[],"excludeChildrenIds":[],"message":""}' | jq -r '.taskId')
for _ in $(seq 1 30); do
  STATE=$(curl -sf -b "$COOKIES" -H "$H2" "$XP_URL/admin/rest-v2/cs/tasks/$TASK" | jq -r '.state')
  [ "$STATE" = "FINISHED" ] && break
  [ "$STATE" = "FAILED" ] && fail "publish task failed"
  sleep 1
done
[ "$STATE" = "FINISHED" ] || fail "publish task did not finish"
pass "publish (task $TASK FINISHED)"

# --- fulltext + aggregation + versions + compare ---------------------------------
FT=$(cs query '{"queryExpr":"fulltext('\''_allText'\'', '\''fjordland'\'', '\''AND'\'')","contentTypeNames":[],"from":0,"size":10,"expand":"summary"}' | jq -r '.metadata.totalHits')
[ "$FT" -ge 1 ] || fail "fulltext search"
pass "fulltext (_allText 'fjordland': $FT hit(s))"

AGG=$(cs query '{"queryExpr":"","contentTypeNames":[],"from":0,"size":0,"aggregationQueries":[{"TermsAggregationQuery":{"name":"byType","fieldName":"type","size":10,"orderByDirection":"DESC","orderByType":"DOC_COUNT"}}],"expand":"none"}' | jq -r '[.aggregations[]."BucketAggregation".buckets[]."BucketJson".docCount] | add')
[ -n "$AGG" ] && [ "$AGG" != "null" ] && [ "$AGG" -ge 1 ] || fail "terms aggregation"
pass "aggregation (terms over type: $AGG docs bucketed)"

VERSIONS=$(cs getVersions '{"contentId":"'"$ID"'","size":50}' | jq -r '.contentVersions | length')
[ "$VERSIONS" -ge 3 ] || fail "version history: expected >=3 versions, got $VERSIONS"
pass "version history ($VERSIONS versions)"

DIFF=$(cs compare '{"ids":["'"$ID"'"]}' | jq -c '.compareContentResults[0].diff')
pass "compare (diff after publish: $DIFF)"

# --- rebuild drill ----------------------------------------------------------------
COUNT_BEFORE=$(curl -sf "$OS_URL/$TENANT-$REPO/_count" | jq -r '.count')
FP() { curl -sf "$OS_URL/$TENANT-$REPO/_search" -H "$H1" -d '{"size":10000,"sort":["_id"],"track_total_hits":true}' | jq -cS '[.hits.hits[] | {i:._id, s:._source}]' | shasum -a 256 | awk '{print $1}'; }
FP_BEFORE=$(FP)
for PHYSICAL in $(curl -sf "$OS_URL/_cat/aliases/$TENANT-$REPO?h=index"); do
  curl -sf -X DELETE "$OS_URL/$PHYSICAL" >/dev/null
done
REBUILD=$(curl -sf -X POST "$OPS_URL/admin/rebuild-search-index?tenant=$TENANT&repo=$REPO")
COUNT_AFTER=$(curl -sf "$OS_URL/$TENANT-$REPO/_count" | jq -r '.count')
FP_AFTER=$(FP)
[ "$COUNT_BEFORE" = "$COUNT_AFTER" ] || fail "rebuild drill: doc count $COUNT_BEFORE -> $COUNT_AFTER"
[ "$FP_BEFORE" = "$FP_AFTER" ] || fail "rebuild drill: fingerprint changed"
FT2=$(cs query '{"queryExpr":"fulltext('\''_allText'\'', '\''fjordland'\'', '\''AND'\'')","contentTypeNames":[],"from":0,"size":10,"expand":"summary"}' | jq -r '.metadata.totalHits')
[ "$FT2" = "$FT" ] || fail "rebuild drill: fulltext hits $FT -> $FT2"
pass "rebuild drill ($REBUILD; count $COUNT_AFTER, fingerprint identical, queries answer)"

rm -f "$COOKIES"
printf '\033[1;32mSMOKE GREEN\033[0m — %s\n' "$NAME"
