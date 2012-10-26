REST Router Module
===========

A Mule module for implementing REST Router whit-in flows. The module includes a custom router that will dispatch the message to the inner message processors as long as the URI matches and the processors are attached to the right HTTP method.

Installation
------------

The module can either be installed for all applications running within the Mule instance or can be setup to be used
for a single application.

*All Applications*

Download the module from the link above and place the resulting jar file in
/lib/user directory of the Mule installation folder.

*Single Application*

To make the module available only to single application then place it in the
lib directory of the application otherwise if using Maven to compile and deploy
your application the following can be done:

Add the connector's maven repo to your pom.xml:

    <repositories>
        <repository>
            <id>mulesoft-snapshots</id>
            <name>MuleForge Snapshot Repository</name>
            <url>https://repository.mulesoft.org/snapshots/</url>
            <layout>default</layout>
        </repository>
    </repositories>

Add the connector as a dependency to your project. This can be done by adding
the following under the dependencies element in the pom.xml file of the
application:

    <dependency>
        <groupId>org.mule.modules</groupId>
        <artifactId>mule-module-rest-router</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

Usage
-----

A REST router is an intercepting message processor that will only execute if the incoming message
contains a _http.request.path_ property that matches the templareUri parameter.

An URI template is a URI-like String that contained variables marked of in
braces ({, }), which can be expanded to produce a URI.

The following is an example URI template:

	/hotels/{hotel}/bookings/{booking}

If the incoming URI matches the template it will extract the variables in it and it will make them available as
properties.

	<rest:router templateUri="http://{userid}.blog.com/comments/{title}/feed">
	    <rest:get>
	        <expression-transformer>
	            <return-argument evaluator="string" expression="Retrieving comment on #[variable:title] for user #[variable:userid]"/>
	        </expression-transformer>
	    </rest:get>
	    <rest:put>
	        <expression-transformer>
	            <return-argument evaluator="string" expression="Creating comment on #[variable:title] for user #[variable:userid]"/>
	        </expression-transformer>
	    </rest:put>
	    <rest:post>
	        <expression-transformer>
	            <return-argument evaluator="string" expression="Updating comment on #[variable:title] for user #[variable:userid]"/>
	        </expression-transformer>
	    </rest:post>
	    <rest:delete>
	        <expression-transformer>
	            <return-argument evaluator="string" expression="Deleting comment on #[variable:title] for user #[variable:userid]"/>
	        </expression-transformer>
	    </rest:delete>
	</rest:router>
