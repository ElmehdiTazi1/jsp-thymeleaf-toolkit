@echo off
echo Running JSP to Thymeleaf conversion with module system restrictions bypassed...
set MAVEN_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED

rem Run the conversion with debug output
echo Using directory: %CD%
echo JSP directory: %CD%\src\main\resources\jsp
echo Templates output: %CD%\src\main\resources\templates

rem Create output directory if it doesn't exist
if not exist "src\main\resources\templates" mkdir src\main\resources\templates

mvn com.cybernostics:jsp2tl-maven-plugin:convert -Djava.illegal.access=permit -DsrcDirectory="%CD%\src\main\resources\jsp" -DoutputDirectory="%CD%\src\main\resources\templates" -DscriptletHandlingStrategy=HTML_COMMENT

echo Conversion complete. Check the output in src\main\resources\templates
pause