# RION Ops for Java

 - [Introduction](#introduction)
 - [RION Ops Tutorial](#rion-ops-tutorial)
 - [RION](#rion)
 - [What We Mean by Object in RION](#what-we-mean-by-object-in-rion)
 - [RION Field Types](#rion-field-types)
 - [TION - Textual Internet Object Notation](#tion)
 - [RION vs ION](#rion-vs-ion)
 - [Maven Dependency](#maven-dependency)
 - [Version History](#version-history)



<a name="introduction"></a>

# Introduction
RION Ops for Java is a toolkit for reading and writing the compact, fast, binary data format RION.



<a name="rion-ops-tutorial">

# RION Ops Tutorial

This README file only contains an introduction to what RION and RION Ops are. To learn how to use RION Ops,
see the [RION Ops Tutorial](http://tutorials.jenkov.com/rion-ops-java/index.html) .



<a name="rion"></a>

# RION - Raw Internet Object Notation
RION is short for Raw Internet Object Notation. By "Raw" we mean "in its raw form - as encoded in bytes".
RION itself will not be described here.
Here is the [RION Documentation](http://tutorials.jenkov.com/rion/index.html)



<a name="what-we-mean-by-object-in-rion"></a>

# What We Mean by "Object" in RION.
The term "Object" in "Raw Internet Object Notation" has a slightly different meaning from what it means in JSON.
By "object" we mean "any individual piece of data that it makes sense to represent / read / write / exchange individually".
According to that interpretation, a single integer field is an object. A Key + value pair can be interpreted as two objects
even though they logically represent a composite object (key + value). A set of key + value pairs can be considered
separate, small objects even though they logically represent one big object with properties with each property consisting
of a key + value pair.

As you can see in the RION field list above, RION has a special type of field called an "Object" which can contain
nested RION fields. The "Object" field type is a commonly used RION field type, but it is not this field type we
refer to in the name RION. It is totally fine for a RION file or stream to contain multiple, separate non-"Object"
RION fields. This is different from JSON, where a JSON file can only contain either a full JSON object or array.
This is not so for RION. A RION file could contain a single integer RION field, or multiple fields, including an
"Object" field if that makes sense for the given use case.


<a name="rion-field-types"></a>

# RION Field Types
RION contains the following field types:

 - Bytes
 - Boolean
 - Integer, positive
 - Integer, negative
 - Float
 - UTF-8
 - UTF-8 Short
 - UTC date time
 - Array
 - Table
 - Object
 - Key
 - Key Short
 - Extended

The Array, Table and Object field types are composite field types which can contain other RION fields inside them.
Actually, this is also possible with the Bytes field type - although it is intended to contain raw bytes (e.g. a file, audio, video etc.).
The rest of the field types are simple field types which contain a single data value.





<a name="tion"></a>

# TION - Textual Internet Object Notation
We are planning to create a textual version of RION, which we will call TION - Textual Internet Object Notation.
RION is thus the raw form of Nanosai ION (RION), whereas TION is the textual form. The purpose of TION is to
make it easier to read and write RION in a text editor. Simply convert RION to TION and open it in a text editor.
You can then edit the TION data, save it, and convert it back to RION.


<a name="rion-vs-ion"></a>

# RION vs. ION
RION was first released under the name ION, but since its release Amazon has released its own binary data format
named ION. Therefore we have renamed Nanosai ION to RION to clearly distinguish it from Amazon ION. Actually,
Nanosai ION and Amazon ION are similar in structure, but we believe Nanosai ION has a few advantages over Amazon ION,
especially when working with the data in its raw form (binary form). However, going forward we will call Nanosai ION
for RION.



<a name="maven-dependency"></a>

# Maven Dependency

If you want to use RION Ops with Maven, the Maven dependency for RION Ops looks like this:

    <dependency>
        <groupId>com.nanosai</groupId>
        <artifactId>rion-ops</artifactId>
        <version>0.5.2</version>
    </dependency>

Remember to substitute the version with the version of RION Ops you want to use. See the RION Ops version history in
the next section.


<a name="version-history"></a>

# Version History

| Version | Java Version | Change |
|---------|--------------|--------|
| 0.5.2   | Java 8       | First release |

