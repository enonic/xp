(function testJava() {
     var GraalJavaObjectType = Java.type('com.enonic.xp.script.impl.GraalJavaObject');

     var bean = new GraalJavaObjectType();
     console.log(bean.sayHi())
 })