package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.CreateBranchParams;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Phase 1 Gate C new storage-only itest (BUILD-PHASE-1.md, Gate 0's proposed list #3):
 * exercises XP's actual branch-creation path -- {@code RepositoryServiceImpl#createBranch}
 * -&gt; {@code NodeStorageServiceImpl#push} -&gt; one {@code storeBranchEntry} call for the
 * root node into a brand-new branch value (verified against the real command/service
 * stack, not the engine directly -- {@code EngineStoreTest#storeIntoNeverSeenBranchAutoVivifiesTheBranchRow}
 * in the nodb build already covers the engine layer). XP has no bulk branch-copy
 * operation: "creating" a branch, from XP's point of view, is just this one write
 * succeeding without a prior explicit create-branch RPC/call -- exactly the auto-vivify
 * behavior {@code BranchStore.store} implements for nodb (see its javadoc) to match ES's
 * implicit-branch semantics (a "branch" was never a first-class entity there either).
 * Pure storage: no query, no search index touched.
 */
class CreateBranchStorageTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    @Test
    void firstWriteIntoANeverSeenBranchSucceedsWithoutAnExplicitCreateBranchCall()
    {
        final Branch neverSeenBranch = Branch.from( "never-seen-in-this-test" );

        // createBranch requireAdminRole()s and reads the repositoryId off the ambient
        // context (not a param) -- ctxDefaultAdmin() carries testRepoId with admin rights.
        assertDoesNotThrow(
            () -> ctxDefaultAdmin().callWith( () -> repositoryService.createBranch( CreateBranchParams.from( neverSeenBranch ) ) ) );

        final Node written = ContextBuilder.from( ctxDefault() )
            .branch( neverSeenBranch )
            .build()
            .callWith( () -> createNode(
                CreateNodeParams.create().parent( NodePath.ROOT ).name( "first-write" ).data( new PropertyTree() ).build() ) );

        assertNotNull( written );

        final Node fetchedInNewBranch = ContextBuilder.from( ctxDefault() )
            .branch( neverSeenBranch )
            .build()
            .callWith( () -> getNodeById( written.id() ) );

        assertNotNull( fetchedInNewBranch, "the node written into the freshly-created branch must be readable back from it" );
    }
}
