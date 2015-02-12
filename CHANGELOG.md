## GA (unreleased)

Bugfixes:

 - Fixed JavaScript require() function to search in /cms/lib/ for paths that are not absolute or local, instead of /lib/ .

Features:

 - N/A

Refactoring:

 - N/A


## RC5 (2015-02-11)

Bugfixes:

 - Text Component - Improved initial mode when starting to edit (CMS-5043)
 - Text Component - Editor selection hangs up (CMS-5059)
 - Text Component - When in edit mode context window should always hide (CMS-5075)
 - Enforced "allow-children" constraints from content types. (CMS-5050)
 - Fixed issue updating references when duplicating content. (CMS-5141)

Features:

  - Fields (elements) "is-abstract", "is-final" and "allow-child-content" in content-type.xml made optional. The "is-built-in" field has been removed from content-type.xml. (CMS-5064)
  - Fields "immutable" and "indexed from input-type in xml are made optional. (CMS-4878)
  - Show available apps in home screen depending on user permissions. (CMS-5018)
  - Automatically set owner to the user creating the content. (CMS-5074)
  - Live Edit - Lock page by default (CMS-4827)
  - Added basic caching headers for assets in Live mode rendering. (CMS-5111)
  - Text component - set "section" to root element (CMS-5125)
  - Filtered content types in Create Content dialogue based on site-hierarchy (CMS-5109)
  - Automatic injection of component data attributes for layout, part, image and text components rendering. (CMS-5110)
  - Media Types - Change FormIcon to use ContentResource.updateThumbnail instead of BlobResource.upload (CMS-4677)
  - New OSGi launcher (Karaf replacement). (CMS-4882)
  - Moved to Gradle as build system. (CMS-4882)

Refactoring:

  - Removed menu-item meta step Mixin from built-in types. (CMS-5045)
  - Removed built-in relationship types "system:like" and "system:link". Renamed "system:default" to "system:reference". (CMS-5053)
  - Updated directory structure for modules. Added "cms" folder as parent for types, components and assets. (CMS-5047)
  - Renamed "meta" property in Content API to "x", for eXtra data. (CMS-5134)
  - Renamed <metadata> tag in content-type.xml to <x-data>. Also renamed <meta-step> in module.xml to <x-data> and removed <content> wrapper tag. (CMS-5134)
  - Replaced <mixin-ref> and child tag <reference> in content-type.xml, with a single <inline mixin="..."/> tag. (CMS-4796)

## RC4 (2015-02-03)

Bugfixes:

 - Client (TypeScript) parsed Property values with empty string as null instead of "", causing the values to be changed from "" to null if saved (CMS-4910)

Features:

  - Mixin names in meta steps are referenced just with the local name, the module prefix is omitted. (CMS-5045)

Refactoring:

  - Removed usage of blueprint in core and admin. (CMS-5065)


## RC3 (2015-01-28)

Bugfixes:

 - When removing a FormItem occurrence (Input or FormItemSet) that was not the last one
   then an error occurred when saving content (CMS-4910)
 - Java type conversion with LOCAL_DATE_TIME_FORMATTER and LOCAL_TIME_FORMATTER was using wrong
   hour format (1-24 instead of 0-23) (CMS-4913)
 - Fixed styling when selecting controller for page template (CMS-4913)
 - InputType ContentTypeFilter lists selected options in wrong order causing the wrong content type to be removed when removing (CMS-5008)

Features:

  - Simplify property-tree values in portal json result. Empty arrays is transformed to nul. Single value arrays
    is transformed to a single value. (CMS-4924)
  - Support id parameter for pageUrl. (CMS-4950)
  - Better componentUrl handling. Support "this" component and component to other pages. (CMS-4995)
  - Improvement to imageUrl function. Allow id and path to image. (CMS-5002)
  - Improvement to attachmentUrl function. Resolve label to name and allow id/path to content. (CMS-4951)
  - Resolving schema names to current module when module prefix is missing. (CMS-4077)

Refactoring:

  - Removed general create URL method. Use pageUrl instead. (CMS-4979)
  - Refactor attachment endpoint to support new pattern. (CMS-4963)


## RC2 (2015-01-20)

Bugfixes:

  - Detect if user is authenticated when loading admin home screen (CMS-4880)
  - Page template regions where stored as null instead of being an empty collection (CMS-4837)
  - Page template created in Admin Console where not configured with regions from selected page
    descriptor. Making it also impossible to add components (CMS-4837)
  - Included properties metadata and attachments in Content.equals (TypeScript) (CMS-4837)
  - Made the ImageSelector input type work in Live Edit (CMS-4922)
  - Content Manager grid not updated correctly after uploading of Media files (CMS-4859)
  - When creating a site, anything you enter for module config does not save (CMS-4921)

Features:

  - Added support for detecting any changes (observer pattern) in a PropertyTree (TypeScript) (CMS-4837)
  - Added support for detecting any changes (observer pattern) in PageRegions, LayoutRegions,
    Region and Component's (CMS-4837)
  - PageModel is now able to detect any changes in the PageRegions object and can then switch mode
    to "Forced Template" upon any changes (CMS-4837)
  - Added language and owner fields to Content
  - Cleanup application selector by removing unused applications (CMS-4730)

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
  - Move the common code to the parent classes for tab menu (CMS-4874)


## RC1 (2015-01-13)

Bugfixes:

  - Too many to list here.

Features:

  - Too many to list here.

Refactoring:

  - Too many to list here.

