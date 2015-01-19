
## RC2 (unreleased)

Bugfixes:

  - Detect if user is authenticated when loading admin home screen (CMS-4880)
  - Page template regions where stored as null instead of being an empty collection (CMS-4837)
  - Page template created in Admin Console where not configured with regions from selected page
    descriptor. Making it also impossible to add components (CMS-4837)
  - Included properties metadata and attachments in Content.equals (TypeScript) (CMS-4837)

Features:

  - Added support for detecting any changes (observer pattern) in a PropertyTree (TypeScript) (CMS-4837)
  - Added support for detecting any changes (observer pattern) in PageRegions, LayoutRegions,
    Region and Component's (CMS-4837)
  - PageModel is now able to detect any changes in the PageRegions object and can then switch mode
    to "Forced Template" upon any changes (CMS-4837)

Refactoring:

  - Renamed module wem-api to core-api (CMS-4916)
  - Renamed module wem-core to core-impl (CMS-4906)
  - Renamed module wem-export to core-export (CMS-4893)
  - Renamed module wem-script to portal-script (CMS-4896)
  - Renamed module wem-admin to admin-impl (CMS-4914)
  - Renamed module wem-admin-ui to admin-ui (CMS-4915)
  - Renamed module wem-distro to distro (CMS-4916)
  - Renamed maven groupId to com.enonic.xp (CMS-4918)
  - Moved security integration tests into core-security (CMS-4897)
  - Merged wem-jsapi and portal-jslib modules into portal-jslib (CMS-4898)
  - Loading of current PageDescriptor when refreshing config form i PageInspectionPanel was not necessary,
    since PageModel could keep it and make it accessible. (CMS-4837)
  - Made MixinName, Metadata, Attachments, Attachment, AttachmentName implement Equitable (TypeScript)
  - When ContentWizardPanel detects inconsistent data in method layoutPersistedItem: 
    Added more logging which can uncover which parts of the Content that was unequal
  - Converted wem-core to use declarative services (CMS-4905)  
  - "Flattened" PageRegions, LayoutRegions with AbstractRegions and renamed it to Regions
  - Replaced usages of Property.getContentId with getReference and removed Property.getContentId and Value.getContentId
  - Generate java classes from XSD schema at build time (CMS-4917)
      
## RC1 (2015-01-13)

Bugfixes:

  - Too many to list here.

Features:

  - Too many to list here.

Refactoring:

  - Too many to list here.

