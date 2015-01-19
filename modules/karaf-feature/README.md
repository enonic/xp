
# Karaf OSGi Runtime

Every module in WEM is a valid OSGi bundle. To startup the system you will need to first
install Apache Karaf 3.0.x. Download Apache Karaf here:

* We need a patched version of 3.0.1 to run on Java 8.
* [Apache Karaf 3.0.x](http://repo.enonic.com/public/org/apache/karaf/apache-karaf-java8/3.0.1/apache-karaf-java8-3.0.1.zip).

After downloading, unpack onto your system.

## Starting Karaf

Start Karaf by running the karaf shell script (or bat file).

    cd <KARAF_HOME>
    ./bin/karaf

After starting Karaf you will enter the Karaf shell.

## Installing WEM feature repository

Before starting up WEM we need to install a feature repository. This is just an
xml file that defines a set of features.

    feature:repo-add mvn:com.enonic.xp/karaf-feature/5.0.0-SNAPSHOT/xml/features

If the repository has been changed, you can refresh the repository by running the
following command:

    feature:repo-refresh

## Installing a WEM feature

To install a WEM feature, issue the install feature command:

    feature:install wem

To list all avaliable features, just run the following command:

    feature:list


