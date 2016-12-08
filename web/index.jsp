<%@ page import="com.microsoft.azure.management.appservice.samples.StorageConnector" %>
<%--
  Copyright (c) Microsoft Corporation. All rights reserved.
  Licensed under the MIT License. See License.txt in the project root for
  license information.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Storage Blob Explorer</title>
  </head>
  <body>
  <div>
    <%= StorageConnector.traverseBlobs() %>
  </div>
  </body>
</html>
