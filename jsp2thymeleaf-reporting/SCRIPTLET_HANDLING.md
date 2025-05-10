# Scriptlet Handling in JSP2Thymeleaf

## Overview

JSP scriptlets (code enclosed in `<% ... %>` tags) represent one of the biggest challenges when migrating from JSP to Thymeleaf, as Thymeleaf doesn't have a direct equivalent. The JSP2Thymeleaf toolkit provides several strategies for handling scriptlets during conversion, along with detailed reporting on scriptlet usage.

## Scriptlet Handling Strategies

The toolkit supports the following strategies for handling scriptlets:

### 1. HTML Comments (Default)

**Strategy Name:** `HTML_COMMENT`

Scriptlets are converted to HTML comments, preserving the original code for reference:

```html
<!-- Original scriptlet:
<% if (user.isAdmin()) { %>
-->
```

This approach:
- Keeps the original logic visible in the HTML
- Makes it easy to identify where manual conversion is needed
- Is human-readable in any text editor or browser

### 2. Thymeleaf Comments

**Strategy Name:** `THYMELEAF_COMMENT`

Scriptlets are converted to Thymeleaf parser-level comments:

```html
/*[[ Original scriptlet:
<% if (user.isAdmin()) { %>
]]*/
```

This approach:
- Is completely hidden in the rendered output
- Still preserves the original code
- Works well with Thymeleaf's parsing model

### 3. Extraction to External Files

**Strategy Name:** `EXTRACT_TO_FILE`

Scriptlets are extracted to separate files for manual conversion:

```html
<!-- Original scriptlet extracted to: /scriptlets/home_page_admin_check.java -->
```

This approach:
- Keeps the HTML clean
- Creates separate files that can be converted systematically
- Provides a clear workflow for complex scriptlet conversion

### 4. Fail on Scriptlet

**Strategy Name:** `FAIL_ON_SCRIPTLET`

The conversion process will fail with an error when a scriptlet is encountered:

```
ERROR: Scriptlet found in /webapp/user/profile.jsp (line 42)
```

This approach:
- Enforces a strict "no scriptlets allowed" policy
- Is useful for projects with strong standards
- Helps identify all scriptlets before conversion begins

## Configuration

### Maven Plugin

```xml
<plugin>
  <groupId>com.cybernostics</groupId>
  <artifactId>jsp2tl-maven-plugin</artifactId>
  <version>1.0.0</version>
  <configuration>
    <scriptletHandlingStrategy>THYMELEAF_COMMENT</scriptletHandlingStrategy>
    <scriptletExtractionFolder>${project.build.directory}/extracted-scriptlets</scriptletExtractionFolder>
    <!-- Other configuration... -->
  </configuration>
</plugin>
```

### Command Line

```
java -jar jsp2thymeleaf.jar -s src/main/webapp -d target/templates -st EXTRACT_TO_FILE -ef target/scriptlets
```

Where:
- `-st` or `--scriptlet-strategy`: The strategy to use (HTML_COMMENT, THYMELEAF_COMMENT, EXTRACT_TO_FILE, FAIL_ON_SCRIPTLET)
- `-ef` or `--extraction-folder`: The folder where scriptlets will be extracted when using EXTRACT_TO_FILE

## Scriptlet Reporting

The reporting module provides detailed information about scriptlets found during conversion:

### Statistics Collected

- **Total count of scriptlets** found across all files
- **Lines of code** in scriptlets
- **Complexity metrics** (when available)
- **Distribution of scriptlets** across files
- **Handling strategy** used for each scriptlet

### Report Examples

#### HTML Report

The HTML report includes a dedicated section for scriptlet analysis, showing:
- Files with the most scriptlets
- Chart of scriptlet distribution
- Complexity rating
- Recommendations for conversion

#### JSON Report

The JSON report includes scriptlet data in a structured format:

```json
{
  "scriptletSummary": {
    "totalCount": 42,
    "totalLines": 156,
    "filesWithScriptlets": 8,
    "handlingStrategy": "HTML_COMMENT",
    "scriptletDetails": [
      {
        "file": "/WEB-INF/jsp/user/profile.jsp",
        "lineNumber": 24,
        "lines": 5,
        "complexity": "medium"
      },
      // More scriptlet details...
    ]
  }
}
```

#### Summary Report

The summary report includes a section like:

```
Scriptlet Summary:
-----------------
Total scriptlets: 42 (156 lines of code)
Files with scriptlets: 8 (16% of all files)
Largest scriptlet: 28 lines in /WEB-INF/jsp/admin/reports.jsp
Strategy used: HTML_COMMENT

Recommendation: Consider refactoring the large scriptlet in reports.jsp
to use a Thymeleaf service or controller method.
```

## Best Practices for Handling Scriptlets

1. **Start with HTML_COMMENT strategy** for initial conversion
2. **Use reporting to identify complex scriptlets** that need special attention
3. **Extract complex business logic** into service classes
4. **Replace with Thymeleaf equivalents** where possible
5. **Consider multiple passes** with different strategies for different files
6. **Keep extracted scriptlets in version control** for tracking

## Common Thymeleaf Alternatives for Scriptlets

| JSP Scriptlet Pattern | Thymeleaf Alternative |
|-----------------------|----------------------|
| `<% if (condition) { %>` | `th:if="${condition}"` |
| `<% for (Type item : items) { %>` | `th:each="item : ${items}"` |
| `<% String value = service.method(); %>` | Use controller or `th:with="value=${service.method()}"` |
| Complex calculations | Move to controller or use Thymeleaf services |
| Dynamic includes | `th:insert` or `th:replace` with dynamic fragments |

## Example Conversion Cases

### Case 1: Simple Condition

**Original JSP:**
```jsp
<% if (user.isLoggedIn()) { %>
  <span>Welcome, <%= user.getName() %></span>
<% } else { %>
  <a href="login.jsp">Log in</a>
<% } %>
```

**Converted Thymeleaf:**
```html
<span th:if="${user.loggedIn}">Welcome, <span th:text="${user.name}"></span></span>
<a th:unless="${user.loggedIn}" href="login.html">Log in</a>
```

### Case 2: Complex Logic

**Original JSP:**
```jsp
<%
  String result = "";
  for (int i = 0; i < items.size(); i++) {
    if (items.get(i).isActive()) {
      result += items.get(i).getName() + ", ";
    }
  }
  if (result.length() > 2) {
    result = result.substring(0, result.length() - 2);
  }
%>
<p>Active items: <%= result %></p>
```

**Recommended approach:**
1. Extract this logic to a controller method or service
2. Use Thymeleaf to display the result:
```html
<p>Active items: <span th:text="${activeItemsList}"></span></p>
```