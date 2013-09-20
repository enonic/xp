<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>


{
  "components": [


    <%
      // Layout

      if ( "2".equals( request.getParameter( "componentType" ) ) )
      {
    %>

    {
      "key": "10017",
      "type": 2,
      "typeName": "layout",
      "name": "2+1 Column Layout",
      "subtitle": "The quick, brown fox jumps over a lazy dog",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"

    },
    {
      "key": "10016",
      "type": 2,
      "typeName": "layout",
      "name": "2 Column Layout",
      "subtitle": "Even the all-powerful Pointing has no control",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10018",
      "type": 2,
      "typeName": "layout",
      "name": "3 Column Layout",
      "subtitle": "Far far away, behind the word mountains",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    }


    <%
      }
    %>


    <%
      // Part

      if ( "3".equals( request.getParameter( "componentType" ) ) )
      {
    %>

    {
      "key": "10060",
      "type": 3,
      "typeName": "part",
      "name": "Jumpy Box",
      "subtitle": "A box where the computed height is recalculated each second",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10001",
      "type": 3,
      "typeName": "part",
      "name": "HTML 5 Video",
      "subtitle": "Separated they live in Bookmarksgrove right at the coast of the Semantics",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10011",
      "type": 3,
      "typeName": "part",
      "name": "Banner",
      "subtitle": "A un Angleso it va semblar un simplificat",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10004",
      "type": 3,
      "typeName": "part",
      "name": "Contact Form",
      "subtitle": "Far far away, behind the word mountains",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10012",
      "type": 3,
      "typeName": "part",
      "name": "Image Gallery",
      "subtitle": "Even the all-powerful Pointing has no control",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10020",
      "type": 3,
      "typeName": "part",
      "name": "Product - Show",
      "subtitle": "Lorem Ipsum decided to leave for the far World of Grammar",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10021",
      "type": 3,
      "typeName": "part",
      "name": "Products - Related",
      "subtitle": "Separated they live in Bookmarksgrove right at the coast of the Semantics",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10022",
      "type": 3,
      "typeName": "part",
      "name": "Trampoline - Show Variants",
      "subtitle": "Quick wafting zephyrs vex bold Jim",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10023",
      "type": "part",
      "name": "Trampoline - Image Gallery",
      "subtitle": "The quick, brown fox jumps over a lazy dog",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10024",
      "type": 3,
      "typeName": "part",
      "name": "Trampoline - Show Description",
      "subtitle": "DJs flock by when MTV ax quiz prog",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10025",
      "type": 3,
      "typeName": "part",
      "name": "Trampoline - Show Accessories",
      "subtitle": "The jay, pig, fox, zebra, and my wolves quack",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10026",
      "type": 3,
      "typeName": "part",
      "name": "Trampoline - Comments",
      "subtitle": "Blowzy red vixens fight for a quick jump",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10027",
      "type": 3,
      "typeName": "part",
      "name": "Upsale Teaser - Big Bounce",
      "subtitle": "Joaquin Phoenix was gazed by MTV for luck",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    }

    <%
      }
    %>


    <%
      // Image

      if ( "4".equals( request.getParameter( "componentType" ) ) )
      {
    %>


    {
      "key": "10070",
      "type": 4,
      "typeName": "image",
      "name": "Test Photo",
      "subtitle": "Sailing on the Nile River",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-image-icon.png"
    }

    <%
      }
    %>


    <%
      // Paragraph

      if ( "5".equals( request.getParameter( "componentType" ) ) )
      {
    %>


    {
      "key": "10007",
      "type": 5,
      "typeName": "paragraph",
      "name": "Lorem Ipsum Paragraph",
      "subtitle": "The quick, brown fox jumps over a lazy dog",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    },
    {
      "key": "10050",
      "type": 5,
      "typeName": "paragraph",
      "name": "æøå",
      "subtitle": "På Værøy, rett ved Røst, bodde en gang en fattig fisker, som hette Isak",
      "icon": "../admin2/apps/content-manager/js/data/context-window/mock-part-icon.png"
    }


    <%
      }
    %>



  ]
}
