<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Spring4 MVC -HelloWorld</title>
</head>
<body>

<table>
    <c:forEach items="${result}" var="res">
        <tr>
            <td>
                <c:out value="${res.name}"/>
            </td>
        </tr>
    </c:forEach>

    <h1>hello: ${name}</h1>
</table>
</body>
</html>