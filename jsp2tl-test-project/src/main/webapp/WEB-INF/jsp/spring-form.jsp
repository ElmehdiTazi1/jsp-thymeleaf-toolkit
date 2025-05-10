<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Spring Form Test</title>
    <style>
        .error { color: red; font-weight: bold; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; }
    </style>
</head>
<body>
    <h1><spring:message code="form.title" text="User Registration Form" /></h1>
    
    <spring:url value="/users/register" var="registrationUrl" />
    
    <form:form action="${registrationUrl}" method="post" modelAttribute="user">
        <div class="form-group">
            <form:label path="firstName">First Name:</form:label>
            <form:input path="firstName" cssClass="form-control" />
            <form:errors path="firstName" cssClass="error" />
        </div>
        
        <div class="form-group">
            <form:label path="lastName">Last Name:</form:label>
            <form:input path="lastName" cssClass="form-control" />
            <form:errors path="lastName" cssClass="error" />
        </div>
        
        <div class="form-group">
            <form:label path="email">Email:</form:label>
            <form:input path="email" cssClass="form-control" />
            <form:errors path="email" cssClass="error" />
        </div>
        
        <div class="form-group">
            <form:label path="password">Password:</form:label>
            <form:password path="password" cssClass="form-control" />
            <form:errors path="password" cssClass="error" />
        </div>
        
        <div class="form-group">
            <form:label path="country">Country:</form:label>
            <form:select path="country" cssClass="form-control">
                <form:option value="" label="-- Select Country --" />
                <form:options items="${countries}" itemValue="code" itemLabel="name" />
            </form:select>
            <form:errors path="country" cssClass="error" />
        </div>
        
        <div class="form-group">
            <form:label path="gender">Gender:</form:label>
            <div>
                <form:radiobutton path="gender" value="M" label="Male" />
                <form:radiobutton path="gender" value="F" label="Female" />
                <form:radiobutton path="gender" value="O" label="Other" />
            </div>
            <form:errors path="gender" cssClass="error" />
        </div>
        
        <div class="form-group">
            <form:label path="interests">Interests:</form:label>
            <div>
                <form:checkboxes path="interests" items="${interestList}" />
            </div>
            <form:errors path="interests" cssClass="error" />
        </div>
        
        <div class="form-group">
            <form:label path="receiveNewsletter">Options:</form:label>
            <div>
                <form:checkbox path="receiveNewsletter" label="Receive Newsletter" />
                <form:checkbox path="acceptTerms" label="I accept the terms and conditions" />
            </div>
        </div>
        
        <div class="form-group">
            <form:label path="notes">Additional Notes:</form:label>
            <form:textarea path="notes" rows="5" cols="30" cssClass="form-control" />
        </div>
        
        <div class="form-group">
            <form:hidden path="id" />
            <button type="submit">Register</button>
            <button type="reset">Reset</button>
        </div>
    </form:form>
    
    <div>
        <spring:hasBindErrors name="user">
            <c:if test="${errors.globalErrorCount > 0}">
                <div class="error">
                    <spring:bind path="user.*">
                        <c:forEach items="${status.errorMessages}" var="error">
                            <p>${error}</p>
                        </c:forEach>
                    </spring:bind>
                </div>
            </c:if>
        </spring:hasBindErrors>
    </div>
    
    <p>
        <a href="<spring:url value="/home" />">Back to Home</a>
    </p>
</body>
</html>