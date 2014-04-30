
# Karaf OSGi Runtime

Every module in WEM is a valid OSGi bundle. To startup the system you will need to first
install Apache Karaf 3.0.x. Download Apache Karaf here:

* [Apache Karaf 3.0.x](https://karaf.apache.org/index/community/download.html).

After downloading, unpack onto your system.

## Starting Karaf

Start Karaf by running the karaf shell script (or bat file).

    cd <KARAF_HOME>
    ./bin/karaf

After starting Karaf you will enter the Karaf shell.

## Installing WEM feature repository

Before starting up WEM we need to install a feature repository. This is just an
xml file that defines a set of features.

    feature:repo-add mvn:com.enonic.wem.platforms/karaf/5.0.0-SNAPSHOT/xml/features

If the repository has been changed, you can refresh the repository by running the
following command:

    feature:repo-refresh

## Installing a WEM feature

To install a WEM feature, issue the install feature command:

    feature:install wem-core

To list all avaliable features, just run the following command:

    feature:list

We have defined the following set of WEM features:

* wem-api
* wem-core
* wem-portal
* wem-admin

