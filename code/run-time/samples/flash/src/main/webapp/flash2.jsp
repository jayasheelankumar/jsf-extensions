<!--
 The contents of this file are subject to the terms
 of the Common Development and Distribution License
 (the License). You may not use this file except in
 compliance with the License.
 
 You can obtain a copy of the License at
 https://javaserverfaces.dev.java.net/CDDL.html or
 legal/CDDLv1.0.txt. 
 See the License for the specific language governing
 permission and limitations under the License.
 
 When distributing Covered Code, include this CDDL
 Header Notice in each file and include the License file
 at legal/CDDLv1.0.txt.    
 If applicable, add the following below the CDDL Header,
 with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"
 
 [Name of File] [ver.__] [Date]
 
 Copyright 2005 Sun Microsystems Inc. All Rights Reserved
-->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>RoR Flash Test Page 2</title>
    <%@ taglib uri="http://java.sun.com/jsf/core"  prefix="f" %>
    <%@ taglib uri="http://java.sun.com/jsf/html"  prefix="h" %>
    <%@ taglib prefix="jsfExt" uri="http://java.sun.com/jsf/extensions/flash" %>

    <link rel="stylesheet" href="css/default_developer.css" />
    <link rel="stylesheet" href="css/default.css" />
    <link rel="stylesheet" href="css/homepage.css" />    
  </head>

  <body>
    <h1>RoR Flash Test Page 2</h1>

<f:view>

  <p>As mentioned in the previous page, if I wanted to store something
  in the flash during this request and also access it during this
  request, <code>\#{flash.now.bar}</code> is the way to do it.  In
  reality, this just puts the value in request scope, but that's what
  "now" is, anyway.</p>

  <h:form id="form1">

  <h:panelGrid columns="2" border="1">

    Value of the previous request's foo

    <h:outputText value="#{flash.foo}" />

    Put <code>barValue</code> in the flash.now under key
    <code>bar</code>.

    <jsfExt:set var="#{flash.now.bar}" value="barValue" />

    <f:verbatim>
      &lt;jsfExt:set var="\#{flash.now.bar}" value="barValue" /&gt;
    </f:verbatim>

    Value of <code>\#{flash.now.bar}</code>, should be <code>barValue</code>.

    <h:outputText value="#{flash.now.bar}" />

    <h:commandButton value="reload" />

    <h:commandButton value="back" action="back" />

    &nbsp;

    <h:commandButton value="next" action="next" />

   </h:panelGrid>

  </h:form>

</f:view>

    <hr />

    <address><a href="mailto:ed.burns@sun.com">Edward Burns</a></address>
<!-- Created: Sun Dec  4 14:11:55 EST 2005 -->
<!-- hhmts start -->
Last modified: Wed Nov  8 15:39:29 EST 2006
<!-- hhmts end -->
  </body>
</html>
