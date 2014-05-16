
# Karaf OSGi Runtime

Every module in WEM is a valid OSGi bundle. To startup the system you will need to first
install Apache Karaf 4.0.0-SNAPSHOT. Download Apache Karaf here:

* [Apache Karaf 4.0.0-SNAPSHOT](http://repository.apache.org/content/groups/snapshots-group/org/apache/karaf/apache-karaf/4.0.0-SNAPSHOT).

After downloading, unpack onto your system.

## Starting Karaf

Start Karaf by running the karaf shell script (or bat file).

    cd <KARAF_HOME>
    ./bin/karaf

After starting Karaf you will enter the Karaf shell.

## Installing WEM feature repository

Before starting up WEM we need to install a feature repository. This is just an
xml file that defines a set of features.

    feature:repo-add mvn:com.enonic.wem/wem-karaf/5.0.0-SNAPSHOT/xml/features

## Installing a WEM feature

To install a WEM feature, issue the install feature command:

    feature:install wem

To list all avaliable features, just run the following command:

    feature:list


